package com.guhao.client;


import com.dfdyz.epicacg.client.render.pipeline.PostEffectPipelines;
import com.dfdyz.epicacg.client.render.pipeline.PostParticleRenderType;
import com.dfdyz.epicacg.client.render.targets.ScaledTarget;
import com.dfdyz.epicacg.client.render.targets.TargetManager;
import com.dfdyz.epicacg.config.ClientConfig;
import com.dfdyz.epicacg.registry.PostEffects;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class BloomSpaceParticleRenderType extends PostParticleRenderType {
    private static final Map<ResourceLocation, BloomSpaceParticleRenderType> RENDER_TYPES = new HashMap<>();
    private static int bloomIdx = 0;

    private final int layer;
    private final Pipeline pipeline;

    public BloomSpaceParticleRenderType(ResourceLocation renderTypeID, ResourceLocation tex, int layer) {
        super(renderTypeID, tex);
        this.layer = layer;
        this.pipeline = new Pipeline(renderTypeID);
    }

    public static BloomSpaceParticleRenderType getOrCreate(ResourceLocation texture, int layer) {
        return RENDER_TYPES.computeIfAbsent(texture,
                k -> new BloomSpaceParticleRenderType(
                        new ResourceLocation("epicacg", "bloom_space_" + bloomIdx++),
                        texture,
                        layer
                )
        );
    }

    @Override
    public PostEffectPipelines.Pipeline getPipeline() {
        return pipeline;
    }

    public static class Pipeline extends PostEffectPipelines.Pipeline {
        // 辉光效果资源
        private RenderTarget[] blur;
        private RenderTarget[] blur_;
        private RenderTarget bloomTemp;

        // 空间破碎效果资源
        private RenderTarget spaceTemp;
        private static final ResourceLocation SPACE_TEMP = new ResourceLocation("epicacg", "bloom_space_temp");

        // 气流效果参数
        private float distortionIntensity = 0.25f;
        private float maxDistortion = 0.45f;
        private float motionFactor = 0f;

        public Pipeline(ResourceLocation name) {
            super(name);
        }

        public void setDistortionParams(float intensity, float max) {
            this.distortionIntensity = intensity;
            this.maxDistortion = max;
        }

        public void setMotionFactor(float factor) {
            this.motionFactor = factor;
        }

        void handlePasses(RenderTarget src) {
            // 确保空间破碎目标已初始化
            if (spaceTemp == null) {
                spaceTemp = TargetManager.getTarget(SPACE_TEMP);
            }

            // 获取主渲染目标
            RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();

            // 调整目标大小
            if (spaceTemp.width != mainTarget.width || spaceTemp.height != mainTarget.height) {
                spaceTemp.resize(mainTarget.width, mainTarget.height, Minecraft.ON_OSX);
            }

            // === 空间破碎效果处理 ===
            // 应用空间破碎效果，使用气流强度参数
            PostEffects.space_broken.process(
                    mainTarget,
                    src,
                    spaceTemp
            );

            // === 辉光效果处理 ===
            initBloomTargets();

            // 设置纹理参数
            RenderSystem.texParameter(3553, 10242, 33071);
            RenderSystem.texParameter(3553, 10243, 33071);
            RenderSystem.texParameter(3553, 10240, 9729);
            RenderSystem.texParameter(3553, 10241, 9729);

            // 使用空间破碎结果作为辉光输入
            RenderTarget bloomInput = spaceTemp;

            // 根据配置选择辉光模式
            if (ClientConfig.cfg.BloomMode == 0) {
                // Unity风格辉光处理
                processUnityBloom(bloomInput, mainTarget);
            } else {
                // UE风格辉光处理
                processUEBloom(bloomInput, mainTarget);
            }
        }

        private void processUnityBloom(RenderTarget input, RenderTarget mainTarget) {
            PostEffects.downSampler.process(input, this.blur[0]);
            PostEffects.downSampler.process(this.blur[0], this.blur[1]);
            PostEffects.downSampler.process(this.blur[1], this.blur[2]);
            PostEffects.downSampler.process(this.blur[2], this.blur[3]);
            PostEffects.downSampler.process(this.blur[3], this.blur[4]);

            PostEffects.upSampler.process(this.blur[4], this.blur_[3], this.blur[3]);
            PostEffects.upSampler.process(this.blur_[3], this.blur_[2], this.blur[2]);
            PostEffects.upSampler.process(this.blur_[2], this.blur_[1], this.blur[1]);
            PostEffects.upSampler.process(this.blur_[1], this.blur_[0], this.blur[0]);

            PostEffects.unity_composite.process(this.blur_[0], this.bloomTemp, input, mainTarget);
            PostEffects.blit.process(this.bloomTemp, mainTarget);
        }

        private void processUEBloom(RenderTarget input, RenderTarget mainTarget) {
            PostEffects.blur.process(input, this.blur[0], 1.0F, 0.0F, 3);
            PostEffects.blur.process(this.blur[0], this.blur_[0], 0.0F, 1.0F, 3);
            PostEffects.blur.process(this.blur_[0], this.blur[1], 1.0F, 0.0F, 5);
            PostEffects.blur.process(this.blur[1], this.blur_[1], 1.0F, 0.0F, 5);
            PostEffects.blur.process(this.blur_[1], this.blur[2], 1.0F, 0.0F, 7);
            PostEffects.blur.process(this.blur[2], this.blur_[2], 1.0F, 0.0F, 7);
            PostEffects.blur.process(this.blur_[2], this.blur[3], 1.0F, 0.0F, 9);
            PostEffects.blur.process(this.blur[3], this.blur_[3], 1.0F, 0.0F, 9);

            PostEffects.ue_composite.process(input, this.bloomTemp,
                    this.blur_[0], this.blur_[1], this.blur_[2], this.blur_[3]);

            PostEffects.blit.process(this.bloomTemp, mainTarget);
        }

        // 初始化辉光效果所需渲染目标
        void initBloomTargets() {
            int cnt = 5;
            float scale;

            // 初始化blur数组
            if (this.blur == null) {
                this.blur = new RenderTarget[cnt];
                scale = 1.0F;

                for(int i = 0; i < cnt; ++i) {
                    scale /= 2.0F;
                    this.blur[i] = new ScaledTarget(scale, scale,
                            this.bufferTarget.width, this.bufferTarget.height, false, Minecraft.ON_OSX);
                    initTarget(this.blur[i]);
                }
            }

            // 初始化blur_数组
            if (this.blur_ == null) {
                this.blur_ = new RenderTarget[cnt - 1];
                scale = 1.0F;

                for(int i = 0; i < cnt - 1; ++i) {
                    scale /= 2.0F;
                    this.blur_[i] = new ScaledTarget(scale, scale,
                            this.bufferTarget.width, this.bufferTarget.height, false, Minecraft.ON_OSX);
                    initTarget(this.blur_[i]);
                }
            }

            // 初始化临时目标
            if (this.bloomTemp == null) {
                this.bloomTemp = PostParticleRenderType.createTempTarget(this.bufferTarget);
                initTarget(this.bloomTemp);
            }

            // 调整目标大小
            resizeTargets();
        }

        // 初始化渲染目标通用设置
        private void initTarget(RenderTarget target) {
            target.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            target.clear(Minecraft.ON_OSX);
            if (this.bufferTarget.isStencilEnabled()) {
                target.enableStencil();
            }
        }

        // 调整所有目标大小
        private void resizeTargets() {
            if (this.bloomTemp != null &&
                    (this.bloomTemp.width != this.bufferTarget.width ||
                            this.bloomTemp.height != this.bufferTarget.height)) {

                for(int i = 0; i < this.blur.length; ++i) {
                    if (this.blur[i] != null) {
                        this.blur[i].resize(this.bufferTarget.width, this.bufferTarget.height, Minecraft.ON_OSX);
                    }
                }

                for(int i = 0; i < this.blur_.length; ++i) {
                    if (this.blur_[i] != null) {
                        this.blur_[i].resize(this.bufferTarget.width, this.bufferTarget.height, Minecraft.ON_OSX);
                    }
                }

                this.bloomTemp.resize(this.bufferTarget.width, this.bufferTarget.height, Minecraft.ON_OSX);
            }
        }

        @Override
        public void PostEffectHandler() {
            this.handlePasses(this.bufferTarget);
        }

        @Override
        public void suspend() {
            super.suspend();
            // 释放空间破碎临时目标
            if (spaceTemp != null) {
                TargetManager.ReleaseTarget(SPACE_TEMP);
                spaceTemp = null;
            }
        }
    }
}