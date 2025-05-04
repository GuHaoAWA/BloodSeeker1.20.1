package com.guhao.client.particle.par;

import com.guhao.api.ParticleRenderTypeN;
import com.guhao.utils.RenderUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.particle.BloodParticle;
import yesman.epicfight.client.particle.HitParticle;
import yesman.epicfight.particle.EpicFightParticles;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class EyeParticle extends HitParticle {
    public static EyeParticleProvider provider(SpriteSet spriteSet) {
        return new EyeParticleProvider(spriteSet);
    }
    @OnlyIn(Dist.CLIENT)

    public static class EyeParticleProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public EyeParticleProvider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new EyeParticle(worldIn, x, y, z, this.spriteSet);
        }
    }


    protected EyeParticle(ClientLevel world, double x, double y, double z, SpriteSet animatedSprite) {
        super(world, x, y, z, animatedSprite);
        this.setSize(0f, 0f);
        this.quadSize *= 2.25f;
        this.lifetime = 7;
        this.gravity = 0f;
        this.hasPhysics = false;
        double d = 0.20000000298023224;
        for(int i = 0; i < 8; ++i) {
            double particleMotionX = this.random.nextDouble() * d;
            d *= this.random.nextBoolean() ? 1.0 : -1.0;
            double particleMotionZ = this.random.nextDouble() * d;
            d *= this.random.nextBoolean() ? 1.0 : -1.0;
            this.level.addParticle(EpicFightParticles.BLOOD.get(), this.x, this.y, this.z, particleMotionX, 0.0, particleMotionZ);
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
    public ParticleRenderType getRenderType() {
        return ParticleRenderTypeN.PARTICLE_SHEET_LIT_NO_CULL;
    }
    @Override
    public void tick() {
        super.tick();
        Random r = new Random();
        double d = 0.20000000298023224;
        for(int i = 0; i < 50; ++i) {
            double particleMotionX = this.random.nextDouble() * d;
            d *= this.random.nextBoolean() ? 1.0 : -1.0;
            double particleMotionZ = this.random.nextDouble() * d;
            d *= this.random.nextBoolean() ? 1.0 : -1.0;
            this.level.addParticle(EpicFightParticles.BLOOD.get(), this.x, this.y, this.z, particleMotionX, 0.0, particleMotionZ);
        }
    }


}
