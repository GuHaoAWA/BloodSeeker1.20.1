package com.guhao.client.particle.par;

import com.guhao.api.ParticleRenderTypeN;
import com.guhao.client.particle.core.TextureSheetParticleN;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class RedRingParticle extends TextureSheetParticleN {

    @OnlyIn(Dist.CLIENT)
    public static class RedRingParticleProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public RedRingParticleProvider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(@NotNull SimpleParticleType typeIn, @NotNull ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new RedRingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }


    protected RedRingParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
        super(world, x, y, z);

        this.setSize(0f, 0f);
        this.quadSize = 0f;

        this.lifetime = 100;

        this.gravity = 0f;
        this.hasPhysics = false;
        this.onGround = true;
        this.xd = vx * 0;
        this.yd = vy * 0;
        this.zd = vz * 0;

        this.setSpriteFromAge(spriteSet);
    }

    public void render(@NotNull VertexConsumer vertexBuffer, Camera camera, float pt) {
        Vec3 vec3 = camera.getPosition();
        float f = (float)(Mth.lerp((double)pt, this.xo, this.x) - vec3.x());
        float f1 = (float)(Mth.lerp((double)pt, this.yo, this.y) - vec3.y());
        float f2 = (float)(Mth.lerp((double)pt, this.zo, this.z) - vec3.z());

        // Commented out the rotation related code
        // Quaternion quaternion;
        // if (this.roll == 0.0F) {
        //    quaternion = camera.rotation();
        // } else {
        //    quaternion = new Quaternion(camera.rotation());
        //    float f3 = Mth.lerp(pt, this.oRoll, this.roll);
        //    quaternion.mul(Vector3f.ZP.rotation(f3));
        // }

        // Commented out the transformed vector
        // Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
        // vector3f1.transform(quaternion);

        Vector3f[] avector3f = new Vector3f[]{
                new Vector3f(-1.0F, 0F, -1.0F),
                new Vector3f(-1.0F, 0F, 1.0F),
                new Vector3f(1.0F, 0F, 1.0F),
                new Vector3f(1.0F, 0F, -1.0F)
        };
        float f4 = this.getQuadSize(pt);

        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            // Removed the transformation and translation related to the rotation
            // vector3f.transform(quaternion);
            vector3f.mul(f4);
            vector3f.add(f, f1, f2);
        }

        float f7 = this.getU0();
        float f8 = this.getU1();
        float f5 = this.getV0();
        float f6 = this.getV1();
        int j = 15728880;
        vertexBuffer.vertex((double)avector3f[0].x(), (double)avector3f[0].y(), (double)avector3f[0].z()).uv(f8, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        vertexBuffer.vertex((double)avector3f[1].x(), (double)avector3f[1].y(), (double)avector3f[1].z()).uv(f8, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        vertexBuffer.vertex((double)avector3f[2].x(), (double)avector3f[2].y(), (double)avector3f[2].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        vertexBuffer.vertex((double)avector3f[3].x(), (double)avector3f[3].y(), (double)avector3f[3].z()).uv(f7, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();

    }
    @Override
    public boolean shouldCull() {
        return false;
    }

    @Override
    public int getLightColor(float partialTick) {
        return 15728880;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderTypeN.PARTICLE_SHEET_LIT_NO_CULL;
    }

    @Override
    public void tick() {
        super.tick();
        quadSize += 2.0F;
    }
}


