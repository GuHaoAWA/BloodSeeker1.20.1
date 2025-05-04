package com.guhao.client.particle.par;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Flameparticle2Particle extends TextureSheetParticle {
    private final SpriteSet spriteSet;

    public static Flameparticle2Particle.FlameparticleParticle2Provider provider(SpriteSet spriteSet) {
        return new Flameparticle2Particle.FlameparticleParticle2Provider(spriteSet);
    }

    protected Flameparticle2Particle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
        super(world, x, y, z);
        this.spriteSet = spriteSet;
        this.setSize(0.05F, 0.07F);
        this.lifetime = 5;
        this.gravity = 0.0F;
        this.hasPhysics = true;
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
        this.setSpriteFromAge(spriteSet);
    }

    public int getLightColor(float partialTick) {
        return 15728880;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    public void tick() {
        super.tick();
        if (!this.removed) {
            this.setSprite(this.spriteSet.get(this.age / 1 % 6 + 1, 6));
        }

    }

    public static class FlameparticleParticle2Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public FlameparticleParticle2Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new Flameparticle2Particle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
