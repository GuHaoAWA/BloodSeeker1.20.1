package com.guhao.client.particle.par;

import com.dfdyz.epicacg.client.render.EpicACGRenderType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.MeshProvider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.particle.CustomModelParticle;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.client.renderer.shader.AnimationShaderInstance;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class After_Image_BloodParticle extends CustomModelParticle<AnimatedMesh> {
    private final OpenMatrix4f[] poseMatrices;
    private final Matrix4f modelMatrix;
    private float alphaO;

    public After_Image_BloodParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, MeshProvider<AnimatedMesh> particleMesh, OpenMatrix4f[] matrices, Matrix4f modelMatrix) {
        super(level, x, y, z, xd, yd, zd, particleMesh);
        this.poseMatrices = matrices;
        this.modelMatrix = modelMatrix;
        this.lifetime = 20;
        this.rCol = 1.0F;
        this.gCol = 0.5F;
        this.bCol = 0.5F;
        this.alphaO = 0.3F;
        this.alpha = 0.3F;
    }
    @Override
    public boolean shouldCull() {
        return false;
    }
    @Override
    public void tick() {
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.pitchO = this.pitch;
            this.yawO = this.yaw;
            this.oRoll = this.roll;
            this.scaleO = this.scale;
            this.alphaO = this.alpha;
            this.alpha = (float)(this.lifetime - this.age) / (float)this.lifetime * 0.8F;
        }
    }
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
        PoseStack poseStack = new PoseStack();
        poseStack.mulPoseMatrix(RenderSystem.getModelViewMatrix());
        this.setupPoseStack(poseStack, camera, partialTicks);
        poseStack.mulPoseMatrix(this.modelMatrix);
        float alpha = this.alphaO + (this.alpha - this.alphaO) * partialTicks;
        AnimationShaderInstance animShader = EpicFightRenderTypes.getAnimationShader(GameRenderer.getPositionColorLightmapShader());
        this.particleMeshProvider.get().drawWithShader(poseStack, animShader, this.getLightColor(partialTicks), this.rCol, this.gCol, this.bCol, alpha, OverlayTexture.NO_OVERLAY, (Armature)null, this.poseMatrices);
    }
    protected void setupPoseStack(PoseStack poseStack, Camera camera, float partialTicks) {
        Quaternionf rotation = new Quaternionf(0.0F, 0.0F, 0.0F, 1.0F);
        float roll = Mth.lerp(partialTicks, this.oRoll, this.roll);
        float pitch = Mth.lerp(partialTicks, this.pitchO, this.pitch);
        float yaw = Mth.lerp(partialTicks, this.yawO, this.yaw);
        rotation.mul(QuaternionUtils.YP.rotationDegrees(yaw));
        rotation.mul(QuaternionUtils.XP.rotationDegrees(pitch));
        rotation.mul(QuaternionUtils.ZP.rotationDegrees(roll));
        Vec3 vec3 = camera.getPosition();
        float x = (float)(Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
        float y = (float)(Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
        float z = (float)(Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());
        float scale = (float)Mth.lerp(partialTicks, this.scaleO, (double)this.scale);
        poseStack.translate(x, y, z);
        poseStack.mulPose(rotation);
        poseStack.scale(scale, scale, scale);
    }
    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return EpicACGRenderType.TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        public Provider(SpriteSet spriteSet) {

        }
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            Entity entity = level.getEntity((int)Double.doubleToLongBits(xSpeed));
            LivingEntityPatch<?> entitypatch = (LivingEntityPatch)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
            if (entitypatch != null && ClientEngine.getInstance().renderEngine.hasRendererFor(entitypatch.getOriginal())) {
                PatchedEntityRenderer renderer = ClientEngine.getInstance().renderEngine.getEntityRenderer(entitypatch.getOriginal());
                Armature armature = entitypatch.getArmature();
                PoseStack poseStack = new PoseStack();
                renderer.mulPoseStack(poseStack, armature, (LivingEntity)entitypatch.getOriginal(), entitypatch, 1.0F);
                OpenMatrix4f[] matrices = renderer.getPoseMatrices(entitypatch, armature, 1.0F, true);
                MeshProvider<AnimatedMesh> meshProvider = ClientEngine.getInstance().renderEngine.getEntityRenderer(entitypatch.getOriginal()).getMeshProvider(entitypatch);
                After_Image_BloodParticle particle = new After_Image_BloodParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, meshProvider, matrices, poseStack.last().pose());
                particle.setColor(12.4f, 0.12f, 0.15f);
                return particle;
            } else {
                return null;
            }
        }
    }
}