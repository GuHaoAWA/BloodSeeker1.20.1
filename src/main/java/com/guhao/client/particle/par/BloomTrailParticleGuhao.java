package com.guhao.client.particle.par;

import com.dfdyz.epicacg.client.render.EpicACGRenderType;
import com.dfdyz.epicacg.client.render.pipeline.PostEffectPipelines;
import com.google.common.collect.Lists;
import com.guhao.init.ParticleType;
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
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class BloomTrailParticleGuhao extends TextureSheetParticle {
    private static final Random RANDOM = new Random();
    private static final float DISTORTION_INTENSITY = 0.4f;
    private static final float MAX_DISTORTION = 0.45f;
    private static final float COLOR_PULSE_SPEED = 0.8f;
    private static final float MOTION_FACTOR_MULTIPLIER = 1.6f;

    private final Joint joint;
    private final TrailInfo trailInfo;
    private final StaticAnimation animation;
    private final LivingEntityPatch<?> entitypatch;
    private final List<TrailEdge> invisibleTrailEdges;
    private final List<TrailEdge> visibleTrailEdges;
    private boolean animationEnd;
    private float startEdgeCorrection = 0.0F;
    private float motionFactor = 0f;
    private float timeOffset = RANDOM.nextFloat() * 100f;

    protected BloomTrailParticleGuhao(ClientLevel level, LivingEntityPatch<?> entitypatch, Joint joint,
                                      StaticAnimation animation, TrailInfo trailInfo, SpriteSet spriteSet) {
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

        initializeInitialTrailEdges();

        this.rCol = this.trailInfo.rCol;
        this.gCol = this.trailInfo.gCol;
        this.bCol = this.trailInfo.bCol;
    }

    private void initializeInitialTrailEdges() {
        Pose prevPose = this.entitypatch.getAnimator().getPose(0);
        Pose middlePose = this.entitypatch.getAnimator().getPose(0.5F);
        Pose currentPose = this.entitypatch.getAnimator().getPose(1);
        Vec3 posOld = this.entitypatch.getOriginal().getPosition(0.0F);
        Vec3 posMid = this.entitypatch.getOriginal().getPosition(0.5F);
        Vec3 posCur = this.entitypatch.getOriginal().getPosition(1.0F);

        OpenMatrix4f prevModelTf = createModelTransform(posOld, 0.0F);
        OpenMatrix4f middleModelTf = createModelTransform(posMid, 0.5F);
        OpenMatrix4f curModelTf = createModelTransform(posCur, 1.0F);

        OpenMatrix4f prevJointTf = getJointTransform(prevPose, prevModelTf);
        OpenMatrix4f middleJointTf = getJointTransform(middlePose, middleModelTf);
        OpenMatrix4f currentJointTf = getJointTransform(currentPose, curModelTf);

        Vec3 prevStartPos = OpenMatrix4f.transform(prevJointTf, trailInfo.start);
        Vec3 prevEndPos = OpenMatrix4f.transform(prevJointTf, trailInfo.end);
        Vec3 middleStartPos = OpenMatrix4f.transform(middleJointTf, trailInfo.start);
        Vec3 middleEndPos = OpenMatrix4f.transform(middleJointTf, trailInfo.end);
        Vec3 currentStartPos = OpenMatrix4f.transform(currentJointTf, trailInfo.start);
        Vec3 currentEndPos = OpenMatrix4f.transform(currentJointTf, trailInfo.end);

        this.invisibleTrailEdges.add(new TrailEdge(prevStartPos, prevEndPos, this.trailInfo.trailLifetime));
        this.invisibleTrailEdges.add(new TrailEdge(middleStartPos, middleEndPos, this.trailInfo.trailLifetime));
        this.invisibleTrailEdges.add(new TrailEdge(currentStartPos, currentEndPos, this.trailInfo.trailLifetime));
    }

    private OpenMatrix4f createModelTransform(Vec3 position, float partialTicks) {
        return OpenMatrix4f.createTranslation((float)position.x, (float)position.y, (float)position.z)
                .mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS)
                        .mulBack(this.entitypatch.getModelMatrix(partialTicks)));
    }

    private OpenMatrix4f getJointTransform(Pose pose, OpenMatrix4f modelTf) {
        return this.entitypatch.getArmature().getBindedTransformFor(pose, this.joint).mulFront(modelTf);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.entitypatch.getOriginal().isAlive()) {
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
            if (!this.entitypatch.getOriginal().isAlive() ||
                    this.animation != animPlayer.getAnimation().getRealAnimation() ||
                    animPlayer.getElapsedTime() > this.trailInfo.endTime) {
                this.animationEnd = true;
                this.lifetime = this.trailInfo.trailLifetime;
            }
        }

        boolean isTrailInvisible = animPlayer.getAnimation() instanceof LinkAnimation ||
                animPlayer.getElapsedTime() <= this.trailInfo.startTime;
        boolean isFirstTrail = this.visibleTrailEdges.isEmpty();
        boolean needCorrection = !isTrailInvisible && isFirstTrail;

        if (needCorrection) {
            float timeDiff = animPlayer.getElapsedTime() - animPlayer.getPrevElapsedTime();
            if (timeDiff > 0) {
                float startCorrection = Math.max((this.trailInfo.startTime - animPlayer.getPrevElapsedTime()) / timeDiff, 0.0F);
                this.startEdgeCorrection = this.trailInfo.interpolateCount * 2 * startCorrection;
            }
        }

        TrailInfo trailInfo = this.trailInfo;
        Pose[] poses = {
                this.entitypatch.getAnimator().getPose(0),
                this.entitypatch.getAnimator().getPose(0.5F),
                this.entitypatch.getAnimator().getPose(1)
        };

        Vec3[] positions = {
                this.entitypatch.getOriginal().getPosition(0.0F),
                this.entitypatch.getOriginal().getPosition(0.5F),
                this.entitypatch.getOriginal().getPosition(1.0F)
        };

        OpenMatrix4f[] modelTransforms = new OpenMatrix4f[3];
        for (int i = 0; i < 3; i++) {
            modelTransforms[i] = createModelTransform(positions[i], i == 0 ? 0.0f : (i == 1 ? 0.5f : 1.0f));
        }

        Vec3[] startPositions = new Vec3[3];
        Vec3[] endPositions = new Vec3[3];
        for (int i = 0; i < 3; i++) {
            OpenMatrix4f jointTf = getJointTransform(poses[i], modelTransforms[i]);
            startPositions[i] = OpenMatrix4f.transform(jointTf, trailInfo.start);
            endPositions[i] = OpenMatrix4f.transform(jointTf, trailInfo.end);
        }

        List<Vec3> finalStartPositions;
        List<Vec3> finalEndPositions;
        boolean visibleTrail;

        if (isTrailInvisible) {
            finalStartPositions = Lists.newArrayList(startPositions[0], startPositions[1]);
            finalEndPositions = Lists.newArrayList(endPositions[0], endPositions[1]);
            this.invisibleTrailEdges.clear();
            visibleTrail = false;
        } else {
            TrailEdge edge1 = isFirstTrail ?
                    this.invisibleTrailEdges.get(this.invisibleTrailEdges.size() - 1) :
                    this.visibleTrailEdges.get(this.visibleTrailEdges.size() - (this.trailInfo.interpolateCount / 2 + 1));

            TrailEdge edge2 = isFirstTrail ?
                    new TrailEdge(startPositions[0], endPositions[0], -1) :
                    this.visibleTrailEdges.get(this.visibleTrailEdges.size() - 1);

            if (!isFirstTrail) {
                edge2.lifetime++;
            }

            List<Vec3> startPosList = Lists.newArrayList(
                    edge1.start, edge2.start, startPositions[1], startPositions[2]
            );

            List<Vec3> endPosList = Lists.newArrayList(
                    edge1.end, edge2.end, endPositions[1], endPositions[2]
            );

            finalStartPositions = CubicBezierCurve.getBezierInterpolatedPoints(
                    startPosList, 1, 3, this.trailInfo.interpolateCount
            );

            finalEndPositions = CubicBezierCurve.getBezierInterpolatedPoints(
                    endPosList, 1, 3, this.trailInfo.interpolateCount
            );

            if (!isFirstTrail) {
                finalStartPositions.remove(0);
                finalEndPositions.remove(0);
            }

            visibleTrail = true;
        }

        spawnAdditionalParticles(finalStartPositions, finalEndPositions);
        this.makeTrailEdges(finalStartPositions, finalEndPositions,
                visibleTrail ? this.visibleTrailEdges : this.invisibleTrailEdges);

        updateMotionFactor();
    }

    private void spawnAdditionalParticles(List<Vec3> startPositions, List<Vec3> endPositions) {
        for (int i = 0; i < startPositions.size(); i++) {
            Vec3 startPos = startPositions.get(i);
            Vec3 endPos = endPositions.get(i);
            Vec3 direction = endPos.subtract(startPos).normalize();
            double speed = 0.8;

            for (int j = 0; j < 1; j++) {
                Vec3 particlePos = startPos.add(direction.scale(j * 0.5));
                level.addParticle(EpicFightParticles.BLOOD.get(), true,
                        particlePos.x, particlePos.y, particlePos.z,
                        speed * direction.x, speed * direction.y, speed * direction.z);

                if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, entitypatch.getOriginal()) >= 1) {
                    double fireSpeed = speed * 0.32;
                    level.addParticle(ParticleType.BLOOD_FIRE_FLAME.get(), true,
                            particlePos.x, particlePos.y, particlePos.z,
                            fireSpeed * direction.x, fireSpeed * direction.y, fireSpeed * direction.z);
                }
            }
        }
    }

    private void updateMotionFactor() {
        Vec3 deltaMovement = entitypatch.getOriginal().getDeltaMovement();
        this.motionFactor = Mth.lerp(0.15f, this.motionFactor,
                (float) deltaMovement.length() * 0.7f);
    }



    private float calculateDynamicIntensity() {
        float intensity = DISTORTION_INTENSITY * (0.5f + motionFactor * MOTION_FACTOR_MULTIPLIER);
        return Mth.clamp(intensity, 0.05f, MAX_DISTORTION);
    }

    private Vector4f applyVertexDistortion(Vec3 position, float time, float intensity, Matrix4f matrix) {
        float timeFactor = time * 1.5f;
        float x = (float) position.x;
        float y = (float) position.y;
        float z = (float) position.z;

        // 多层波形叠加产生更自然的扭曲
        float offsetX = Mth.sin(timeFactor + x * 0.5f) * intensity
                + Mth.cos(timeFactor * 0.8f + z * 0.3f) * intensity * 0.6f;

        float offsetY = Mth.sin(timeFactor * 1.2f + x * 0.2f) * intensity * 0.4f;

        float offsetZ = Mth.cos(timeFactor * 0.7f + z * 0.4f) * intensity
                - Mth.sin(timeFactor * 0.9f) * intensity * 0.3f;

        return new Vector4f(
                x + offsetX,
                y + offsetY,
                z + offsetZ,
                1.0F
        ).mul(matrix);
    }

    private void applyMotionBlur(Vector4f pos1, Vector4f pos2, Vector4f pos3, Vector4f pos4, int index, int totalEdges) {
        Vec3 velocity = entitypatch.getOriginal().getDeltaMovement();
        float velocityFactor = 0.2f * (1.0f - (float) index / totalEdges);

        Vector4f velocityOffset = new Vector4f(
                (float) velocity.x * velocityFactor,
                (float) velocity.y * velocityFactor,
                (float) velocity.z * velocityFactor,
                0
        );

        pos1.add(velocityOffset);
        pos2.add(velocityOffset);
        pos3.add(velocityOffset);
        pos4.add(velocityOffset);
    }

    private float[] calculateVertexColors(float time, int index, int totalEdges, float fading) {
        float colorPulse = Mth.sin(time * COLOR_PULSE_SPEED) * 0.2f + 0.8f;
        float edgeFactor = 1.0f - Mth.abs((float) index / totalEdges - 0.5f) * 2.0f;
        float edgeGlow = (float) Math.pow(edgeFactor, 3) * 0.4f;

        float r = Mth.clamp(this.rCol * colorPulse * 1.2f + edgeGlow, 0, 1);
        float g = Mth.clamp(this.gCol * colorPulse * 1.1f + edgeGlow * 0.8f, 0, 1);
        float b = Mth.clamp(this.bCol * colorPulse + edgeGlow * 0.6f, 0, 1);

        return new float[]{r, g, b};
    }

    private float calculateAlpha(float value, float fading) {
        float alpha = Mth.clamp(value, 0, 1) * fading;
        return Mth.sqrt(alpha); // 使用平方根曲线使透明度变化更自然
    }

    private void renderTrailQuad(VertexConsumer consumer, Vector4f p1, Vector4f p2, Vector4f p3, Vector4f p4,
                                 float[] colors, float uFrom, float uTo, float alphaFrom, float alphaTo) {
        int light = 15728880; // 全亮度

        consumer.vertex(p1.x(), p1.y(), p1.z())
                .color(colors[0], colors[1], colors[2], alphaFrom)
                .uv(uFrom, 1.0F).uv2(light).endVertex();

        consumer.vertex(p2.x(), p2.y(), p2.z())
                .color(colors[0], colors[1], colors[2], alphaFrom)
                .uv(uFrom, 0.0F).uv2(light).endVertex();

        consumer.vertex(p3.x(), p3.y(), p3.z())
                .color(colors[0], colors[1], colors[2], alphaTo)
                .uv(uTo, 0.0F).uv2(light).endVertex();

        consumer.vertex(p4.x(), p4.y(), p4.z())
                .color(colors[0], colors[1], colors[2], alphaTo)
                .uv(uTo, 1.0F).uv2(light).endVertex();
    }

    protected void setupPoseStack(PoseStack poseStack, Camera camera, float partialTicks) {
        Vec3 cameraPos = camera.getPosition();
        float shakeIntensity = motionFactor * 0.003f;
        float shakeX = (RANDOM.nextFloat() - 0.5f) * shakeIntensity;
        float shakeY = (RANDOM.nextFloat() - 0.5f) * shakeIntensity;

        poseStack.translate(
                -cameraPos.x + shakeX,
                -cameraPos.y + shakeY,
                -cameraPos.z
        );
    }

    @Override
    public boolean shouldCull() {
        return false;
    }
