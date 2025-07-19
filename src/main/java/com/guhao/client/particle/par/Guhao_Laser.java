package com.guhao.client.particle.par;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.RawMesh;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.client.particle.CustomModelParticle;
import yesman.epicfight.client.particle.EpicFightParticleRenderTypes;

@OnlyIn(Dist.CLIENT)
public class Guhao_Laser extends CustomModelParticle<RawMesh> {
    private final float length;
    private final float xRot;
    private final float yRot;

    public Guhao_Laser(ClientLevel level, double x, double y, double z, double toX, double toY, double toZ) {
        super(level, x, y, z, 0.0, 0.0, 0.0, Meshes.LASER);
        this.lifetime = 11;
        Vec3 direction = new Vec3(toX - x, toY - y, toZ - z);
        Vec3 start = new Vec3(x, y, z);
        Vec3 destination = start.add(direction.normalize().scale(200.0));
        BlockHitResult hitResult = level.clip(new ClipContext(start, destination, Block.COLLIDER, Fluid.NONE, (Entity) null));
        double xLength = hitResult.getLocation().x - x;
        double yLength = hitResult.getLocation().y - y;
        double zLength = hitResult.getLocation().z - z;
        double horizontalDistance = (float) Math.sqrt(xLength * xLength + zLength * zLength);
        this.length = (float) Math.sqrt(xLength * xLength + yLength * yLength + zLength * zLength);
        this.yRot = (float) (-Math.atan2(zLength, xLength) * 57.29577951308232) - 90.0F;
        this.xRot = (float) (Math.atan2(yLength, horizontalDistance) * 57.29577951308232);
        int smokeCount = (int) this.length * 4;

        for (int i = 0; i < smokeCount; ++i) {
            level.addParticle(ParticleTypes.SMOKE, x + xLength / (double) smokeCount * (double) i, y + yLength / (double) smokeCount * (double) i, z + zLength / (double) smokeCount * (double) i, 0.0, 0.0, 0.0);
        }

        this.setBoundingBox(new AABB(x, y, z, toX, toY, toZ));
    }

    public void prepareDraw(PoseStack poseStack, float partialTicks) {
        poseStack.mulPose(QuaternionUtils.YP.rotationDegrees(this.yRot));
        poseStack.mulPose(QuaternionUtils.XP.rotationDegrees(this.xRot));
        float progression = ((float) this.age + partialTicks) / (float) (this.lifetime + 1);
        float scale = Mth.sin(progression * 3.1415927F);
        float zScale = progression > 0.5F ? 1.0F : Mth.sin(progression * 3.1415927F);
        poseStack.scale(scale, scale, zScale * this.length);
    }

    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
        super.render(vertexConsumer, camera, partialTicks);
        PoseStack poseStack = new PoseStack();
        this.setupPoseStack(poseStack, camera, partialTicks);
        this.prepareDraw(poseStack, partialTicks);
        poseStack.scale(1.4F, 1.4F, 1.4F);
    }

    public @NotNull ParticleRenderType getRenderType() {
        return EpicFightParticleRenderTypes.TRANSLUCENT_GLOWING;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        public Provider(SpriteSet spriteSet) {
        }

        public Particle createParticle(@NotNull SimpleParticleType typeIn, @NotNull ClientLevel level, double startX, double startY, double startZ, double endX, double endY, double endZ) {
            Guhao_Laser particle = new Guhao_Laser(level, startX, startY, startZ, endX, endY, endZ);
            particle.setColor(255f, 0f, 0f);
            return particle;
        }
    }
}