package com.guhao.epicfight;


import cc.xypp.damage_number.network.DamagePackage;
import cc.xypp.damage_number.network.Network;
import com.dfdyz.epicacg.client.camera.CameraAnimation;
import com.dfdyz.epicacg.efmextra.anims.BasicAttackAnimationEx;
import com.dfdyz.epicacg.utils.MoveCoordFuncUtils;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.guhao.GuHaoColliderPreset;
import com.guhao.api.SpecialActionAnimation;
import com.guhao.epicfight.skills.GuHaoSkills;
import com.guhao.init.Effect;
import com.guhao.init.ParticleType;
import com.guhao.ranksystem.ServerEventExtra;
import com.guhao.stars.regirster.Sounds;
import com.guhao.utils.BattleUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import reascer.wom.animation.attacks.BasicMultipleAttackAnimation;
import reascer.wom.animation.attacks.SpecialAttackAnimation;
import reascer.wom.animation.attacks.SpecialAttackNoRotAnimation;
import reascer.wom.gameasset.WOMWeaponColliders;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.types.*;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.forgeevent.AnimationRegistryEvent;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.effect.EpicFightMobEffects;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.guhao.GuHaoColliderPreset.BIG_ATTACK;
import static com.guhao.GuhaoMod.MODID;


public class GuHaoAnimations {


    public static void registerAnimations(AnimationRegistryEvent event) {
        event.getRegistryMap().put(MODID, GuHaoAnimations::register);
    }
    public static CameraAnimation BLOOD_JUDGEMENT_C;

    public static void LoadCamAnims() {
        BLOOD_JUDGEMENT_C = CameraAnimation.load(new ResourceLocation(MODID, "camera_animation/blood_judgement.json"));
    }
    public GuHaoAnimations() {}


    public static StaticAnimation NB_ATTACK;
    public static StaticAnimation ENDER;
    public static StaticAnimation PASSIVE;
    public static StaticAnimation V_GUHAO_SHEATHING_AUTO1;
    public static StaticAnimation GUHAO_BATTOJUTSU_DASH;
    public static StaticAnimation GUHAO_UCHIGATANA_SHEATHING_AUTO;
    public static StaticAnimation GUHAO_UCHIGATANA_SHEATHING_DASH;
    public static StaticAnimation EF_UCHIGATANA_SHEATHING_DASH;
    public static StaticAnimation GUHAO_UCHIGATANA_SHEATH_AIR_SLASH;
    public static StaticAnimation GUHAO_DASH;
    public static StaticAnimation GUHAO_DASH_2;
    public static StaticAnimation SACRIFICE;
    public static StaticAnimation BIU;
    public static StaticAnimation GUHAO_BIU;
    public static StaticAnimation BLOOD_JUDGEMENT;
    public static StaticAnimation BLOOD_BURST;
    public static StaticAnimation BIG_STAR;
    public static StaticAnimation DENG_LONG;
    public static StaticAnimation GUHAO_UCHIGATANA_SCRAP;
    public static StaticAnimation JIANQIE;
    public static StaticAnimation SETTLEMENT;
    public static StaticAnimation HERRSCHER_AUTO_3;
    public static StaticAnimation GUHAO_UCHIGATANA_SHEATHING_AUTO_EX;
    public static StaticAnimation GUHAO_EX_4;
    public static StaticAnimation KATANA_AUTO_3;

    private static void register() {
        HumanoidArmature biped = Armatures.BIPED;
        Random rand = new Random();

        SACRIFICE = (new ActionAnimation(0.1F, 1.851F, "biped/sacrifice", biped))
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
//                .addProperty(ClientAnimationProperties.TRAIL_EFFECT, newTFL(newTF(0f, 10.0f, 5, biped.toolR, InteractionHand.MAIN_HAND)))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, SC)
                .addProperty(AnimationProperty.StaticAnimationProperty.TIME_STAMPED_EVENTS, new AnimationEvent.TimeStampedEvent[]{
                        AnimationEvent.TimeStampedEvent.create(0.415F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.sacrifice(ep), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(0.8F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_ritual(ep), AnimationEvent.Side.BOTH),
                        AnimationEvent.TimeStampedEvent.create(0.8F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_god_incarnation(ep), AnimationEvent.Side.BOTH),
                        AnimationEvent.TimeStampedEvent.create(0.8F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_explosion(ep), AnimationEvent.Side.BOTH),
                        //AnimationEvent.TimeStampedEvent.create(0.605F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.sacrifice_post(ep), AnimationEvent.Side.CLIENT),
                        AnimationEvent.TimeStampedEvent.create(0.805F, Animations.ReusableSources.FRACTURE_GROUND_SIMPLE, AnimationEvent.Side.SERVER).params(new Vec3f(0.0F, -0.32F, 0.0F), Armatures.BIPED.rootJoint, 3.0, 0.45F)});
        BLOOD_BURST = (new ActionAnimation(0.1F, 1.851F, "biped/blood_burst", biped))
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                //.addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, BLOOD_BURST_S)
                .addProperty(AnimationProperty.StaticAnimationProperty.TIME_STAMPED_EVENTS, new AnimationEvent.TimeStampedEvent[]{
                        AnimationEvent.TimeStampedEvent.create(0.305F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_burst(ep), AnimationEvent.Side.SERVER),
                });
        NB_ATTACK = (new AirSlashAnimation(0.1F, 0.15F, 0.386F, 0.55F, BIG_ATTACK, biped.rootJoint, "biped/very_gaint_big_cool_attack", biped))
                .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, ParticleType.BLOOD_CUT_BIG)
                .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.15f))
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.46F, Animations.ReusableSources.FRACTURE_GROUND_SIMPLE, AnimationEvent.Side.CLIENT).params(new Vec3f(0.0F, -0.24F, 0f), Armatures.BIPED.toolR, 4.25, 0.45F),
                        AnimationEvent.TimeStampedEvent.create(0.05F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_explosion(ep), AnimationEvent.Side.BOTH));
