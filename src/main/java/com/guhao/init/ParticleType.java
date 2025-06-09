package com.guhao.init;


import com.guhao.GuhaoMod;
import com.guhao.client.particle.par.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.particle.HitParticleType;

import static com.guhao.GuhaoMod.MODID;


@Mod.EventBusSubscriber(modid = GuhaoMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ParticleType {
    public static final DeferredRegister<net.minecraft.core.particles.ParticleType<?>> PARTICLES;
    public static final RegistryObject<SimpleParticleType> TRAIL_GUHAO;
    public static final RegistryObject<SimpleParticleType> TRAIL_GUHAO_BLOOM;
    public static final RegistryObject<SimpleParticleType> TRAIL_GUHAO_SPACE;
    public static final RegistryObject<SimpleParticleType> EYE;
    public static final RegistryObject<SimpleParticleType> TWO_EYE;
    public static final RegistryObject<SimpleParticleType> RING;
    public static final RegistryObject<SimpleParticleType> RED_RING;
    public static final RegistryObject<SimpleParticleType> BLOOD_FIRE_FLAME;
    public static final RegistryObject<SimpleParticleType> BLOOD_FIRE_FLAME2;
    public static final RegistryObject<SimpleParticleType> BLOOD_JUDGEMENT;
    public static final RegistryObject<SimpleParticleType> ONE_JC_BLOOD_JUDGEMENT;
    public static final RegistryObject<SimpleParticleType> ONE_JC_BLOOD_JUDGEMENT_LONG;
    public static final RegistryObject<SimpleParticleType> ONE_JC_BLOOD_JUDGEMENT_WIDE;
    public static final RegistryObject<SimpleParticleType> ENTITY_AFTER_IMG_BLOOD;
    public static final RegistryObject<SimpleParticleType> GUHAO_LASER;

    @SubscribeEvent
    public static void RP(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(TRAIL_GUHAO_BLOOM.get(), BloomTrailParticleGuhao.Provider::new);
        event.registerSpriteSet(TRAIL_GUHAO_SPACE.get(), SpaceTrailParticle.Provider::new);
        event.registerSpriteSet(EYE.get(), EyeParticle.EyeParticleProvider::new);
        event.registerSpriteSet(TWO_EYE.get(), TwoEyeParticle.EyeParticleProvider::new);
        event.registerSpriteSet(RING.get(), RingParticle.RingParticleProvider::new);
        event.registerSpriteSet(RED_RING.get(), RedRingParticle.RedRingParticleProvider::new);
        event.registerSpriteSet(BLOOD_FIRE_FLAME.get(), BloodFireParticle.SmallFlameProvider::new);
        event.registerSpriteSet(BLOOD_FIRE_FLAME2.get(), Flameparticle2Particle.FlameparticleParticle2Provider::new);
        event.registerSpriteSet(BLOOD_JUDGEMENT.get(), BloodJudgementParticle.Provider::new);
        event.registerSpriteSet(ONE_JC_BLOOD_JUDGEMENT.get(), ONEJCBladeTrail.Provider::new);
        event.registerSpriteSet(ONE_JC_BLOOD_JUDGEMENT_LONG.get(), ONEJCBladeTrailLong.Provider::new);
        event.registerSpriteSet(ONE_JC_BLOOD_JUDGEMENT_WIDE.get(), ONEJCBladeTrailWide.Provider::new);
        event.registerSpriteSet(ENTITY_AFTER_IMG_BLOOD.get(), After_Image_BloodParticle.Provider::new);
        event.registerSpriteSet(GUHAO_LASER.get(), Guhao_Laser.Provider::new);
    }

    public ParticleType() {
    }

    static {
        PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);
        TRAIL_GUHAO = PARTICLES.register("trail_guhao", () -> new SimpleParticleType(true));
        TRAIL_GUHAO_BLOOM = PARTICLES.register("trail_guhao_bloom", () -> new SimpleParticleType(true));
        TRAIL_GUHAO_SPACE = PARTICLES.register("trail_guhao_space", () -> new SimpleParticleType(true));
        EYE = PARTICLES.register("eye", () -> new HitParticleType(true, HitParticleType.RANDOM_WITHIN_BOUNDING_BOX, HitParticleType.ZERO));
        RING = PARTICLES.register("ring", () -> new SimpleParticleType(true));
        TWO_EYE = PARTICLES.register("two_eye", () -> new SimpleParticleType(true));
        RED_RING = PARTICLES.register("red_ring", () -> new SimpleParticleType(true));
        BLOOD_FIRE_FLAME = PARTICLES.register("blood_fire_flame", () -> new SimpleParticleType(true));
        BLOOD_FIRE_FLAME2 = PARTICLES.register("blood_fire_flame_2", () -> new SimpleParticleType(true));
        BLOOD_JUDGEMENT = PARTICLES.register("blood_judgement", () -> new SimpleParticleType(true));
        ONE_JC_BLOOD_JUDGEMENT = PARTICLES.register("one_jc_blood_judgement", () -> new HitParticleType(true));
        ONE_JC_BLOOD_JUDGEMENT_LONG = PARTICLES.register("one_jc_blood_judgement_long", () -> new HitParticleType(true));
        ONE_JC_BLOOD_JUDGEMENT_WIDE = PARTICLES.register("one_jc_blood_judgement_wide", () -> new HitParticleType(true));
        ENTITY_AFTER_IMG_BLOOD = PARTICLES.register("after_image_blood", () -> new SimpleParticleType(true));
        GUHAO_LASER = PARTICLES.register("guhao_laser", () -> new SimpleParticleType(true));
    }
}
