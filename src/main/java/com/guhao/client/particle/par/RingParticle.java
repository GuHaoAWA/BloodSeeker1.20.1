package com.guhao.client.particle.par;

import com.guhao.api.ParticleRenderTypeN;
import com.guhao.client.particle.core.TextureSheetParticleN;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.concurrent.TimeUnit;

@OnlyIn(Dist.CLIENT)
public class RingParticle extends TextureSheetParticleN {
    private final long startTime;

    @OnlyIn(Dist.CLIENT)
    public static class RingParticleProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public RingParticleProvider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(@NotNull SimpleParticleType typeIn, @NotNull ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new RingParticle(worldIn, x, y, z, this.spriteSet);
        }
    }


    public RingParticle(ClientLevel world, double x, double y, double z, SpriteSet spriteSet) {
        super(world, x, y, z);
        startTime = System.nanoTime();
        this.setSize(0f, 0f);
        this.quadSize = 4.05f;

        this.lifetime = 2;

        this.gravity = 0f;
        this.hasPhysics = false;

        this.setSpriteFromAge(spriteSet);
    }
    @Override
    public void render(@NotNull VertexConsumer vertexBuffer, Camera camera, float pt) {
        super.render(vertexBuffer, camera, pt);

        long currentTime = System.nanoTime();
        long elapsedTime = currentTime - startTime;
        if (elapsedTime >= TimeUnit.MILLISECONDS.toNanos(0)) {
            remove();
        }
        for (int i = 0; i < 1; i++) {
            remove();
        }
        Vec3 vec3 = camera.getPosition();
        float f = (float)(Mth.lerp((double)pt, this.xo, this.x) - vec3.x());
        float f1 = (float)(Mth.lerp((double)pt, this.yo, this.y) - vec3.y());
        float f2 = (float)(Mth.lerp((double)pt, this.zo, this.z) - vec3.z());


        Vector3f[] avector3f = new Vector3f[]{
                new Vector3f(-1.0F, 0F, -1.0F),
                new Vector3f(-1.0F, 0F, 1.0F),
                new Vector3f(1.0F, 0F, 1.0F),
                new Vector3f(1.0F, 0F, -1.0F)
        };
        float f4 = this.getQuadSize(pt);

        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.mul(f4);
            vector3f.add(f, f1, f2);

        }

        float f7 = this.getU0();
        float f8 = this.getU1();
        float f5 = this.getV0();
        float f6 = this.getV1();
        int j = 15728880;
        float alpha = this.alpha - (float)elapsedTime / 1000000.0F; // Decrease alpha value based on elapsed time
        vertexBuffer.vertex((double)avector3f[0].x(), (double)avector3f[0].y(), (double)avector3f[0].z()).uv(f8, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        vertexBuffer.vertex((double)avector3f[1].x(), (double)avector3f[1].y(), (double)avector3f[1].z()).uv(f8, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        vertexBuffer.vertex((double)avector3f[2].x(), (double)avector3f[2].y(), (double)avector3f[2].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        vertexBuffer.vertex((double)avector3f[3].x(), (double)avector3f[3].y(), (double)avector3f[3].z()).uv(f7, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            double x = Mth.lerp(pt, player.xOld, player.getX());
            double y = Mth.lerp(pt, player.yOld, player.getY()) + 0.05;
            double z = Mth.lerp(pt, player.zOld, player.getZ());
            this.setPos(x, y, z);
        }
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
        if (this.age < this.lifetime) {
            this.roll = (this.roll + 5.0F) % 360.0F; // 假设每帧增加5度
            this.oRoll = this.roll;
        } else {
            this.remove();
        }
    }

}


