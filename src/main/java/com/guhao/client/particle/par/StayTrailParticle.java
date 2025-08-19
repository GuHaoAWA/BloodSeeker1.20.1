package com.guhao.client.particle.par;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
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
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.property.ClientAnimationProperties;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.api.client.model.ItemSkin;
import yesman.epicfight.api.client.model.ItemSkins;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.CubicBezierCurve;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.particle.EpicFightParticleRenderTypes;
import yesman.epicfight.client.particle.TrailParticle;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class StayTrailParticle extends TextureSheetParticle {
    protected final Joint joint;
    protected final TrailInfo trailInfo;
    protected final StaticAnimation animation;
    protected final LivingEntityPatch<?> entitypatch;
    protected final List<TrailParticle.TrailEdge> invisibleTrailEdges;
    protected final List<TrailParticle.TrailEdge> visibleTrailEdges;
    protected boolean animationEnd;
    protected float startEdgeCorrection = 0.0F;
    protected float fadeStartTime = 0.7f; // 残影总生命周期的70%后开始淡出
    protected float fadeDuration = 0.3f;  // 淡出过程占30%生命周期
    protected StayTrailParticle(ClientLevel level, LivingEntityPatch<?> entitypatch, Joint joint, StaticAnimation animation, TrailInfo trailInfo, SpriteSet spriteSet) {
        super(level, 0.0, 0.0, 0.0);
        this.joint = joint;
        this.entitypatch = entitypatch;
        this.animation = animation;
        this.invisibleTrailEdges = Lists.newLinkedList();
        this.visibleTrailEdges = Lists.newLinkedList();
        this.hasPhysics = false;
        this.trailInfo = trailInfo;
        Vec3 entityPos = (entitypatch.getOriginal()).position();
        this.move(entityPos.x, entityPos.y + (entitypatch.getOriginal()).getEyeHeight(), entityPos.z);
        float size = (float)Math.max(this.trailInfo.start.length(), this.trailInfo.end.length()) * 2.0F;
        this.setSize(size, size);
        this.setSpriteFromAge(spriteSet);
        Pose prevPose = this.entitypatch.getAnimator().getPose(0.0F);
        Pose middlePose = this.entitypatch.getAnimator().getPose(0.5F);
        Pose currentPose = this.entitypatch.getAnimator().getPose(1.0F);
        Vec3 posOld = (this.entitypatch.getOriginal()).getPosition(0.0F);
        Vec3 posMid = (this.entitypatch.getOriginal()).getPosition(0.5F);
        Vec3 posCur = (this.entitypatch.getOriginal()).getPosition(1.0F);
        OpenMatrix4f prvmodelTf = OpenMatrix4f.createTranslation((float)posOld.x, (float)posOld.y, (float)posOld.z).mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS).mulBack(this.entitypatch.getModelMatrix(0.0F)));
        OpenMatrix4f middleModelTf = OpenMatrix4f.createTranslation((float)posMid.x, (float)posMid.y, (float)posMid.z).mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS).mulBack(this.entitypatch.getModelMatrix(0.5F)));
        OpenMatrix4f curModelTf = OpenMatrix4f.createTranslation((float)posCur.x, (float)posCur.y, (float)posCur.z).mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS).mulBack(this.entitypatch.getModelMatrix(1.0F)));
        OpenMatrix4f prevJointTf = this.entitypatch.getArmature().getBindedTransformFor(prevPose, this.joint).mulFront(prvmodelTf);
        OpenMatrix4f middleJointTf = this.entitypatch.getArmature().getBindedTransformFor(middlePose, this.joint).mulFront(middleModelTf);
        OpenMatrix4f currentJointTf = this.entitypatch.getArmature().getBindedTransformFor(currentPose, this.joint).mulFront(curModelTf);
        Vec3 prevStartPos = OpenMatrix4f.transform(prevJointTf, trailInfo.start);
        Vec3 prevEndPos = OpenMatrix4f.transform(prevJointTf, trailInfo.end);
        Vec3 middleStartPos = OpenMatrix4f.transform(middleJointTf, trailInfo.start);
        Vec3 middleEndPos = OpenMatrix4f.transform(middleJointTf, trailInfo.end);
        Vec3 currentStartPos = OpenMatrix4f.transform(currentJointTf, trailInfo.start);
        Vec3 currentEndPos = OpenMatrix4f.transform(currentJointTf, trailInfo.end);
        this.invisibleTrailEdges.add(new TrailParticle.TrailEdge(prevStartPos, prevEndPos, this.trailInfo.trailLifetime));
        this.invisibleTrailEdges.add(new TrailParticle.TrailEdge(middleStartPos, middleEndPos, this.trailInfo.trailLifetime));
        this.invisibleTrailEdges.add(new TrailParticle.TrailEdge(currentStartPos, currentEndPos, this.trailInfo.trailLifetime));
        this.rCol = Math.max(this.trailInfo.rCol, 0.0F);
        this.gCol = Math.max(this.trailInfo.gCol, 0.0F);
        this.bCol = Math.max(this.trailInfo.bCol, 0.0F);
        if (this.trailInfo.texturePath != null) {
            TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
            AbstractTexture abstracttexture = texturemanager.getTexture(this.trailInfo.texturePath);
            RenderSystem.bindTexture(abstracttexture.getId());
            RenderSystem.texParameter(3553, 10242, 33071);
            RenderSystem.texParameter(3553, 10243, 33071);
        }

    }

    /** @deprecated */
    @Deprecated
    protected StayTrailParticle(Armature armature, LivingEntityPatch<?> entitypatch, Joint joint, StaticAnimation animation, TrailInfo trailInfo) {
        super(null, 0.0, 0.0, 0.0);
        this.entitypatch = entitypatch;
        this.joint = joint;
        this.animation = animation;
        this.invisibleTrailEdges = Lists.newLinkedList();
        this.visibleTrailEdges = Lists.newLinkedList();
        this.hasPhysics = false;
        this.trailInfo = trailInfo;
        float size = (float)Math.max(this.trailInfo.start.length(), this.trailInfo.end.length()) * 2.0F;
        this.setSize(size, size);
        Pose prevPose = this.entitypatch.getAnimator().getPose(0.0F);
        Pose middlePose = this.entitypatch.getAnimator().getPose(0.5F);
        Pose currentPose = this.entitypatch.getAnimator().getPose(1.0F);
        OpenMatrix4f prevJointTf = armature.getBindedTransformFor(prevPose, this.joint);
        OpenMatrix4f middleJointTf = armature.getBindedTransformFor(middlePose, this.joint);
        OpenMatrix4f currentJointTf = armature.getBindedTransformFor(currentPose, this.joint);
        Vec3 prevStartPos = OpenMatrix4f.transform(prevJointTf, trailInfo.start);
        Vec3 prevEndPos = OpenMatrix4f.transform(prevJointTf, trailInfo.end);
        Vec3 middleStartPos = OpenMatrix4f.transform(middleJointTf, trailInfo.start);
        Vec3 middleEndPos = OpenMatrix4f.transform(middleJointTf, trailInfo.end);
        Vec3 currentStartPos = OpenMatrix4f.transform(currentJointTf, trailInfo.start);
        Vec3 currentEndPos = OpenMatrix4f.transform(currentJointTf, trailInfo.end);
        this.invisibleTrailEdges.add(new TrailParticle.TrailEdge(prevStartPos, prevEndPos, this.trailInfo.trailLifetime));
        this.invisibleTrailEdges.add(new TrailParticle.TrailEdge(middleStartPos, middleEndPos, this.trailInfo.trailLifetime));
        this.invisibleTrailEdges.add(new TrailParticle.TrailEdge(currentStartPos, currentEndPos, this.trailInfo.trailLifetime));
        this.rCol = Math.max(this.trailInfo.rCol, 0.0F);
        this.gCol = Math.max(this.trailInfo.gCol, 0.0F);
        this.bCol = Math.max(this.trailInfo.bCol, 0.0F);
        if (this.trailInfo.texturePath != null) {
            TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
            AbstractTexture abstracttexture = texturemanager.getTexture(this.trailInfo.texturePath);
            RenderSystem.bindTexture(abstracttexture.getId());
            RenderSystem.texParameter(3553, 10242, 33071);
            RenderSystem.texParameter(3553, 10243, 33071);
        }

    }

    public void tick() {
        AnimationPlayer animPlayer = this.entitypatch.getAnimator().getPlayerFor(this.animation);
        this.visibleTrailEdges.removeIf((v) -> !v.isAlive());
        if (this.animationEnd) {
            if (this.lifetime-- == 0) {
                this.remove();
                this.animationEnd = true;
            }
        } else if (!this.entitypatch.getOriginal().isAlive() || this.animation != animPlayer.getAnimation().getRealAnimation() || animPlayer.getElapsedTime() > this.trailInfo.endTime) {
            this.animationEnd = true;
            this.lifetime = this.trailInfo.trailLifetime;
        }

        if (!TrailInfo.isValidTime(this.trailInfo.fadeTime) || !(this.trailInfo.endTime < animPlayer.getElapsedTime())) {
            double xd = Math.pow((this.entitypatch.getOriginal()).getX() - (this.entitypatch.getOriginal()).xo, 2.0);
            double yd = Math.pow((this.entitypatch.getOriginal()).getY() - (this.entitypatch.getOriginal()).yo, 2.0);
            double zd = Math.pow((this.entitypatch.getOriginal()).getZ() - (this.entitypatch.getOriginal()).zo, 2.0);
            float move = (float)Math.sqrt(xd + yd + zd) * 2.0F;
            this.setSize(this.bbWidth + move, this.bbHeight + move);
            boolean isTrailInvisible = animPlayer.getAnimation().isLinkAnimation() || animPlayer.getElapsedTime() <= this.trailInfo.startTime;
            boolean isFirstTrail = this.visibleTrailEdges.isEmpty();
            boolean needCorrection = !isTrailInvisible && isFirstTrail;
            if (needCorrection) {
                float startCorrection = Math.max((this.trailInfo.startTime - animPlayer.getPrevElapsedTime()) / (animPlayer.getElapsedTime() - animPlayer.getPrevElapsedTime()), 0.0F);
                this.startEdgeCorrection = (float)(this.trailInfo.interpolateCount * 2) * startCorrection;
            }

            TrailInfo trailInfo = this.trailInfo;
            Pose prevPose = this.entitypatch.getAnimator().getPose(0.0F);
            Pose middlePose = this.entitypatch.getAnimator().getPose(0.5F);
            Pose currentPose = this.entitypatch.getAnimator().getPose(1.0F);
            Vec3 posOld = (this.entitypatch.getOriginal()).getPosition(0.0F);
            Vec3 posMid = (this.entitypatch.getOriginal()).getPosition(0.5F);
            Vec3 posCur = (this.entitypatch.getOriginal()).getPosition(1.0F);
            OpenMatrix4f prvmodelTf = OpenMatrix4f.createTranslation((float)posOld.x, (float)posOld.y, (float)posOld.z).mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS).mulBack(this.entitypatch.getModelMatrix(0.0F)));
            OpenMatrix4f middleModelTf = OpenMatrix4f.createTranslation((float)posMid.x, (float)posMid.y, (float)posMid.z).mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS).mulBack(this.entitypatch.getModelMatrix(0.5F)));
            OpenMatrix4f curModelTf = OpenMatrix4f.createTranslation((float)posCur.x, (float)posCur.y, (float)posCur.z).mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS).mulBack(this.entitypatch.getModelMatrix(1.0F)));
            OpenMatrix4f prevJointTf = this.entitypatch.getArmature().getBindedTransformFor(prevPose, this.joint).mulFront(prvmodelTf);
            OpenMatrix4f middleJointTf = this.entitypatch.getArmature().getBindedTransformFor(middlePose, this.joint).mulFront(middleModelTf);
            OpenMatrix4f currentJointTf = this.entitypatch.getArmature().getBindedTransformFor(currentPose, this.joint).mulFront(curModelTf);
            Vec3 prevStartPos = OpenMatrix4f.transform(prevJointTf, trailInfo.start);
            Vec3 prevEndPos = OpenMatrix4f.transform(prevJointTf, trailInfo.end);
            Vec3 middleStartPos = OpenMatrix4f.transform(middleJointTf, trailInfo.start);
            Vec3 middleEndPos = OpenMatrix4f.transform(middleJointTf, trailInfo.end);
            Vec3 currentStartPos = OpenMatrix4f.transform(currentJointTf, trailInfo.start);
            Vec3 currentEndPos = OpenMatrix4f.transform(currentJointTf, trailInfo.end);
            Object finalStartPositions;
            Object finalEndPositions;
            boolean visibleTrail;
            if (isTrailInvisible) {
                finalStartPositions = Lists.newArrayList();
                finalEndPositions = Lists.newArrayList();
                ((List)finalStartPositions).add(prevStartPos);
                ((List)finalStartPositions).add(middleStartPos);
                ((List)finalEndPositions).add(prevEndPos);
                ((List)finalEndPositions).add(middleEndPos);
                this.invisibleTrailEdges.clear();
                visibleTrail = false;
            } else {
                List<Vec3> startPosList = Lists.newArrayList();
                List<Vec3> endPosList = Lists.newArrayList();
                TrailParticle.TrailEdge edge1;
                TrailParticle.TrailEdge edge2;
                if (isFirstTrail) {
                    int lastIdx = this.invisibleTrailEdges.size() - 1;
                    edge1 = this.invisibleTrailEdges.get(lastIdx);
                    edge2 = new TrailParticle.TrailEdge(prevStartPos, prevEndPos, -1);
                } else {
                    edge1 = this.visibleTrailEdges.get(this.visibleTrailEdges.size() - (this.trailInfo.interpolateCount / 2 + 1));
                    edge2 = this.visibleTrailEdges.get(this.visibleTrailEdges.size() - 1);
                    ++edge2.lifetime;
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
                    ((List)finalStartPositions).remove(0);
                    ((List)finalEndPositions).remove(0);
                }

                visibleTrail = true;
            }

            this.makeTrailEdges((List)finalStartPositions, (List)finalEndPositions, visibleTrail ? this.visibleTrailEdges : this.invisibleTrailEdges);
        }
    }

    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        if (!this.visibleTrailEdges.isEmpty()) {
            PoseStack poseStack = new PoseStack();
            int light = this.getLightColor(partialTick);
            this.setupPoseStack(poseStack, camera, partialTick);
            Matrix4f matrix4f = poseStack.last().pose();
            int edges = this.visibleTrailEdges.size() - 1;
            boolean startFade = this.visibleTrailEdges.get(0).lifetime == 1;
            boolean endFade = this.visibleTrailEdges.get(edges).lifetime == this.trailInfo.trailLifetime;
            float startEdge = (startFade ? (float)(this.trailInfo.interpolateCount * 2) * partialTick : 0.0F) + this.startEdgeCorrection;
            float endEdge = endFade ? Math.min((float)edges - (float)(this.trailInfo.interpolateCount * 2) * (1.0F - partialTick), (float)(edges - 1)) : (float)(edges - 1);
            float interval = 1.0F / (endEdge - startEdge);
            float fading = 1.0F;
            if (this.animationEnd) {
                if (TrailInfo.isValidTime(this.trailInfo.fadeTime)) {
                    fading = (float)this.lifetime / (float)this.trailInfo.trailLifetime;
                } else {
                    fading = Mth.clamp(((float)this.lifetime + (1.0F - partialTick)) / (float)this.trailInfo.trailLifetime, 0.0F, 1.0F);
                }
            }

            float partialStartEdge = interval * (startEdge % 1.0F);
            float from = -partialStartEdge;
            float to = -partialStartEdge + interval;

            for(int i = (int)startEdge; i < (int)endEdge + 1; ++i) {
                TrailParticle.TrailEdge e1 = this.visibleTrailEdges.get(i);
                TrailParticle.TrailEdge e2 = this.visibleTrailEdges.get(i + 1);
                Vector4f pos1 = new Vector4f((float)e1.start.x, (float)e1.start.y, (float)e1.start.z, 1.0F);
                Vector4f pos2 = new Vector4f((float)e1.end.x, (float)e1.end.y, (float)e1.end.z, 1.0F);
                Vector4f pos3 = new Vector4f((float)e2.end.x, (float)e2.end.y, (float)e2.end.z, 1.0F);
                Vector4f pos4 = new Vector4f((float)e2.start.x, (float)e2.start.y, (float)e2.start.z, 1.0F);
                pos1.mul(matrix4f);
                pos2.mul(matrix4f);
                pos3.mul(matrix4f);
                pos4.mul(matrix4f);
                float alphaFrom = Mth.clamp(from, 0.0F, 1.0F);
                float alphaTo = Mth.clamp(to, 0.0F, 1.0F);
                vertexConsumer.vertex(pos1.x(), pos1.y(), pos1.z()).uv(from, 1.0F).color(this.rCol, this.gCol, this.bCol, this.alpha * alphaFrom * fading).uv2(light).endVertex();
                vertexConsumer.vertex(pos2.x(), pos2.y(), pos2.z()).uv(from, 0.0F).color(this.rCol, this.gCol, this.bCol, this.alpha * alphaFrom * fading).uv2(light).endVertex();
                vertexConsumer.vertex(pos3.x(), pos3.y(), pos3.z()).uv(to, 0.0F).color(this.rCol, this.gCol, this.bCol, this.alpha * alphaTo * fading).uv2(light).endVertex();
                vertexConsumer.vertex(pos4.x(), pos4.y(), pos4.z()).uv(to, 1.0F).color(this.rCol, this.gCol, this.bCol, this.alpha * alphaTo * fading).uv2(light).endVertex();
                from += interval;
                to += interval;
            }

        }
    }

    public boolean shouldCull() {
        return false;
    }

    public @NotNull ParticleRenderType getRenderType() {
        return EpicFightParticleRenderTypes.TRAIL_PROVIDER.apply(this.trailInfo.texturePath);
    }

    protected void setupPoseStack(PoseStack poseStack, Camera camera, float partialTicks) {
        Vec3 vec3 = camera.getPosition();
        float x = (float)(-vec3.x());
        float y = (float)(-vec3.y());
        float z = (float)(-vec3.z());
        poseStack.translate(x, y, z);
    }

    protected void makeTrailEdges(List<Vec3> startPositions, List<Vec3> endPositions, List<TrailParticle.TrailEdge> dest) {
        for(int i = 0; i < startPositions.size(); ++i) {
            dest.add(new TrailParticle.TrailEdge((Vec3)startPositions.get(i), (Vec3)endPositions.get(i), this.trailInfo.trailLifetime));
        }

    }

    @OnlyIn(Dist.CLIENT)
    public static class TrailEdge {
        public final Vec3 start;
        public final Vec3 end;
        public int lifetime;

        public TrailEdge(Vec3 start, Vec3 end, int lifetime) {
            this.start = start;
            this.end = end;
            this.lifetime = lifetime;
        }

        public boolean isAlive() {
            return --this.lifetime > 0;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            int eid = (int)Double.doubleToRawLongBits(x);
            int animid = (int)Double.doubleToRawLongBits(z);
            int jointId = (int)Double.doubleToRawLongBits(xSpeed);
            int idx = (int)Double.doubleToRawLongBits(ySpeed);
            Entity entity = level.getEntity(eid);
            if (entity != null) {
                LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>) EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
                StaticAnimation animation = AnimationManager.getInstance().byId(animid);
                Optional<List<TrailInfo>> trailInfo = animation.getProperty(ClientAnimationProperties.TRAIL_EFFECT);
                TrailInfo result = (TrailInfo)((List)trailInfo.get()).get(idx);
                if (result.hand != null) {
                    ItemStack stack = (entitypatch.getOriginal()).getItemInHand(result.hand);
                    ItemSkin itemSkin = ItemSkins.getItemSkin(stack.getItem());
                    if (itemSkin != null && itemSkin.trailInfo() != null) {
                        result = itemSkin.trailInfo().overwrite(result);
                    }
                }

                if (entitypatch != null && animation != null && trailInfo.isPresent()) {
                    return new StayTrailParticle(level, entitypatch, entitypatch.getArmature().searchJointById(jointId), animation, result, this.spriteSet);
                }
            }

            return null;
        }
    }
}