//                .addProperty(ClientAnimationProperties.TRAIL_EFFECT, newTFL(newTF(0.12f, 0.50f, 20, biped.toolR, InteractionHand.MAIN_HAND))));
        GUHAO_DASH = (new DashAttackAnimation(0.1F, 0.09F,0.091F, 0.45F, 0.985F, null, biped.toolR, "biped/guhao_dash", biped))
                .addProperty(AnimationProperty.AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.15F)
                .addProperty(AnimationProperty.AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6f))
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0f))
                .addProperty(AnimationProperty.ActionAnimationProperty.RESET_PLAYER_COMBO_COUNTER, true);
        GUHAO_DASH_2 = (new DashAttackAnimation(0.1F, 0.31F,0.31F, 0.44F, 0.70F, null, biped.toolR, "biped/guhao_dash_2", biped))
                .addProperty(AnimationProperty.AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.15F)
                .addProperty(AnimationProperty.AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6f))
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0f))
                .addProperty(AnimationProperty.ActionAnimationProperty.RESET_PLAYER_COMBO_COUNTER, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> 0.66F);
//                .addProperty(ClientAnimationProperties.TRAIL_EFFECT, newTFL(newTF(0.1f, 0.65f, 6, biped.toolR, InteractionHand.MAIN_HAND)));
        ENDER = (new SpecialActionAnimation(0.1F, 0.485F, "biped/ender", biped))
                .addProperty(AnimationProperty.StaticAnimationProperty.TIME_STAMPED_EVENTS, new AnimationEvent.TimeStampedEvent[]{
                        AnimationEvent.TimeStampedEvent.create(0.381F, (livingEntityPatch, staticAnimation, objects) -> BattleUtils.Guhao_Battle_utils.ender(livingEntityPatch), AnimationEvent.TimeStampedEvent.Side.SERVER)});
        V_GUHAO_SHEATHING_AUTO1 = (new BasicAttackAnimation(0.1F, 0.16F, 0.45F, 0.785F, null, biped.toolR, "biped/auto1", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(30.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6f))
                .addProperty(AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(30.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(2.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(3.0F)));
//                .addProperty(ClientAnimationProperties.TRAIL_EFFECT, newTFL(newTF(0.16f, 10.0f, 6, biped.toolR, InteractionHand.MAIN_HAND)));
        GUHAO_BATTOJUTSU_DASH = new AttackAnimation(0.15F, 0.43F, 0.7F, 0.92F, 1.4F, GuHaoColliderPreset.GUHAO_BATTOJUTSU_DASH, biped.rootJoint, "biped/battojutsu_dash", biped)

                        .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_SHARP.get())
                        .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, ParticleType.BLOOD_CUT_BIG)
                        .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD)
                        .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, null)
                        .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, Animations.ReusableSources.CONSTANT_ONE)
                        .addEvents(
                                AnimationEvent.TimeStampedEvent.create(0.05F, Animations.ReusableSources.PLAY_SOUND, AnimationEvent.Side.SERVER).params(EpicFightSounds.SWORD_IN.get()),
                                AnimationEvent.TimeStampedEvent.create(0.7F, (entitypatch, animation, params) -> {
                                    Entity entity = entitypatch.getOriginal();
                                    entity.level().addParticle(ParticleType.ENTITY_AFTER_IMG_BLOOD.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0.0, 0.0);
                                    Random random = new Random();
                                    double x = entity.getX() + (random.nextDouble() - random.nextDouble()) * 2.0D;
                                    double y = entity.getY();
                                    double z = entity.getZ() + (random.nextDouble() - random.nextDouble()) * 2.0D;
                                    entity.level().addParticle(ParticleTypes.EXPLOSION, x, y, z, random.nextDouble() * 0.005D, 0.0D, 0.0D);
                                }, AnimationEvent.Side.CLIENT),
                                AnimationEvent.TimeStampedEvent.create(0.8F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_blade(ep), AnimationEvent.Side.SERVER)
                        );
        GUHAO_UCHIGATANA_SHEATHING_AUTO = new BasicAttackAnimation(0.05F, 0.0F, 0.06F, 0.65F, null, biped.toolR, "biped/uchigatana_sheath_auto", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.25F))
                .addProperty(AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(30.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(2.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_SHARP.get())
                .addProperty(AnimationProperty.StaticAnimationProperty.TIME_STAMPED_EVENTS, new AnimationEvent.TimeStampedEvent[]{
                        AnimationEvent.TimeStampedEvent.create(0.055F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_blade(ep), AnimationEvent.Side.SERVER),
                });
        GUHAO_UCHIGATANA_SHEATHING_AUTO_EX = new BasicAttackAnimation(0.05F, 0.0F, 0.06F, 0.65F, null, biped.toolR, "biped/guhao_uchigatana_sheath_auto_ex", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.25F))
                .addProperty(AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(30.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(2.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_SHARP.get())
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.StaticAnimationProperty.TIME_STAMPED_EVENTS, new AnimationEvent.TimeStampedEvent[]{
                        AnimationEvent.TimeStampedEvent.create(0.055F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_blade(ep), AnimationEvent.Side.SERVER),
                });
        GUHAO_UCHIGATANA_SHEATHING_DASH = new DashAttackAnimation(0.05F, 0.05F, 0.2F, 0.35F, 0.65F, null, biped.toolR, "biped/uchigatana_sheath_dash_guhao", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.25F))
                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_SHARP.get())
                .addProperty(AnimationProperty.StaticAnimationProperty.TIME_STAMPED_EVENTS, new AnimationEvent.TimeStampedEvent[]{
                        AnimationEvent.TimeStampedEvent.create(0.345F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_blade(ep), AnimationEvent.Side.SERVER),
                });
        EF_UCHIGATANA_SHEATHING_DASH = new DashAttackAnimation(0.05F, 0.05F, 0.2F, 0.35F, 0.65F, null, biped.toolR, "biped/uchigatana_sheath_dash", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_SHARP.get())
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                .addProperty(AnimationProperty.AttackAnimationProperty.EXTRA_COLLIDERS, 2)
                .addProperty(AnimationProperty.ActionAnimationProperty.RESET_PLAYER_COMBO_COUNTER, false);
                
        GUHAO_UCHIGATANA_SHEATH_AIR_SLASH = (new AirSlashAnimation(0.1F, 0.1F, 0.16F, 0.3F, null, biped.toolR, "biped/uchigatana_sheath_airslash", biped))
                .addProperty(AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(30.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(2.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_SHARP.get())
                .addProperty(AnimationProperty.AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
                .addProperty(AnimationProperty.StaticAnimationProperty.TIME_STAMPED_EVENTS, new AnimationEvent.TimeStampedEvent[]{
                        AnimationEvent.TimeStampedEvent.create(0.155F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_blade(ep), AnimationEvent.Side.SERVER),});
        BIU = (new BasicAttackAnimation(0.1F, 0.315F,0.315F, 0.385F, 1.05F, GuHaoColliderPreset.ENDER_LASER, biped.toolR, "biped/biu", biped))
                .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.NONE)
                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT)
                .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST)
                .addProperty(AnimationProperty.AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)

                .addEvents(AnimationEvent.TimeStampedEvent.create(0.195F, (entitypatch, self, params) -> {
                    if (entitypatch instanceof PlayerPatch) {
                        entitypatch.getOriginal().level().playSound((Player) entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.BUZZ.get(), SoundSource.PLAYERS, 0.6F, 1.5F);
                    }

                }, AnimationEvent.Side.CLIENT), AnimationEvent.TimeStampedEvent.create(0.345F, (entitypatch, self, params) -> {
                    if (entitypatch instanceof PlayerPatch) {
                        entitypatch.getOriginal().level().playSound((Player) entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.LASER_BLAST.get(), SoundSource.PLAYERS, 0.7F, 1.0F);
                    }

            OpenMatrix4f transformMatrix = entitypatch.getArmature().getBindedTransformFor(entitypatch.getAnimator().getPose(-0.5F), Armatures.BIPED.toolR);
            OpenMatrix4f transformMatrix2 = entitypatch.getArmature().getBindedTransformFor(entitypatch.getAnimator().getPose(-0.5F), Armatures.BIPED.toolR);
                    transformMatrix.translate(new Vec3f(0.0F, -0.6F, -0.3F));
                    transformMatrix2.translate(new Vec3f(0.0F, -0.8F, -0.3F));
                    OpenMatrix4f CORRECTION = (new OpenMatrix4f()).rotate(-((float) Math.toRadians(entitypatch.getOriginal().yRotO + 180.0F)), new Vec3f(0.0F, 1.0F, 0.0F));
                    OpenMatrix4f.mul(CORRECTION, transformMatrix, transformMatrix);
                    OpenMatrix4f.mul(CORRECTION, transformMatrix2, transformMatrix2);
                    int n = 40;
                    double r = 0.2;
                    double t = 0.01;

                    for (int i = 0; i < n; ++i) {
                        double theta = 6.283185307179586 * (new Random()).nextDouble();
                        double phi = ((new Random()).nextDouble() - 0.5) * Math.PI * t / r;
                        double x = r * Math.cos(phi) * Math.cos(theta);
                        double y = r * Math.cos(phi) * Math.sin(theta);
                        double z = r * Math.sin(phi);
                        Vec3f direction = new Vec3f((float) x, (float) y, (float) z);
                        OpenMatrix4f rotation = (new OpenMatrix4f()).rotate(-((float) Math.toRadians((double) ((LivingEntity) entitypatch.getOriginal()).yBodyRotO)), new Vec3f(0.0F, 1.0F, 0.0F));
                        rotation.rotate((transformMatrix.m11 + 0.07F) * 1.5F, new Vec3f(1.0F, 0.0F, 0.0F));
                        OpenMatrix4f.transform3v(rotation, direction, direction);
                        entitypatch.getOriginal().level().addParticle(ParticleTypes.FLAME, (double) transformMatrix.m30 + ((LivingEntity) entitypatch.getOriginal()).getX(), (double) transformMatrix.m31 + ((LivingEntity) entitypatch.getOriginal()).getEyeY()-1.12, (double) transformMatrix.m32 + ((LivingEntity) entitypatch.getOriginal()).getZ(), (double) (transformMatrix2.m30 - transformMatrix.m30 + direction.x), (double) (transformMatrix2.m31 - transformMatrix.m31 + direction.y), (double) (transformMatrix2.m32 - transformMatrix.m32 + direction.z));
                    }

                    HitResult ray = entitypatch.getOriginal().pick(10.0, 0.7F, false);
                    entitypatch.getOriginal().level().addParticle(EpicFightParticles.LASER.get(), (double) transformMatrix.m30 + ((LivingEntity) entitypatch.getOriginal()).getX(), (double) transformMatrix.m31 + ((LivingEntity) entitypatch.getOriginal()).getEyeY()-1.12, (double) transformMatrix.m32 + ((LivingEntity) entitypatch.getOriginal()).getZ(), ray.getLocation().x, ray.getLocation().y, ray.getLocation().z);
                }, AnimationEvent.Side.CLIENT));
        GUHAO_BIU = (new BasicAttackAnimation(0.1F, 0.315F,0.315F, 0.385F, 1.05F, GuHaoColliderPreset.ENDER_LASER, biped.toolR, "biped/guhao_biu", biped))
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.5F))
                .addProperty(AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(34.4F))
                .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.NONE)
                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT)
                .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST)
                .addProperty(AnimationProperty.AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
                .addProperty(AnimationProperty.AttackAnimationProperty.EXTRA_COLLIDERS, 2)
                .addProperty(AnimationProperty.ActionAnimationProperty.RESET_PLAYER_COMBO_COUNTER, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.195F, (entitypatch, self, params) -> {
                    if (entitypatch instanceof PlayerPatch) {
                        entitypatch.getOriginal().level().playSound((Player) entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.BUZZ.get(), SoundSource.PLAYERS, 0.6F, 1.5F);
                    }

                }, AnimationEvent.Side.CLIENT), AnimationEvent.TimeStampedEvent.create(0.345F, (entitypatch, self, params) -> {
                    if (entitypatch instanceof PlayerPatch) {
                        entitypatch.getOriginal().level().playSound((Player) entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.LASER_BLAST.get(), SoundSource.PLAYERS, 0.7F, 1.0F);
                    }
                    OpenMatrix4f transformMatrix = entitypatch.getArmature().getBindedTransformFor(entitypatch.getAnimator().getPose(-0.5F), Armatures.BIPED.toolR);
                    OpenMatrix4f transformMatrix2 = entitypatch.getArmature().getBindedTransformFor(entitypatch.getAnimator().getPose(-0.5F), Armatures.BIPED.toolR);
                    transformMatrix.translate(new Vec3f(0.0F, -0.6F, -0.3F));
                    transformMatrix2.translate(new Vec3f(0.0F, -0.8F, -0.3F));
                    OpenMatrix4f CORRECTION = (new OpenMatrix4f()).rotate(-((float) Math.toRadians(entitypatch.getOriginal().yRotO + 180.0F)), new Vec3f(0.0F, 1.0F, 0.0F));
                    OpenMatrix4f.mul(CORRECTION, transformMatrix, transformMatrix);
                    OpenMatrix4f.mul(CORRECTION, transformMatrix2, transformMatrix2);
                    int n = 40;
                    double r = 0.2;
                    double t = 0.01;

                    for (int i = 0; i < n; ++i) {
                        double theta = 6.283185307179586 * (new Random()).nextDouble();
                        double phi = ((new Random()).nextDouble() - 0.5) * Math.PI * t / r;
                        double x = r * Math.cos(phi) * Math.cos(theta);
                        double y = r * Math.cos(phi) * Math.sin(theta);
                        double z = r * Math.sin(phi);
                        Vec3f direction = new Vec3f((float) x, (float) y, (float) z);
                        OpenMatrix4f rotation = (new OpenMatrix4f()).rotate(-((float) Math.toRadians(entitypatch.getOriginal().yBodyRotO)), new Vec3f(0.0F, 1.0F, 0.0F));
                        rotation.rotate((transformMatrix.m11 + 0.07F) * 1.5F, new Vec3f(1.0F, 0.0F, 0.0F));
                        OpenMatrix4f.transform3v(rotation, direction, direction);
                        entitypatch.getOriginal().level().addParticle(ParticleType.BLOOD_FIRE_FLAME.get(), (double) transformMatrix.m30 + entitypatch.getOriginal().getX(), (double) transformMatrix.m31 + entitypatch.getOriginal().getEyeY()-1.12, (double) transformMatrix.m32 + entitypatch.getOriginal().getZ(), transformMatrix2.m30 - transformMatrix.m30 + direction.x, transformMatrix2.m31 - transformMatrix.m31 + direction.y, (double) (transformMatrix2.m32 - transformMatrix.m32 + direction.z));
                    }

                    HitResult ray = entitypatch.getOriginal().pick(10.0, 0.7F, false);
                    entitypatch.getOriginal().level().addParticle(ParticleType.GUHAO_LASER.get(), (double) transformMatrix.m30 + entitypatch.getOriginal().getX(), (double) transformMatrix.m31 + entitypatch.getOriginal().getEyeY()-1.12, (double) transformMatrix.m32 + ((LivingEntity) entitypatch.getOriginal()).getZ(), ray.getLocation().x, ray.getLocation().y, ray.getLocation().z);
                }, AnimationEvent.Side.CLIENT));

        BLOOD_JUDGEMENT = (new BasicMultipleAttackAnimation(0.1F, "biped/blood_judgement", biped,
                new AttackAnimation.Phase(0.0F, 0.3F, 0.52F, 1.20F, 1.20F, biped.toolR, GuHaoColliderPreset.SACRIFICE_ATTACK)
                        .addProperty(AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(24.0F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.5F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(4.0F))
//                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, com.guhao.init.Sounds.DAO1.get())
                        .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.HOLD),
                new AttackAnimation.Phase(1.20f, 2.85F, 3.174F, 3.2F, Float.MAX_VALUE, biped.rootJoint, GuHaoColliderPreset.GUHAO_BATTOJUTSU_DASH)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.0F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, ParticleType.BLOOD_CUT_BIG)
                        .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.NEUTRALIZE)))
                .addProperty(AnimationProperty.AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
                .addProperty(AnimationProperty.AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
                .addProperty(AnimationProperty.AttackAnimationProperty.EXTRA_COLLIDERS, 4)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.RESET_PLAYER_COMBO_COUNTER, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.7F, 1.32F))
                .addProperty(AnimationProperty.StaticAnimationProperty.TIME_STAMPED_EVENTS, new AnimationEvent.TimeStampedEvent[]{
//                        AnimationEvent.TimeStampedEvent.create(0.0F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_judgement_geo(ep), AnimationEvent.Side.BOTH),
                        //AnimationEvent.TimeStampedEvent.create(0f, (ep, anim, objs) -> CameraEvents.SetAnim(BLOOD_JUDGEMENT_C, ep.getOriginal(), true), AnimationEvent.Side.CLIENT),
                        AnimationEvent.TimeStampedEvent.create(0.295F, Animations.ReusableSources.PLAY_SOUND, AnimationEvent.Side.SERVER).params(EpicFightSounds.SWORD_IN.get()),
                        AnimationEvent.TimeStampedEvent.create(0.412F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_judgement_sound_1(ep), AnimationEvent.Side.SERVER),
//                        AnimationEvent.TimeStampedEvent.create(0.744F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_judgement_post(ep), AnimationEvent.Side.CLIENT),
                        AnimationEvent.TimeStampedEvent.create(0.745F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_judgement_p2(ep), AnimationEvent.Side.BOTH),
                        AnimationEvent.TimeStampedEvent.create(0.795F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_judgement_p2(ep), AnimationEvent.Side.BOTH),
                        AnimationEvent.TimeStampedEvent.create(0.845F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_judgement_p2(ep), AnimationEvent.Side.BOTH),
                        AnimationEvent.TimeStampedEvent.create(0.895F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_judgement_p2(ep), AnimationEvent.Side.BOTH),
                        AnimationEvent.TimeStampedEvent.create(0.945F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_judgement_p2(ep), AnimationEvent.Side.BOTH),
                        AnimationEvent.TimeStampedEvent.create(0.955F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_avatar(ep), AnimationEvent.Side.BOTH),
                        AnimationEvent.TimeStampedEvent.create(0.955F, Animations.ReusableSources.PLAY_SOUND, AnimationEvent.Side.SERVER).params(EpicFightSounds.SWORD_IN.get()),
                        AnimationEvent.TimeStampedEvent.create(1.00F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_judgement_effect(ep), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(1.185F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_judgement_sound_2(ep), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(1.20F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_judgement_cut(ep), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(1.20F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_blade_whirl_wind(ep), AnimationEvent.Side.BOTH),
                        AnimationEvent.TimeStampedEvent.create(1.2001F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_judgement_p1(ep), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(1.45F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_judgement_cut(ep), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(1.45F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_blade_whirl_wind(ep), AnimationEvent.Side.BOTH),
                        AnimationEvent.TimeStampedEvent.create(1.48F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_judgement_sound_2(ep), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(1.70F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_judgement_cut(ep), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(1.70F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_blade_whirl_wind(ep), AnimationEvent.Side.BOTH),
                        AnimationEvent.TimeStampedEvent.create(2.20F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_judgement_cut(ep), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(2.20F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_blade_whirl_wind(ep), AnimationEvent.Side.BOTH),
                        AnimationEvent.TimeStampedEvent.create(2.21F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_judgement_sound_2(ep), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(2.46F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_judgement_cut(ep), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(2.46F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_blade_whirl_wind(ep), AnimationEvent.Side.BOTH),
                        AnimationEvent.TimeStampedEvent.create(2.71F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_judgement_cut(ep), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(2.71F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_blade_whirl_wind(ep), AnimationEvent.Side.BOTH),
                        AnimationEvent.TimeStampedEvent.create(2.93F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_avatar(ep), AnimationEvent.Side.BOTH),
                        AnimationEvent.TimeStampedEvent.create(2.9435F, Animations.ReusableSources.FRACTURE_GROUND_SIMPLE, AnimationEvent.Side.CLIENT).params(new Vec3f(0.0F, -0.25F, 0f), Armatures.BIPED.rootJoint, 2.5, 2.0F),
                        AnimationEvent.TimeStampedEvent.create(2.945F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_judgement_sound_3(ep), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(2.9455F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_judgement_p3(ep), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(2.946F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_blade(ep), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(2.9465F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_needles(ep), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(2.948F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_judgement_p2(ep), AnimationEvent.Side.CLIENT),

//                        AnimationEvent.TimeStampedEvent.create(3.165F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_judgement_geo2(ep), AnimationEvent.Side.BOTH),
                })
//                .addEvents(AnimationProperty.StaticAnimationProperty.TIME_PERIOD_EVENTS, new AnimationEvent.TimePeriodEvent[] {
//                        AnimationEvent.TimePeriodEvent.create(2.94F,3.149999999999999999F,(ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_judgement_hurt(ep), AnimationEvent.Side.SERVER)
//                })
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, BLOOD_JUDGEMENT_SPEED)
                .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION,false)
                .addStateRemoveOld(EntityState.HURT_LEVEL,10)
        ;
//                .addProperty(ClientAnimationProperties.TRAIL_EFFECT, newTFL(newTF(0.194F, 3.2F, 20, biped.toolR, InteractionHand.MAIN_HAND)));

        BIG_STAR = (new BasicAttackAnimationEx(0.1F, "biped/bigstar", biped,
                new AttackAnimation.Phase(0.0F, 9.5F, 10.6F, Float.MAX_VALUE, Float.MAX_VALUE, biped.rootJoint, GuHaoColliderPreset.BIGSTAR)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(99999999999F))
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.NONE)))
                .addProperty(AnimationProperty.AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
                .addProperty(AnimationProperty.AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 11.0F))
                .addProperty(AnimationProperty.StaticAnimationProperty.TIME_STAMPED_EVENTS, new AnimationEvent.TimeStampedEvent[]{
                        //AnimationEvent.TimeStampedEvent.create(0f, (ep, anim, objs) -> CameraEvents.SetAnim(BLOOD_JUDGEMENT_C, ep.getOriginal(), true), AnimationEvent.Side.CLIENT),
                        AnimationEvent.TimeStampedEvent.create(9F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.explosionEffect(ep), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(10.5F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.explosion2(ep), AnimationEvent.Side.SERVER),
                })
                .addEvents(AnimationProperty.StaticAnimationProperty.TIME_PERIOD_EVENTS, new AnimationEvent.TimePeriodEvent[] {
                        AnimationEvent.TimePeriodEvent.create(9F,10.65F,(ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.explosion(ep), AnimationEvent.Side.SERVER)
                })
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, BIGSTAR);
//                .addProperty(ClientAnimationProperties.TRAIL_EFFECT, newTFL(newTF(9F, 10.6F, 100, biped.toolR, InteractionHand.MAIN_HAND)));
        DENG_LONG = (new BasicMultipleAttackAnimation(0.15F, "biped/deng_long", biped,
                new SpecialAttackNoRotAnimation.Phase(0.0F, 2.016F, 2.066F, 2.133F, 2.133F, biped.toolR, GuHaoColliderPreset.SACRIFICE_ATTACK)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.66F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, ParticleType.BLOOD_CUT_BIG)
                        .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.HOLD),
                new SpecialAttackNoRotAnimation.Phase(2.133F, 2.133F, 2.183F, 2.25F, 2.25F, biped.toolR, GuHaoColliderPreset.SACRIFICE_ATTACK)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.66F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_SHARP.get())
                        .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.HOLD),
                new SpecialAttackNoRotAnimation.Phase(2.25F, 2.25F, 2.3F, 2.366F, 2.366F, biped.toolR, GuHaoColliderPreset.SACRIFICE_ATTACK)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.66F))

                        .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.HOLD),
                new SpecialAttackNoRotAnimation.Phase(2.366F, 2.366F, 2.416F, 2.483F, 2.483F, biped.toolR, GuHaoColliderPreset.SACRIFICE_ATTACK)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.66F))

                        .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.HOLD),
                new SpecialAttackNoRotAnimation.Phase(2.6F, 2.6F, 2.65F, 2.716F, 2.716F, biped.toolR, GuHaoColliderPreset.SACRIFICE_ATTACK)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.66F))

                        .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.HOLD),
                new SpecialAttackNoRotAnimation.Phase(2.366F, 2.366F, 2.416F, 2.483F, 2.483F, biped.toolR, GuHaoColliderPreset.SACRIFICE_ATTACK)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.66F))

                        .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.HOLD),
                new SpecialAttackNoRotAnimation.Phase(2.716F, 2.716F, 2.766F, 2.833F, 3.0F, biped.toolR, GuHaoColliderPreset.SACRIFICE_ATTACK)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.66F))

                        .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.HOLD)))
                .addProperty(AnimationProperty.AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
                .addProperty(AnimationProperty.AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 3.0F))
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFuncUtils.TraceLockedTargetEx(12.5F))
                .addProperty(AnimationProperty.StaticAnimationProperty.TIME_STAMPED_EVENTS, new AnimationEvent.TimeStampedEvent[]{
                        AnimationEvent.TimeStampedEvent.create(0.365F, (ep, anim, objs) -> {Vec3 pos = ep.getOriginal().position();ep.playSound(EpicFightSounds.ROLL.get(), 0, 0);ep.getOriginal().level().addAlwaysVisibleParticle(EpicFightParticles.AIR_BURST.get(), pos.x, pos.y + ep.getOriginal().getBbHeight() * 0.5D, pos.z, 0, -1, 2);}, AnimationEvent.Side.CLIENT),
                        AnimationEvent.TimeStampedEvent.create(2.15F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_explosion(ep), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(2.20F, Animations.ReusableSources.FRACTURE_GROUND_SIMPLE, AnimationEvent.Side.CLIENT).params(new Vec3f(0.0F, -0.25F, 0f), Armatures.BIPED.rootJoint, 1.25, 0.2F),
                        AnimationEvent.TimeStampedEvent.create(2.56F, (ep, anim, objs) -> {
                            Vec3 playerPos = ep.getOriginal().position();
                            Vec3 lookVec = ep.getOriginal().getLookAngle();
                            Vec3 frontPos = playerPos.add(lookVec.x * 1, lookVec.y * 1, lookVec.z * 1);
                            if (ep.getOriginal().level() instanceof ServerLevel serverLevel) serverLevel.sendParticles(ParticleType.ONE_JC_BLOOD_JUDGEMENT_LONG.get(), frontPos.x, frontPos.y, frontPos.z, 1, 0.05, 0, 0.05, 1);
                        }, AnimationEvent.Side.SERVER)
                })
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, DENGLONG);
                
