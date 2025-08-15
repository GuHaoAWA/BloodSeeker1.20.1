package com.guhao.client.particle.par;


import com.guhao.client.model.ESClientModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.client.model.RawMesh;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.client.particle.EpicFightParticleRenderTypes;
import yesman.epicfight.client.particle.TexturedCustomModelParticle;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class ConquerorHakiParticle extends TexturedCustomModelParticle {
    private LivingEntityPatch<?> caster;

    public ConquerorHakiParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, RawMesh particleMesh, ResourceLocation texture) {
        super(level, x, y, z, xd, yd, zd, particleMesh, texture);
        this.lifetime = 5;
        this.hasPhysics = false;
        this.roll = (float)xd;
        this.pitch = (float)zd;
        this.scale = 12.0F;
        Entity entity = level.getEntity((int)Double.doubleToLongBits(yd));
        if (entity != null) {
            this.caster = (LivingEntityPatch<?>) entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
        }

    }

    public @NotNull ParticleRenderType getRenderType() {
        return EpicFightParticleRenderTypes.PARTICLE_MODEL_NO_NORMAL;
    }

    public void tick() {
        super.tick();
    }
    @Override
    public void render(@NotNull VertexConsumer vertexBuffer, Camera camera, float pt) {
        super.render(vertexBuffer,camera,pt);
        this.yaw += 0.5F;
        this.scale += (float)Math.max(30 - this.age, 0) * 0.85F;
        this.alpha = (float)(this.lifetime - this.age) / (float)this.lifetime * 2.0F;
        if (this.caster != null && this.caster.getStunShield() <= 0.0F) {
            this.remove();
        }
    }

    protected void setupPoseStack(PoseStack poseStack, Camera camera, float partialTicks) {
        float yaw = Mth.lerp(partialTicks, this.yawO, this.yaw);
        Vec3 vec3 = camera.getPosition();
        float x = (float)(Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
        float y = (float)(Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
        float z = (float)(Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());
        float scale = Mth.lerp(partialTicks, this.scaleO, this.scale);
        poseStack.translate(x, y, z);
        poseStack.mulPose(QuaternionUtils.XP.rotationDegrees(this.pitch));
        poseStack.mulPose(QuaternionUtils.ZP.rotationDegrees(this.roll));
        poseStack.mulPose(QuaternionUtils.YP.rotationDegrees(yaw));
        poseStack.scale(scale, scale, scale);
    }

    public int getLightColor(float p_107086_) {
        int i = super.getLightColor(p_107086_);
        int k = i >> 16 & 255;
        return 240 | k << 16;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {

        public Provider(SpriteSet spriteSet) {
        }

        public Particle createParticle(@NotNull SimpleParticleType typeIn, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ConquerorHakiParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, ESClientModels.CONQUEROR_HAKI, new ResourceLocation("guhao", "textures/models/particle/conqueror_haki.png"));
        }
    }
}
