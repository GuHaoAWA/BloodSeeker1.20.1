package com.guhao.client.particle.par;


import com.guhao.utils.RenderUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class BloodJudgementParticle extends NoRenderParticle {
    public BloodJudgementParticle(ClientLevel level, double x, double y, double z, double rx, double ry, double rz) {
        super(level, x, y, z, rx, ry, rz);
        this.lifetime = 42;
    }

        public boolean shouldCull() {
        return false;
    }

        public void tick() {
        if (this.age++ >= this.lifetime) {
            this.remove();
        }

        for(int i = 0; i < 2; ++i) {
            float r = Mth.nextFloat(this.random, 5.0F, 8.0F);
            float theta = Mth.nextFloat(this.random, 0.0F, 360.0F);
            float beta = Mth.nextFloat(this.random, 45.0F, 80.0F);
            float r2 = 8.0F;
            float theta2 = Mth.nextFloat(this.random, 180.0F + theta - 45.0F, 180.0F + theta + 45.0F);
            float beta2 = Mth.nextFloat(this.random, 180.0F + beta - 20.0F, 180.0F + beta + 20.0F);
            theta = (float)((double)(theta / 180.0F) * Math.PI);
            beta = (float)((double)(beta / 180.0F) * Math.PI);
            theta2 = (float)((double)(theta2 / 180.0F) * Math.PI);
            beta2 = (float)((double)(beta2 / 180.0F) * Math.PI);
            float scale = 0.52F;
            double sr = (double)r * Math.sin(beta);
            double sx = sr * Math.sin(theta) * (double)scale;
            double sy = (double)r * Math.cos(beta) * (double)scale;
            double sz = sr * Math.cos(theta) * (double)scale;
            double er = (double)r2 * Math.sin(beta2);
            double ex = er * Math.sin(theta2) * (double)scale;
            double ey = (double)r2 * Math.cos(beta2) * (double)scale;
            double ez = er * Math.cos(theta2) * (double)scale;
            double randTheta = Math.random() * 2 * Math.PI; // 随机生成0到2π之间的角度
            double randPhi = Math.random() * Math.PI; // 随机生成0到π之间的角度
            double radius = 2.0; // 球的半径
            double xCoord = radius * Math.sin(randPhi) * Math.cos(randTheta); // 根据球面坐标公式计算x坐标
            double yCoord = radius * Math.sin(randPhi) * Math.sin(randTheta); // 根据球面坐标公式计算y坐标
            double zCoord = radius * Math.cos(randPhi); // 根据球面坐标公式计算z坐标
            RenderUtils.AddParticle(this.level, new BloodBladeTrail(this.level, sx + this.x, sy + this.y + 1.2, sz + this.z, 0, (ey - sy)*4, 0));
        }

    }

        public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

        @OnlyIn(Dist.CLIENT)
        public static class Provider implements ParticleProvider<SimpleParticleType> {
            private final SpriteSet spriteSet;

            public Provider(SpriteSet spriteSet) {
                this.spriteSet = spriteSet;
            }

            public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
                return new BloodJudgementParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            }
        }
}