//                .addProperty(ClientAnimationProperties.TRAIL_EFFECT, newTFL(newTF(0.93F, 1.35F, 9, biped.toolR, InteractionHand.MAIN_HAND)));
        GUHAO_UCHIGATANA_SCRAP = (new SpecialAttackAnimation(0.05F, 0.135F, 0.155F, 0.155F, GuHaoColliderPreset.CUT_IN, biped.rootJoint, "biped/uchigatana_scrap", biped))
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F))
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                .addProperty(AnimationProperty.AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, (a1,b1,c1,d1,e1) -> 0.7F)
                .addEvents(
                        AnimationEvent.TimeStampedEvent.create(0.15F, Animations.ReusableSources.PLAY_SOUND, AnimationEvent.Side.CLIENT).params(EpicFightSounds.SWORD_IN.get()),
                        AnimationEvent.TimeStampedEvent.create(0.1501F, (ep, anim, objs) -> {
                            PlayerPatch<?> playerPatch = EpicFightCapabilities.getEntityPatch(ep.getOriginal(), PlayerPatch.class);
                            ServerPlayerPatch serverPlayerPatch = EpicFightCapabilities.getEntityPatch(ep.getOriginal(), ServerPlayerPatch.class);
                            if (playerPatch != null && serverPlayerPatch != null && playerPatch.getSkill(GuHaoSkills.GUHAO_PASSIVE) != null) {
                                playerPatch.getSkill(GuHaoSkills.GUHAO_PASSIVE).getDataManager().setData(GuHaoSkillDataKeys.SHEATH.get(), true);
                                serverPlayerPatch.modifyLivingMotionByCurrentItem();
                            }
                        }, AnimationEvent.Side.SERVER));
        JIANQIE = (new AttackAnimation(0.1F, 0.35F,0.35F, 0.6F, 0.6F, null, biped.toolR, "biped/jianqie", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG))
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.05f))
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.5f))
                .addProperty(AnimationProperty.AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)

                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.TIME_STAMPED_EVENTS, new AnimationEvent.TimeStampedEvent[]{
                        AnimationEvent.TimeStampedEvent.create(0.36F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.dodge(ep), AnimationEvent.Side.SERVER),
                })
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, AnimationEvent.create((entitypatch, animation, params) -> {
                    PlayerPatch<?> pp1 = EpicFightCapabilities.getEntityPatch(entitypatch.getOriginal(), PlayerPatch.class);
                    if (pp1 != null) {
                        pp1.getEventListener().addEventListener(PlayerEventListener.EventType.HURT_EVENT_PRE, UUID.fromString("f6f8c8d8-6e54-4b02-8f18-7c6f3e6e3f6f"), (event) -> {
                            event.setResult(AttackResult.ResultType.MISSED);
                            pp1.playSound(Sounds.FORESIGHT.get(),1f,1f);
                            pp1.getOriginal().addEffect(new MobEffectInstance(Effect.WUDI.get(),40,40));
                            pp1.getOriginal().addEffect(new MobEffectInstance(EpicFightMobEffects.STUN_IMMUNITY.get(),40,40));
                            if (pp1.getOriginal() != null && pp1.getOriginal().getType() == net.minecraft.world.entity.EntityType.PLAYER) {
                                String uid = pp1.getOriginal().getUUID().toString();
                                ServerEventExtra.addUserDamage(uid, event.getAmount() * 1.5f);
                                ServerEventExtra.updateKeepUntil(uid, 5000);
                                Network.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) pp1.getOriginal()),
                                        new DamagePackage("emit",
                                                ServerEventExtra.getUserDamage(uid),
                                                ServerEventExtra.getDamageCount(uid),
                                                event.getAmount() * 1.5f,
                                                -1));
                            }
                            event.setCanceled(true);
                        });
                        pp1.getEventListener().addEventListener(PlayerEventListener.EventType.HURT_EVENT_PRE, UUID.fromString("f6f8c8d8-6e54-4b02-8f18-7c6f3e6e3f7f"), (event) -> {
                            if (pp1.getOriginal().hasEffect(Effect.WUDI.get())) {
                                event.setResult(AttackResult.ResultType.MISSED);
                                event.setCanceled(true);
                            }
                        });
                    }
                }, AnimationEvent.Side.SERVER))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create((entitypatch, animation, params) -> {
                    PlayerPatch<?> pp3 = EpicFightCapabilities.getEntityPatch(entitypatch.getOriginal(), PlayerPatch.class);
                    if (pp3 != null) {
                        pp3.getEventListener().removeListener(PlayerEventListener.EventType.HURT_EVENT_PRE, UUID.fromString("f6f8c8d8-6e54-4b02-8f18-7c6f3e6e3f7f"));
                        entitypatch.getOriginal().removeEffect(Effect.WUDI.get());
                        entitypatch.getOriginal().removeEffect(EpicFightMobEffects.STUN_IMMUNITY.get());
                    }

                }, AnimationEvent.Side.SERVER))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, JIANQIE_SPEED);;
        HERRSCHER_AUTO_3 = (new BasicMultipleAttackAnimation(0.2F, "biped/herrscher_auto_3", biped, new AttackAnimation.Phase(0.0F, 0.45F, 0.55F, 0.75F, Float.MAX_VALUE, biped.toolR, WOMWeaponColliders.HERSCHER_CHARGE_1))).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.6F)).addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(5.0F)).addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.NONE).addProperty(AnimationProperty.AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F);
        SETTLEMENT = (new SpecialAttackNoRotAnimation(0.2F, 0.35F, 0.35F, 0.75F, 1.05F, WOMWeaponColliders.FATAL_DRAW_DASH, biped.rootJoint, "biped/settlement", biped))
                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_SHARP.get())
                .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, ParticleType.BLOOD_CUT_BIG)
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                .addProperty(AnimationProperty.AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
                .addProperty(AnimationProperty.AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOCROT_TARGET)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, AnimationEvent.create((entitypatch, animation, params) -> {
                        PlayerPatch<?> pp1 = EpicFightCapabilities.getEntityPatch(entitypatch.getOriginal(), PlayerPatch.class);
                    if (pp1 != null) {
                        pp1.getEventListener().addEventListener(PlayerEventListener.EventType.HURT_EVENT_PRE, UUID.fromString("f6f8c8d8-6e54-4b02-8f18-7c6f3e6e3f6f"), (event) -> {
                            event.setResult(AttackResult.ResultType.MISSED);
                            pp1.playSound(Sounds.FORESIGHT.get(),1f,1f);
                            pp1.getOriginal().addEffect(new MobEffectInstance(Effect.WUDI.get(),80,80));
                            pp1.getOriginal().addEffect(new MobEffectInstance(EpicFightMobEffects.STUN_IMMUNITY.get(),80,80));
                            if (pp1.getOriginal() != null && pp1.getOriginal().getType() == net.minecraft.world.entity.EntityType.PLAYER) {
                                String uid = pp1.getOriginal().getUUID().toString();
                                ServerEventExtra.addUserDamage(uid, event.getAmount() * 1.5f);
                                ServerEventExtra.updateKeepUntil(uid, 5000);
                                Network.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) pp1.getOriginal()),
                                        new DamagePackage("emit",
                                                ServerEventExtra.getUserDamage(uid),
                                                ServerEventExtra.getDamageCount(uid),
                                                event.getAmount() * 1.5f,
                                                -1));
                            }
                            event.setCanceled(true);
                        });
                        pp1.getEventListener().addEventListener(PlayerEventListener.EventType.HURT_EVENT_PRE, UUID.fromString("f6f8c8d8-6e54-4b02-8f18-7c6f3e6e3f7f"), (event) -> {
                            if (pp1.getOriginal().hasEffect(Effect.WUDI.get())) {
                                event.setResult(AttackResult.ResultType.MISSED);
                                event.setCanceled(true);
                            }
                        });
                    }
                }, AnimationEvent.Side.SERVER))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create((entitypatch, animation, params) -> {
                    PlayerPatch<?> pp3 = EpicFightCapabilities.getEntityPatch(entitypatch.getOriginal(), PlayerPatch.class);
                    if (pp3 != null) {
                        pp3.getEventListener().removeListener(PlayerEventListener.EventType.HURT_EVENT_PRE, UUID.fromString("f6f8c8d8-6e54-4b02-8f18-7c6f3e6e3f7f"));
                        entitypatch.getOriginal().removeEffect(Effect.WUDI.get());
                        entitypatch.getOriginal().removeEffect(EpicFightMobEffects.STUN_IMMUNITY.get());
                    }

                }, AnimationEvent.Side.SERVER))


                .addEvents(AnimationEvent.TimeStampedEvent.create(0.2F, KATANA_IN, AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(0.8F, (entitypatch, self, params) -> {
            Entity entity = entitypatch.getOriginal();
            entitypatch.getOriginal().level().addParticle(ParticleType.ENTITY_AFTER_IMG_BLOOD.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0.0, 0.0);
        }, AnimationEvent.Side.BOTH),
                        AnimationEvent.TimeStampedEvent.create(0.45F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.dodge(ep), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(0.8F, (entitypatch, self, params) -> {
            if (entitypatch instanceof PlayerPatch) {
                entitypatch.updateMotion(true);
            }

        }, AnimationEvent.Side.SERVER), AnimationEvent.TimeStampedEvent.create(1.9F, (entitypatch, self, params) -> {
            entitypatch.playSound(EpicFightSounds.WHOOSH.get(), 0.0F, 0.0F);
        }, AnimationEvent.Side.SERVER));
        GUHAO_EX_4 = new BasicMultipleAttackAnimation(0.1F, "biped/ex_4", biped,
                new BasicMultipleAttackAnimation.Phase(0.0F, 0.34F, 0.6F, 0.9F, 0.89F, biped.toolR, GuHaoColliderPreset.LARGER)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.05F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
                        .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG),
                new BasicMultipleAttackAnimation.Phase(0.9F, 0.9F, 1.0F, 1.40F, 1.65F, biped.rootJoint, GuHaoColliderPreset.LARGER)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
        )
                .addProperty(AnimationProperty.AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
                .addProperty(AnimationProperty.AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFuncUtils.TraceLockedTargetEx(12.5F))
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 0.6F))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.TIME_STAMPED_EVENTS, new AnimationEvent.TimeStampedEvent[] {
                        AnimationEvent.TimeStampedEvent.create(0.08F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_tornado(ep), AnimationEvent.Side.BOTH),
                        AnimationEvent.TimeStampedEvent.create(0.08F, (ep, anim, objs) -> ep.playSound(EpicFightSounds.ROCKET_JUMP.get(),1.0f,1.0f), AnimationEvent.Side.CLIENT),
                        AnimationEvent.TimeStampedEvent.create(0.1F, (ep, anim, objs) -> {Vec3 pos = ep.getOriginal().position();ep.playSound(EpicFightSounds.ROLL.get(), 1.0f, 1.0f);ep.getOriginal().level().addAlwaysVisibleParticle(EpicFightParticles.AIR_BURST.get(), pos.x, pos.y + ep.getOriginal().getBbHeight() * 0.5D, pos.z, 0, -1, 2);}, AnimationEvent.Side.CLIENT),
                        AnimationEvent.TimeStampedEvent.create(0.545F, Animations.ReusableSources.FRACTURE_GROUND_SIMPLE, AnimationEvent.Side.SERVER).params(new Vec3f(0.0F, -0.32F, 0.0F), Armatures.BIPED.rootJoint, 2.1, 1.2F),
                        AnimationEvent.TimeStampedEvent.create(0.95F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_explosion(ep), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(0.98F, Animations.ReusableSources.FRACTURE_GROUND_SIMPLE, AnimationEvent.Side.SERVER).params(new Vec3f(0.0F, -0.32F, 0.0F), Armatures.BIPED.rootJoint, 3.74, 1.6F),
                        AnimationEvent.TimeStampedEvent.create(0.9F, (ep, anim, objs) -> {
                            PlayerPatch<?> playerPatch = EpicFightCapabilities.getEntityPatch(ep.getOriginal(), PlayerPatch.class);
                            ServerPlayerPatch serverPlayerPatch = EpicFightCapabilities.getEntityPatch(ep.getOriginal(), ServerPlayerPatch.class);
                            if (playerPatch != null && serverPlayerPatch != null && playerPatch.getSkill(GuHaoSkills.GUHAO_PASSIVE) != null) {
                                playerPatch.playSound(EpicFightSounds.SWORD_IN.get(), 1.25F,1.0F,1.0F);
                                playerPatch.getSkill(GuHaoSkills.GUHAO_PASSIVE).getDataManager().setDataSync(GuHaoSkillDataKeys.SHEATH.get(), true,serverPlayerPatch.getOriginal());
                                serverPlayerPatch.modifyLivingMotionByCurrentItem(false);
                            }
                        }, AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(0.99F, (ep, anim, objs) -> BattleUtils.Guhao_Battle_utils.blood_devour(ep), AnimationEvent.Side.SERVER),

                })
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, EX_4);
        KATANA_AUTO_3 = (new BasicMultipleAttackAnimation(0.2F, 0.25F, 0.4F, 0.75F, null, biped.toolR, "biped/katana_auto_3", biped)).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F)).addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.HOLD).addProperty(AnimationProperty.AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F);
    }


    public static final AnimationProperty.PlaybackSpeedModifier NB = (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> 1.25F;
    public static final AnimationProperty.PlaybackSpeedModifier SC = (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> 0.666F;
    public static final AnimationProperty.PlaybackSpeedModifier DENGLONG = (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> 1.25F;
    public static final AnimationProperty.PlaybackSpeedModifier EX_4 = (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> 0.616F;
    public static final AnimationProperty.PlaybackSpeedModifier BLOOD_JUDGEMENT_SPEED = (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> 1.0F;
    public static final AnimationProperty.PlaybackSpeedModifier BIGSTAR = (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> 0.75F;
    public static final AnimationProperty.PlaybackSpeedModifier JIANQIE_SPEED = (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> 0.8F;

    public static final AnimationEvent.AnimationEventConsumer KATANA_IN = (entitypatch, self, params) -> {
        entitypatch.playSound(EpicFightSounds.SWORD_IN.get(), 0.0F, 0.0F);
    };
    public static List<TrailInfo> newTFL(TrailInfo... tfs) {
        return Lists.newArrayList(tfs);
    }

    public static TrailInfo newTF(float start, float end, int lifetime, Joint joint, InteractionHand hand) {
        JsonObject je = new JsonObject();
        je.addProperty("joint", joint.getName());
        je.addProperty("start_time", start);
        je.addProperty("end_time", end);
        je.addProperty("item_skin_hand", hand.toString());
        je.addProperty("lifetime", lifetime);
        return TrailInfo.deserialize(je);
    }
}
