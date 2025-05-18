package com.guhao.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.io.IOException;

public class ParticlePostProcessor {
    private static final List<Vector4f> activeParticles = new ArrayList<>();
    private static ShaderInstance distortionShader;
    private static final int MAX_PARTICLES = 10;
    private static final ResourceLocation SHADER_LOCATION =
            new ResourceLocation("guhao", "shaders/post/particle_distortion.json");

    // ========== 着色器注册部分 ==========
    public static void registerShaders(ResourceProvider provider) throws IOException {
        distortionShader = new ShaderInstance(
                provider,
                SHADER_LOCATION,
                DefaultVertexFormat.POSITION
        );
    }

    // ========== 每帧开始前调用 ==========
    public static void beginFrame() {
        activeParticles.clear();
    }

    // ========== 添加粒子位置 ==========
    public static void addParticlePosition(float x, float y, float z, float intensity) {
        if (activeParticles.size() < MAX_PARTICLES) {
            activeParticles.add(new Vector4f(x, y, z, intensity));
        }
    }

    // ========== 应用后处理 ==========
    public static void applyPostEffect(GuiGraphics guiGraphics, float partialTick) {
        if (distortionShader == null || activeParticles.isEmpty()) return;

        Minecraft minecraft = Minecraft.getInstance();
        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();
        PoseStack poseStack = guiGraphics.pose();

        // 保存渲染状态
        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();

        try {


            // 传递动态参数
            distortionShader.safeGetUniform("Time").set(
                    (minecraft.level.getGameTime() + partialTick) % 100000
            );

            distortionShader.safeGetUniform("ScreenSize").set(
                    (float) width,
                    (float) height
            );

            // 传递粒子位置数组
            float[] positions = new float[MAX_PARTICLES * 4];
            for (int i = 0; i < Math.min(MAX_PARTICLES, activeParticles.size()); i++) {
                Vector4f pos = activeParticles.get(i);
                positions[i*4] = pos.x;
                positions[i*4+1] = pos.y;
                positions[i*4+2] = pos.z;
                positions[i*4+3] = pos.w; // 强度值
            }
            distortionShader.safeGetUniform("ParticleData").set(positions);
            distortionShader.safeGetUniform("ParticleCount").set(activeParticles.size());

            // 应用着色器
            distortionShader.apply();
            guiGraphics.blit(SHADER_LOCATION, 0, 0, 0, 0, width, height, width, height);
        } finally {
            distortionShader.clear();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.defaultBlendFunc();
        }
    }
    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        new ResourceLocation("guhao", "particle_distortion"),
                        DefaultVertexFormat.POSITION
                ),
                shader -> distortionShader = shader
        );
    }
}