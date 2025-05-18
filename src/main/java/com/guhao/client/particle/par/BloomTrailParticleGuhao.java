package com.guhao.client.particle.par;


import com.dfdyz.epicacg.client.render.EpicACGRenderType;
import com.dfdyz.epicacg.client.render.pipeline.PostEffectPipelines;
import com.google.common.collect.Lists;
import com.guhao.client.ParticlePostProcessor;
import com.guhao.init.ParticleType;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.LinkAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.property.ClientAnimationProperties;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.api.client.model.ItemSkin;
import yesman.epicfight.api.client.model.ItemSkins;
import yesman.epicfight.api.utils.math.CubicBezierCurve;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class BloomTrailParticleGuhao extends TextureSheetParticle {
    private final Joint joint;
    private final TrailInfo trailInfo;
    private final StaticAnimation animation;
    private final LivingEntityPatch<?> entitypatch;
    private final List<TrailEdge> invisibleTrailEdges;
    private final List<TrailEdge> visibleTrailEdges;
    private boolean animationEnd;
    private float startEdgeCorrection = 0.0F;
    private float motionFactor = 0f;
    private static final float DISTORTION_INTENSITY = 0.25f; // 基础强度提升
    private static final float COLOR_PULSE_SPEED = 0.8f;     // 颜色变化加速
    private static final float MAX_DISTORTION = 0.45f;       // 最大扭曲值
    private static final float MOTION_FACTOR_MULTIPLIER = 1.5f; // 运动影响系数
    protected BloomTrailParticleGuhao(ClientLevel level, LivingEntityPatch<?> entitypatch, Joint joint, StaticAnimation animation, TrailInfo trailInfo, SpriteSet spriteSet) {
        super(level, 0, 0, 0);

        this.joint = joint;
        this.entitypatch = entitypatch;
        this.animation = animation;
        this.invisibleTrailEdges = Lists.newLinkedList();
        this.visibleTrailEdges = Lists.newLinkedList();
        this.hasPhysics = false;
        this.trailInfo = trailInfo;

        Vec3 entityPos = entitypatch.getOriginal().position();

        float size = (float)Math.max(this.trailInfo.start.length(), this.trailInfo.end.length()) * 2.0F;
        this.setSize(size, size);
        this.move(entityPos.x, entityPos.y + entitypatch.getOriginal().getEyeHeight(), entityPos.z);
        this.setSpriteFromAge(spriteSet);

        Pose prevPose = this.entitypatch.getAnimator().getPose(0);
        Pose middlePose = this.entitypatch.getAnimator().getPose(0.5F);
        Pose currentPose = this.entitypatch.getAnimator().getPose(1);
        Vec3 posOld = this.entitypatch.getOriginal().getPosition(0.0F);
        Vec3 posMid = this.entitypatch.getOriginal().getPosition(0.5F);
        Vec3 posCur = this.entitypatch.getOriginal().getPosition(1.0F);

        OpenMatrix4f prvmodelTf = OpenMatrix4f.createTranslation((float)posOld.x, (float)posOld.y, (float)posOld.z)
                .mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS)
                        .mulBack(this.entitypatch.getModelMatrix(0.0F)));
        OpenMatrix4f middleModelTf = OpenMatrix4f.createTranslation((float)posMid.x, (float)posMid.y, (float)posMid.z)
                .mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS)
                        .mulBack(this.entitypatch.getModelMatrix(0.5F)));
        OpenMatrix4f curModelTf = OpenMatrix4f.createTranslation((float)posCur.x, (float)posCur.y, (float)posCur.z)
                .mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS)
                        .mulBack(this.entitypatch.getModelMatrix(1.0F)));

        OpenMatrix4f prevJointTf = this.entitypatch.getArmature().getBindedTransformFor(prevPose, this.joint).mulFront(prvmodelTf);
        OpenMatrix4f middleJointTf = this.entitypatch.getArmature().getBindedTransformFor(middlePose, this.joint).mulFront(middleModelTf);
        OpenMatrix4f currentJointTf = this.entitypatch.getArmature().getBindedTransformFor(currentPose, this.joint).mulFront(curModelTf);
        Vec3 prevStartPos = OpenMatrix4f.transform(prevJointTf, trailInfo.start);
        Vec3 prevEndPos = OpenMatrix4f.transform(prevJointTf, trailInfo.end);
        Vec3 middleStartPos = OpenMatrix4f.transform(middleJointTf, trailInfo.start);
        Vec3 middleEndPos = OpenMatrix4f.transform(middleJointTf, trailInfo.end);
        Vec3 currentStartPos = OpenMatrix4f.transform(currentJointTf, trailInfo.start);
        Vec3 currentEndPos = OpenMatrix4f.transform(currentJointTf, trailInfo.end);

        this.invisibleTrailEdges.add(new TrailEdge(prevStartPos, prevEndPos, this.trailInfo.trailLifetime));
        this.invisibleTrailEdges.add(new TrailEdge(middleStartPos, middleEndPos, this.trailInfo.trailLifetime));
        this.invisibleTrailEdges.add(new TrailEdge(currentStartPos, currentEndPos, this.trailInfo.trailLifetime));

        this.rCol = this.trailInfo.rCol;
        this.gCol = this.trailInfo.gCol;
        this.bCol = this.trailInfo.bCol;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if(this.entitypatch.getOriginal().isAlive()){
            Vec3 pos = entitypatch.getOriginal().position();
            this.setPos(pos.x, pos.y, pos.z);
        }

        AnimationPlayer animPlayer = this.entitypatch.getAnimator().getPlayerFor(this.animation);
        this.visibleTrailEdges.removeIf(v -> !v.isAlive());

        if (this.animationEnd) {
            if (this.lifetime-- == 0) {
                this.remove();
            }
        } else {
            if (!this.entitypatch.getOriginal().isAlive() || this.animation != animPlayer.getAnimation().getRealAnimation() || animPlayer.getElapsedTime() > this.trailInfo.endTime) {
                this.animationEnd = true;
                this.lifetime = this.trailInfo.trailLifetime;
            }
        }

        boolean isTrailInvisible = animPlayer.getAnimation() instanceof LinkAnimation || animPlayer.getElapsedTime() <= this.trailInfo.startTime;
        boolean isFirstTrail = this.visibleTrailEdges.size() == 0;
        boolean needCorrection = (!isTrailInvisible && isFirstTrail);

        if (needCorrection) {
            float startCorrection = Math.max((this.trailInfo.startTime - animPlayer.getPrevElapsedTime()) / (animPlayer.getElapsedTime() - animPlayer.getPrevElapsedTime()), 0.0F);
            this.startEdgeCorrection = this.trailInfo.interpolateCount * 2 * startCorrection;
        }

        TrailInfo trailInfo = this.trailInfo;
        Pose prevPose = this.entitypatch.getAnimator().getPose(0);
        Pose middlePose = this.entitypatch.getAnimator().getPose(0.5F);
        Pose currentPose = this.entitypatch.getAnimator().getPose(1);
        Vec3 posOld = this.entitypatch.getOriginal().getPosition(0.0F);
        Vec3 posMid = this.entitypatch.getOriginal().getPosition(0.5F);
        Vec3 posCur = this.entitypatch.getOriginal().getPosition(1.0F);

        OpenMatrix4f prvmodelTf = OpenMatrix4f.createTranslation((float)posOld.x, (float)posOld.y, (float)posOld.z)
                .mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS)
                        .mulBack(this.entitypatch.getModelMatrix(0.0F)));
        OpenMatrix4f middleModelTf = OpenMatrix4f.createTranslation((float)posMid.x, (float)posMid.y, (float)posMid.z)
                .mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS)
                        .mulBack(this.entitypatch.getModelMatrix(0.5F)));
        OpenMatrix4f curModelTf = OpenMatrix4f.createTranslation((float)posCur.x, (float)posCur.y, (float)posCur.z)
                .mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS)
                        .mulBack(this.entitypatch.getModelMatrix(1.0F)));

        OpenMatrix4f prevJointTf = this.entitypatch.getArmature().getBindedTransformFor(prevPose, this.joint).mulFront(prvmodelTf);
        OpenMatrix4f middleJointTf = this.entitypatch.getArmature().getBindedTransformFor(middlePose, this.joint).mulFront(middleModelTf);
        OpenMatrix4f currentJointTf = this.entitypatch.getArmature().getBindedTransformFor(currentPose, this.joint).mulFront(curModelTf);
        Vec3 prevStartPos = OpenMatrix4f.transform(prevJointTf, trailInfo.start);
        Vec3 prevEndPos = OpenMatrix4f.transform(prevJointTf, trailInfo.end);
        Vec3 middleStartPos = OpenMatrix4f.transform(middleJointTf, trailInfo.start);
        Vec3 middleEndPos = OpenMatrix4f.transform(middleJointTf, trailInfo.end);
        Vec3 currentStartPos = OpenMatrix4f.transform(currentJointTf, trailInfo.start);
        Vec3 currentEndPos = OpenMatrix4f.transform(currentJointTf, trailInfo.end);

        List<Vec3> finalStartPositions;
        List<Vec3> finalEndPositions;
        boolean visibleTrail;

        if (isTrailInvisible) {
            finalStartPositions = Lists.newArrayList();
            finalEndPositions = Lists.newArrayList();
            finalStartPositions.add(prevStartPos);
            finalStartPositions.add(middleStartPos);
            finalEndPositions.add(prevEndPos);
            finalEndPositions.add(middleEndPos);

            this.invisibleTrailEdges.clear();
            visibleTrail = false;
        } else {
            List<Vec3> startPosList = Lists.newArrayList();
            List<Vec3> endPosList = Lists.newArrayList();
            TrailEdge edge1;
            TrailEdge edge2;

            if (isFirstTrail) {
                int lastIdx = this.invisibleTrailEdges.size() - 1;
                edge1 = this.invisibleTrailEdges.get(lastIdx);
                edge2 = new TrailEdge(prevStartPos, prevEndPos, -1);
            } else {
                edge1 = this.visibleTrailEdges.get(this.visibleTrailEdges.size() - (this.trailInfo.interpolateCount / 2 + 1));
                edge2 = this.visibleTrailEdges.get(this.visibleTrailEdges.size() - 1);
                edge2.lifetime++;
            }

            startPosList.add(edge1.start);
            endPosList.add(edge1.end);
            startPosList.add(edge2.start);
            endPosList.add(edge2.end);
            startPosList.add(middleStartPos);
            endPosList.add(middleEndPos);
            startPosList.add(currentStartPos);
            endPosList.add(currentEndPos);

            finalStartPositions = CubicBezierCurve.getBezierInterpolatedPoints(startPosList, 1, 3, this.trailInfo.interpolateCount);
            finalEndPositions = CubicBezierCurve.getBezierInterpolatedPoints(endPosList, 1, 3, this.trailInfo.interpolateCount);

            if (!isFirstTrail) {
                finalStartPositions.remove(0);
                finalEndPositions.remove(0);
            }

            visibleTrail = true;
        }
        for (int i = 0; i < finalStartPositions.size(); i++) {
            Vec3 startPos = finalStartPositions.get(i);
            Vec3 endPos = finalEndPositions.get(i);
            // 计算粒子的数量和间隔
            int particleCount = 1; // 粒子数量
            double interval = 0.5; // 粒子之间的间隔
            // 计算粒子的方向和速度
            Vec3 direction = endPos.subtract(startPos).normalize();
            double speed = 0.8; // 粒子的速度
            for (int j = 0; j < particleCount; j++) {
                Vec3 particlePos = startPos.add(direction.scale(j * interval));
                level.addParticle(EpicFightParticles.BLOOD.get(), true, particlePos.x, particlePos.y, particlePos.z, speed * direction.x, speed * direction.y, speed * direction.z);
                if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, (entitypatch.getOriginal())) >= 1) {
                    // 火！
                    double firespeed = speed * 0.32;
                    level.addParticle(ParticleType.BLOOD_FIRE_FLAME.get(), true, particlePos.x, particlePos.y, particlePos.z, firespeed * direction.x, firespeed * direction.y, firespeed * direction.z);
                }
            }

        }
        this.makeTrailEdges(finalStartPositions, finalEndPositions, visibleTrail ? this.visibleTrailEdges : this.invisibleTrailEdges);
        // 新增：计算运动因子
        Vec3 deltaMovement = entitypatch.getOriginal().getDeltaMovement();
        this.motionFactor = Mth.lerp(0.15f, this.motionFactor,
                (float)deltaMovement.length() * 0.7f);
    }
    @Override
    public void render(@NotNull VertexConsumer vertexConsumer, @NotNull Camera camera, float partialTick) {
        RenderSystem.stencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        RenderSystem.stencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        RenderSystem.stencilMask(0xFF);
        if (this.visibleTrailEdges.isEmpty()) {
            return;
        }

        if(!PostEffectPipelines.isActive() || !this.entitypatch.getOriginal().isAlive()) return;
        EpicACGRenderType.getBloomRenderTypeByTexture(trailInfo.texturePath).callPipeline();
// 新增：动态参数计算
        float time = (level.getGameTime() + partialTick) * 1.2f;
        float dynamicIntensity = DISTORTION_INTENSITY * (0.5f + motionFactor * 1.2f); // 增强速度影响
        dynamicIntensity = Mth.clamp(dynamicIntensity, 0.05f, MAX_DISTORTION);
        float colorPulse = Mth.sin(time * COLOR_PULSE_SPEED) * 0.2f + 0.8f;

        PoseStack poseStack = new PoseStack();
        int light = 15728880;
        this.setupPoseStack(poseStack, camera, partialTick);
        Matrix4f matrix4f = poseStack.last().pose();
        // 新增：深度偏移防止Z-fighting
        poseStack.translate(0, 0, 0.02f);
        int edges = this.visibleTrailEdges.size() - 1;
        boolean startFade = this.visibleTrailEdges.get(0).lifetime == 1;
        boolean endFade = this.visibleTrailEdges.get(edges).lifetime == this.trailInfo.trailLifetime;
        float startEdge = (startFade ? this.trailInfo.interpolateCount * 2 * partialTick : 0.0F) + this.startEdgeCorrection;
        float endEdge = endFade ? Math.min(edges - (this.trailInfo.interpolateCount * 2) * (1.0F - partialTick), edges - 1) : edges - 1;
        float interval = 1.0F / (endEdge - startEdge);
        float fading = 1.0F;

        if (this.animationEnd) {
            fading = Mth.clamp((this.lifetime + (1.0F - partialTick)) / this.trailInfo.trailLifetime, 0.0F, 1.0F);
        }

        float partialStartEdge = interval * (startEdge % 1.0F);
        float from = -partialStartEdge;
        float to = -partialStartEdge + interval;

        // 修改后的顶点处理循环
        for (int i = (int)(startEdge); i < (int)endEdge + 1; i++) {

            TrailEdge e1 = this.visibleTrailEdges.get(i);
            TrailEdge e2 = this.visibleTrailEdges.get(i + 1);

            // 新增：动态顶点扭曲方法
// 修改applyDistortion方法中的计算逻辑
            float finalDynamicIntensity = dynamicIntensity;
            Function<Vec3, Vector4f> applyDistortion = pos -> {
                // 增加Y轴偏移和多层波形叠加
                float timeFactor = time * 1.5f;
                float offsetX = Mth.sin((float) (timeFactor + pos.x * 0.5f)) * finalDynamicIntensity
                        + Mth.cos((float) (timeFactor * 0.8f + pos.z * 0.3f)) * finalDynamicIntensity * 0.6f;

                float offsetY = Mth.sin((float) (timeFactor * 1.2f + pos.x * 0.2f)) * finalDynamicIntensity * 0.4f;

                float offsetZ = Mth.cos((float) (timeFactor * 0.7f + pos.z * 0.4f)) * finalDynamicIntensity
                        - Mth.sin(timeFactor * 0.9f) * finalDynamicIntensity * 0.3f;

                return new Vector4f(
                        (float)pos.x + offsetX,
                        (float)pos.y + offsetY, // 添加Y轴偏移
                        (float)pos.z + offsetZ,
                        1.0F
                ).mul(matrix4f);
            };

            Vector4f pos1 = applyDistortion.apply(e1.start);
            Vector4f pos2 = applyDistortion.apply(e1.end);
            Vector4f pos3 = applyDistortion.apply(e2.end);
            Vector4f pos4 = applyDistortion.apply(e2.start);

            // 动态颜色和透明度
            float edgeFactor = 1.0f - Mth.abs((float)i/edges - 0.5f) * 2.0f;

// 修改颜色计算部分
            float colorVariation = Mth.sin(time * 0.4f) * 0.3f + 0.7f;
            float edgeGlow = (float) (Math.pow(edgeFactor, 3) * 0.4f); // 立方增强边缘效果

            float r = Mth.clamp(this.rCol * colorPulse * 1.2f + edgeGlow, 0, 1);
            float g = Mth.clamp(this.gCol * colorPulse * 1.1f + edgeGlow * 0.8f, 0, 1);
            float b = Mth.clamp(this.bCol * colorPulse + edgeGlow * 0.6f, 0, 1);
            float alphaMod = Mth.sin(time * 0.5f) * 0.1f + 0.9f;
            float alphaFrom = Mth.sqrt(Mth.clamp(from, 0, 1)) * alphaMod;
            float alphaTo = Mth.sqrt(Mth.clamp(to, 0, 1)) * alphaMod;
            Vector4f velocityOffset = new Vector4f(
                    (float)(entitypatch.getOriginal().getDeltaMovement().x * 0.2f),
                    (float)(entitypatch.getOriginal().getDeltaMovement().y * 0.2f),
                    (float)(entitypatch.getOriginal().getDeltaMovement().z * 0.2f),
                    0
            );

            pos1.add(velocityOffset.mul(0.8f - i/(float)edges));
            pos2.add(velocityOffset.mul(0.8f - i/(float)edges));
            pos3.add(velocityOffset.mul(1.2f - i/(float)edges));
            pos4.add(velocityOffset.mul(1.2f - i/(float)edges));
            // 边缘光晕增强
            if (i == (int)startEdge || i == (int)endEdge) {
                r = Math.min(r * 1.2f, 1.0f);
                alphaFrom *= 1.3f;
                alphaTo *= 1.3f;
            }

            // 顶点数据写入
            vertexConsumer.vertex(pos1.x(), pos1.y(), pos1.z())
                    .color(r, g, b, alphaFrom)
                    .uv(from, 1.0F).uv2(light).endVertex();
            vertexConsumer.vertex(pos2.x(), pos2.y(), pos2.z())
                    .color(r, g, b, alphaFrom)
                    .uv(from, 0.0F).uv2(light).endVertex();
            vertexConsumer.vertex(pos3.x(), pos3.y(), pos3.z())
                    .color(r, g, b, alphaTo)
                    .uv(to, 0.0F).uv2(light).endVertex();
            vertexConsumer.vertex(pos4.x(), pos4.y(), pos4.z())
                    .color(r, g, b, alphaTo)
                    .uv(to, 1.0F).uv2(light).endVertex();

            from += interval;
            to += interval;
        }
        ParticlePostProcessor.addParticlePosition(
                (float) this.x,
                (float) (this.y + 0.5), // Y偏移
                (float) this.z,
                this.motionFactor // 强度因子
        );

        RenderSystem.stencilMask(0x00);
    }

    protected void setupPoseStack(PoseStack poseStack, Camera camera, float partialTicks) {
        Vec3 vec3 = camera.getPosition();
        float x = (float)-vec3.x();
        float y = (float)-vec3.y();
        float z = (float)-vec3.z();

        // 添加相机抖动效果
        float cameraShake = motionFactor * 0.003f;
        poseStack.translate(
                x + (level.random.nextFloat() - 0.5f) * cameraShake,
                y + (level.random.nextFloat() - 0.5f) * cameraShake,
                z
        );
    }

    @Override
    public boolean shouldCull() {
        return false;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return EpicACGRenderType.getBloomRenderTypeByTexture(trailInfo.texturePath);
    }



    private void makeTrailEdges(List<Vec3> startPositions, List<Vec3> endPositions, List<TrailEdge> dest) {
        for (int i = 0; i < startPositions.size(); i++) {
            dest.add(new TrailEdge(startPositions.get(i), endPositions.get(i), this.trailInfo.trailLifetime));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            int eid = (int)Double.doubleToRawLongBits(x);
            //int modid = (int)Double.doubleToRawLongBits(y);
            int animid = (int)Double.doubleToRawLongBits(z);
            int jointId = (int)Double.doubleToRawLongBits(xSpeed);
            int idx = (int)Double.doubleToRawLongBits(ySpeed);
            Entity entity = level.getEntity(eid);

            if (entity != null) {
                LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
                StaticAnimation animation =  AnimationManager.getInstance().byId(animid);
                Optional<List<TrailInfo>> trailInfo = animation.getProperty(ClientAnimationProperties.TRAIL_EFFECT);
                TrailInfo result = trailInfo.get().get(idx);

                if (result.hand != null) {
                    ItemStack stack = entitypatch.getOriginal().getItemInHand(result.hand);
                    ItemSkin itemSkin = ItemSkins.getItemSkin(stack.getItem());

                    if (itemSkin != null) {
                        result = itemSkin.trailInfo().overwrite(result);
                    }
                }

                if (entitypatch != null) {
                    return new BloomTrailParticleGuhao(level, entitypatch, entitypatch.getArmature().searchJointById(jointId), animation, result, this.spriteSet);
                }
            }

            return null;
        }
    }

    private static class TrailEdge {
        final Vec3 start;
        final Vec3 end;
        int lifetime;

        public TrailEdge(Vec3 start, Vec3 end, int lifetime) {
            this.start = start;
            this.end = end;
            this.lifetime = lifetime;
        }

        boolean isAlive() {
            return --this.lifetime > 0;
        }
    }
}