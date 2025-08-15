package com.guhao.client.particle.par;

import com.guhao.api.ParticleRenderTypeN;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class TwoEyeParticle extends TextureSheetParticle {
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
            return new TwoEyeParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }

    }

    private final SpriteSet spriteSet;

    protected TwoEyeParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
        super(world, x, y, z);
        this.spriteSet = spriteSet;
        this.alpha = 0.5f;
        this.setSize(0f, 0f);
        this.quadSize *= 1.1f;
        this.lifetime = 7;
        this.gravity = 0f;
        this.hasPhysics = false;
        Random rand = new Random();
        float angle = (float)Math.toRadians(180.0F + (rand.nextFloat() - 0.5F) * 360.0F);
        this.oRoll = angle;
        this.roll = angle;
        this.pickSprite(spriteSet);

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
    }


}
