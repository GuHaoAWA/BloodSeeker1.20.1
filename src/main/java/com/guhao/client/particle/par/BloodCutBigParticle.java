package com.guhao.client.particle.par;

import com.guhao.api.ParticleRenderTypeN;
import com.guhao.init.ParticleType;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.client.particle.HitParticle;

import java.util.Random;

public class BloodCutBigParticle extends HitParticle {

    public BloodCutBigParticle(ClientLevel world, double x, double y, double z, SpriteSet animatedSprite) {
        super(world, x, y, z, animatedSprite);
        this.rCol = 1.0F;
        this.gCol = 0.1F;
        this.bCol = 0.1F;

        this.lifetime = 9;
        alpha = 0.66f;
        this.setSpriteFromAge(animatedSprite);
        Random rand = new Random();
        float angle = (float)Math.toRadians(75.0F + (rand.nextFloat() - 0.5F) * 75.0F + (rand.nextBoolean() ? 0.0F : 180.0F));
        this.oRoll = angle;
        this.roll = angle;
        this.quadSize = 8.0F * rand.nextFloat(0.76f,1.24f);
    }
    @Override
    public int getLightColor(float partialTick) {
        return 15728880;
    }
    @Override
    public void tick() {
        if (this.age == 1) {
            this.level.addParticle(ParticleType.EYE.get(), this.x, this.y, this.z, 0.0, 0.0, 0.0);
            for (int i = 0; i < 3; i++) {
                Random rand = new Random();
                this.level.addParticle(ParticleType.EYE.get(), this.x + rand.nextFloat(-1.0f,1.0f), this.y+ rand.nextFloat(-1.0f,1.0f), this.z+ rand.nextFloat(-1.0f,1.0f), 0.0, 0.0, 0.0);
            }
        }
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.setSpriteFromAge(this.animatedSprite);
        }

    }
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderTypeN.PARTICLE_SHEET_LIT_NO_CULL2;
    }
    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(@NotNull SimpleParticleType typeIn, @NotNull ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new BloodCutBigParticle(worldIn, x, y, z, this.spriteSet);
        }
    }
}
