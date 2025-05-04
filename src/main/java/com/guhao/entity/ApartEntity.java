package com.guhao.entity;

import com.dfdyz.epicacg.registry.Sounds;
import com.dfdyz.epicacg.utils.MoveCoordFuncUtils;
import com.guhao.init.Entities;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reascer.wom.animation.attacks.BasicAttackNoRotAnimation;
import reascer.wom.gameasset.WOMAnimations;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageType;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

import java.util.Set;

public class ApartEntity extends TamableAnimal {
//    public ApartEntity(ServerPlayer owner,LivingEntity target){
//        super(Entities.APART.get(), owner.level());
//
//        tame(owner);
//        setTarget(target);
//    }

    public ApartEntity(ServerPlayer owner){
        super(Entities.APART.get(), owner.level());
        setMaxUpStep(0f);
        xpReward = 0;
        setNoAi(false);
        setItemSlot(EquipmentSlot.MAINHAND, owner.getItemBySlot(EquipmentSlot.MAINHAND).copy());
        tame(owner);
    }
//    public ApartEntity(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
//        super(p_21803_, p_21804_);
//        setMaxUpStep(0f);
//        xpReward = 0;
//        setNoAi(false);
//        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GUHAO.get().getDefaultInstance().getItem());
//
//    }

    public ApartEntity(EntityType<ApartEntity> type, Level world) {
        super(type, world);
        setMaxUpStep(0f);
        xpReward = 0;
        setNoAi(false);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
    @Override
    public void tame(@NotNull Player player) {
        super.tame(player);
        setItemSlot(EquipmentSlot.MAINHAND, player.getItemBySlot(EquipmentSlot.MAINHAND).copy());
        setTarget(EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class).getTarget());
    }
    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(@NotNull Entity entityIn) {
    }

    @Override
    protected void pushEntities() {
    }

    public static void init() {
    }


//    @Override
//    public void tame(@NotNull Player player) {
//        super.tame(player);
//        setTarget(EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class).getTarget());
//        if (this.getTarget() != null) this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(this.getTarget().getX(), this.getTarget().getEyeY() - 0.15, this.getTarget().getZ()));
//    }
//    @Nullable
//    @Override
//    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
//        return null;
//    }
    @Override
    public void tick() {
        super.tick();
        if (this.getTarget() != null) this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(this.getTarget().getX(), this.getTarget().getEyeY() - 0.15, this.getTarget().getZ()));
//        if(getOwner() == null) {
//            this.discard();
//        }
        if (this.tickCount == 1) {
            StaticAnimation animation = WOMAnimations.HERRSCHER_VERDAMMNIS;
            if (animation instanceof BasicAttackNoRotAnimation basicAttackNoRotAnimation) {
                basicAttackNoRotAnimation
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.05f))
                        .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
                        .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.NONE)
                        .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(2.0F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(100.0F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
                        .addProperty(AnimationProperty.AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageType.WEAPON_INNATE))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_SHARP.get())
                        .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 3.0F))
                        .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFuncUtils.TraceLockedTargetEx(7.5F));
                EpicFightCapabilities.getEntityPatch(this, LivingEntityPatch.class).playAnimationSynchronized(basicAttackNoRotAnimation, 0.0f);
            }
        }
        if (this.tickCount >= 14) {
            level.playSound(null, getX(), getY(), getZ(), Sounds.DualSword_SA1_2.get(), getSoundSource(), 1.0F, 1.0F);
            this.discard();
        }
    }
    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(0, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, LivingEntity.class));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, true) {

            @Override
            protected double getAttackReachSqr(@NotNull LivingEntity entity) {
                return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
            }

        });

    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(EpicFightAttributes.WEIGHT.get())
                .add(Attributes.MOVEMENT_SPEED, 0.4F)
                .add(EpicFightAttributes.ARMOR_NEGATION.get())
                .add(EpicFightAttributes.IMPACT.get())
                .add(EpicFightAttributes.MAX_STRIKES.get())
                .add(Attributes.ATTACK_DAMAGE);
    }
    @Override
    public boolean hurt(@NotNull DamageSource source, float p_27568_) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel serverLevel, @NotNull AgeableMob ageableMob) {
        return null;
    }
}