//    @Override
//    public @NotNull ParticleRenderType getRenderType() {
//        return GuHaoRenderType.getBloomSpaceRenderTypeByTexture(trailInfo.texturePath);
//    }
@Override
public @NotNull ParticleRenderType getRenderType() {
    return EpicACGRenderType.getBloomRenderTypeByTexture(trailInfo.texturePath);
}

    @Override
    public void render(@NotNull VertexConsumer vertexConsumer, @NotNull Camera camera, float partialTick) {

        if (this.visibleTrailEdges.isEmpty() ||
                !PostEffectPipelines.isActive() ||
                !this.entitypatch.getOriginal().isAlive()) {
            return;
        }
//        updateMotionFactor();
        PoseStack poseStack = new PoseStack();
        setupPoseStack(poseStack, camera, partialTick);
        Matrix4f matrix4f = poseStack.last().pose();

        int edges = this.visibleTrailEdges.size() - 1;
        if (edges <= 0) return;

        boolean startFade = this.visibleTrailEdges.get(0).lifetime == 1;
        boolean endFade = this.visibleTrailEdges.get(edges).lifetime == this.trailInfo.trailLifetime;

        float startEdge = (startFade ? this.trailInfo.interpolateCount * 2 * partialTick : 0.0F) + this.startEdgeCorrection;
        float endEdge = endFade ? Math.min(edges - (this.trailInfo.interpolateCount * 2) * (1.0F - partialTick), edges - 1) : edges - 1;

        if (endEdge <= startEdge) return;

        float interval = 1.0F / (endEdge - startEdge);
        float fading = this.animationEnd ?
                Mth.clamp((this.lifetime + (1.0F - partialTick)) / this.trailInfo.trailLifetime, 0.0F, 1.0F) :
                1.0F;

        float partialStartEdge = interval * (startEdge % 1.0F);
        float from = -partialStartEdge;
        float to = -partialStartEdge + interval;
        // 动态效果参数
        float time = (level.getGameTime() + partialTick + timeOffset) * 1.2f;
        float dynamicIntensity = calculateDynamicIntensity();

        for (int i = (int) startEdge; i < (int) endEdge + 1; i++) {
            if (i >= visibleTrailEdges.size() - 1) break;

            TrailEdge e1 = this.visibleTrailEdges.get(i);
            TrailEdge e2 = this.visibleTrailEdges.get(i + 1);

            Vector4f pos1 = applyVertexDistortion(e1.start, time, dynamicIntensity, matrix4f);
            Vector4f pos2 = applyVertexDistortion(e1.end, time, dynamicIntensity, matrix4f);
            Vector4f pos3 = applyVertexDistortion(e2.end, time, dynamicIntensity, matrix4f);
            Vector4f pos4 = applyVertexDistortion(e2.start, time, dynamicIntensity, matrix4f);

            // 应用运动模糊
            applyMotionBlur(pos1, pos2, pos3, pos4, i, edges);

            // 计算颜色和透明度
            float[] colors = calculateVertexColors(time, i, edges, fading);
            float alphaFrom = calculateAlpha(from, fading);
            float alphaTo = calculateAlpha(to, fading);

            // 增强边缘效果
            if (i == (int) startEdge || i == (int) endEdge) {
                colors[0] = Math.min(colors[0] * 1.2f, 1.0f);
                alphaFrom *= 1.3f;
                alphaTo *= 1.3f;
            }
            // 渲染四边形

            renderTrailQuad(vertexConsumer, pos1, pos2, pos3, pos4, colors, from, to, alphaFrom, alphaTo);


            from += interval;
            to += interval;

        }
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
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            Random random = new Random();
            int eid = (int) Double.doubleToRawLongBits(x);
            int animid = (int) Double.doubleToRawLongBits(z);
            int jointId = (int) Double.doubleToRawLongBits(xSpeed);
            int idx = (int) Double.doubleToRawLongBits(ySpeed);

            Entity entity = level.getEntity(eid);
            if (entity == null) return null;

            LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
            if (entitypatch == null) return null;

            StaticAnimation animation = AnimationManager.getInstance().byId(animid);
            Optional<List<TrailInfo>> trailInfo = animation.getProperty(ClientAnimationProperties.TRAIL_EFFECT);
            if (!trailInfo.isPresent() || idx >= trailInfo.get().size()) return null;

            TrailInfo result = trailInfo.get().get(idx);

            if (result.hand != null) {
                ItemStack stack = entitypatch.getOriginal().getItemInHand(result.hand);
                ItemSkin itemSkin = ItemSkins.getItemSkin(stack.getItem());
                if (itemSkin != null) {
                    result = itemSkin.trailInfo().overwrite(result);
                }
            }
            Particle spaceTrail = new SpaceTrailParticle(level, entitypatch,
                    entitypatch.getArmature().searchJointById(jointId),
                    animation, result, this.spriteSet);

            Particle bloomTrail = new BloomTrailParticleGuhao(level, entitypatch,
                    entitypatch.getArmature().searchJointById(jointId),
                    animation, result, this.spriteSet);
//            if (random.nextBoolean()) return bloomTrail;
            return bloomTrail;


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