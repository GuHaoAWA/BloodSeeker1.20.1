package com.guhao.epicfight.skills;

import com.google.common.collect.Maps;

import com.guhao.GuhaoMod;
import com.guhao.epicfight.GuHaoAnimations;
import com.guhao.init.Effect;
import com.guhao.init.Key;
import com.guhao.init.ParticleType;
import com.guhao.network.ParticlePacket;
import com.guhao.stars.regirster.Sounds;
import com.guhao.utils.ArrayUtils;
import com.nameless.falchion.gameasset.FalchionAnimations;
import io.netty.buffer.Unpooled;
import net.corruptdog.cdm.gameasset.CorruptAnimations;
import net.mehvahdjukaar.dummmmmmy.network.NetworkHandler;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.PacketDistributor;
import reascer.wom.gameasset.WOMAnimations;
import yesman.epicfight.api.animation.StaticAnimationProvider;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.events.engine.ControllEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.network.client.CPExecuteSkill;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.effect.EpicFightMobEffects;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.guhao.epicfight.GuHaoSkillDataKeys.IS_CTRL_DOWN;
import static com.guhao.epicfight.GuHaoSkillDataKeys.SHEATH;

public class SacrificeSkill extends WeaponInnateSkill {
    private final StaticAnimationProvider[] animations = new StaticAnimationProvider[3];
    private final Map<StaticAnimation, AttackAnimation> comboAnimation = new ConcurrentHashMap<>();
    private static final UUID EVENT_UUID = UUID.fromString("d706b5bc-b98b-cc49-b83e-16ae590db349");

    public SacrificeSkill(Builder<? extends Skill> builder) {
        super(builder);
        this.animations[0] = () -> FalchionAnimations.FALCHION_FORWARD;
        this.animations[1] = () -> FalchionAnimations.FALCHION_BACKWARD;
        this.animations[2] = () -> FalchionAnimations.FALCHION_SIDE;
    }
    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.HURT_EVENT_POST, EVENT_UUID, (event) -> {
            ServerPlayerPatch executer = event.getPlayerPatch();
            DynamicAnimation animation = executer.getAnimator().getPlayerFor(null).getAnimation();
            if (animation == FalchionAnimations.FALCHION_SIDE || animation == FalchionAnimations.FALCHION_AUTO3 || animation == GuHaoAnimations.SETTLEMENT || animation == GuHaoAnimations.BLOOD_JUDGEMENT) {
                event.getDamageSource().setStunType(StunType.NONE);
            }
        });
    }

    @Override
    public void onRemoved(SkillContainer container) {
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.HURT_EVENT_POST, EVENT_UUID);
    }
    @Override
    public WeaponInnateSkill registerPropertiesToAnimation() {
        this.comboAnimation.clear();

        this.comboAnimation.put(GuHaoAnimations.GUHAO_UCHIGATANA_SHEATHING_AUTO, (AttackAnimation) GuHaoAnimations.GUHAO_UCHIGATANA_SHEATHING_AUTO_EX.newTimePair(0.0F, 0.056F).addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false));
        this.comboAnimation.put(GuHaoAnimations.GUHAO_UCHIGATANA_SHEATHING_DASH, (AttackAnimation) GuHaoAnimations.GUHAO_UCHIGATANA_SHEATHING_AUTO_EX.newTimePair(0.0F, 0.056F).addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false));
        this.comboAnimation.put(GuHaoAnimations.GUHAO_UCHIGATANA_SHEATH_AIR_SLASH, (AttackAnimation) GuHaoAnimations.GUHAO_UCHIGATANA_SHEATHING_AUTO_EX.newTimePair(0.0F, 0.056F).addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false));
        this.comboAnimation.put(GuHaoAnimations.GUHAO_UCHIGATANA_SHEATHING_AUTO_EX, (AttackAnimation) GuHaoAnimations.GUHAO_UCHIGATANA_SHEATHING_AUTO_EX.newTimePair(0.0F, 0.056F).addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false));

        this.comboAnimation.put(WOMAnimations.KATANA_AUTO_1, (AttackAnimation) Animations.RUSHING_TEMPO2.newTimePair(0.0F, 0.25F).addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false));
        this.comboAnimation.put(WOMAnimations.KATANA_AUTO_2, (AttackAnimation) Animations.RUSHING_TEMPO3.newTimePair(0.0F, 0.25F).addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false));
        this.comboAnimation.put(WOMAnimations.KATANA_AUTO_3, (AttackAnimation) GuHaoAnimations.EF_UCHIGATANA_SHEATHING_DASH.newTimePair(0.0F, 0.25F).addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false));
        this.comboAnimation.put(CorruptAnimations.SWORD_ONEHAND_AUTO3, (AttackAnimation) Animations.RUSHING_TEMPO2.newTimePair(0.0F, 0.25F).addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false));
        this.comboAnimation.put(GuHaoAnimations.HERRSCHER_AUTO_3, (AttackAnimation) Animations.RUSHING_TEMPO1.newTimePair(0.0F, 0.25F).addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false));

        this.comboAnimation.put(GuHaoAnimations.GUHAO_DASH_2, (AttackAnimation) GuHaoAnimations.DENG_LONG.newTimePair(0.0F, 2.833F).addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false));
        this.comboAnimation.put(GuHaoAnimations.SETTLEMENT, (AttackAnimation) GuHaoAnimations.DENG_LONG.newTimePair(0.0F, 2.833F).addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false));

        this.comboAnimation.put(Animations.RUSHING_TEMPO2, (AttackAnimation) GuHaoAnimations.GUHAO_BIU.newTimePair(0.0F, 0.385F).addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false));
        this.comboAnimation.put(GuHaoAnimations.EF_UCHIGATANA_SHEATHING_DASH, (AttackAnimation) GuHaoAnimations.GUHAO_BIU.newTimePair(0.0F, 0.385F).addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false));
        this.comboAnimation.put(Animations.RUSHING_TEMPO3, (AttackAnimation) GuHaoAnimations.GUHAO_BIU.newTimePair(0.0F, 0.385F).addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false));
        this.comboAnimation.put(Animations.RUSHING_TEMPO1, (AttackAnimation) GuHaoAnimations.GUHAO_BIU.newTimePair(0.0F, 0.385F).addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false));

        this.comboAnimation.put(GuHaoAnimations.GUHAO_BIU, (AttackAnimation) GuHaoAnimations.GUHAO_UCHIGATANA_SCRAP.newTimePair(0.0F, 0.155F).addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false));
        this.comboAnimation.values().forEach((animation) -> animation.phases[0].addProperties((this.properties.get(0)).entrySet()));
        return this;
    }
    @Override
    @OnlyIn(Dist.CLIENT)
    public FriendlyByteBuf gatherArguments(LocalPlayerPatch executer, ControllEngine controllEngine) {
        Input input = executer.getOriginal().input;
        float pulse = Mth.clamp(0.3F + EnchantmentHelper.getSneakingSpeedBonus(executer.getOriginal()), 0.0F, 1.0F);
        input.tick(false, pulse);
        int forward = input.up ? 1 : 0;
        int backward = input.down ? -1 : 0;
        int left = input.left ? 1 : 0;
        int right = input.right ? -1 : 0;
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(forward);
        buf.writeInt(backward);
        buf.writeInt(left);
        buf.writeInt(right);
        return buf;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Object getExecutionPacket(LocalPlayerPatch executer, FriendlyByteBuf args) {
        int forward = args.readInt();
        int backward = args.readInt();
        int left = args.readInt();
        int right = args.readInt();
        int vertic = forward + backward;
        int horizon = left + right;
        int animation;
        if (vertic == 0) {
            if (horizon == 0) {
                animation = 1;
            } else {
                animation = 2;
            }
        } else {
            animation = vertic >= 0 ? 0 : 1;
        }

        CPExecuteSkill packet = new CPExecuteSkill(executer.getSkill(this).getSlotId());
        packet.getBuffer().writeInt(animation);
        return packet;
    }

    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        if(container.getExecuter().isLogicalClient()) {
            container.getDataManager().setDataSync(IS_CTRL_DOWN.get(), Key.CTRL.isDown(), ((LocalPlayer) container.getExecuter().getOriginal()));
        }
    }

    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        boolean isSheathed = executer.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().getDataValue(SHEATH.get());
        boolean isStop = executer.getOriginal().isSprinting();
        boolean isOnGround = executer.getOriginal().onGround();
        DynamicAnimation animation = executer.getAnimator().getPlayerFor(null).getAnimation();
        while (true) {
//            executer.playSound(Sounds.CHUA.get(),0.75f,1.0f,1.0f);
            if (executer.getTarget() != null && ((executer.getSkill(SkillSlots.WEAPON_INNATE).getStack() >= 12 && ((executer.getTarget().getHealth() <= executer.getTarget().getMaxHealth() * 0.1f) || (executer.getTarget().getHealth() <= 10.0f))) && !isOnGround)) {
                executer.playSound(Sounds.SEKIRO.get(), 1f, 1f, 1f);
                executer.playAnimationSynchronized(WOMAnimations.AGONY_CLAWSTRIKE.addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, (a,b,c,d,e) -> 0.8F)
                        .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, AnimationEvent.create((entitypatch, animation2, params) -> {
                    LivingEntity target = entitypatch.getTarget();
                            GuhaoMod.NETWORK_CHANNEL.send(
                                    PacketDistributor.TRACKING_ENTITY.with(() -> target),
                                    new ParticlePacket(
                                            ParticleType.TWO_EYE.get(),
                                            target.getX(), target.getEyeY(), target.getZ(),
                                            target.getX(), target.getEyeY(), target.getZ()
                                    )
                            );
                    Vec3 viewVec = executer.getOriginal().getViewVector(1.0F);
                    target.teleportTo(executer.getOriginal().getX() + viewVec.x() * 1.55, executer.getOriginal().getY(), executer.getOriginal().getZ() + viewVec.z() * 1.55);
                    if (target.hasEffect(EpicFightMobEffects.STUN_IMMUNITY.get())) target.removeEffect(EpicFightMobEffects.STUN_IMMUNITY.get());
                    if (target.hasEffect(com.guhao.stars.regirster.Effect.REALLY_STUN_IMMUNITY.get())) target.removeEffect(com.guhao.stars.regirster.Effect.REALLY_STUN_IMMUNITY.get());
                    if (!target.isAlive()) return;
                    entitypatch.playSound(com.guhao.init.Sounds.CHARGE.get(),1.2f,1.1f,1.1f);
                            GuhaoMod.queueServerWork(40, () -> {
                            if (!target.isAlive()) return;
                            Random random = new Random();
                                    double distance = 500;
                                    // 生成指定数量的粒子
                                    for (int i = 0; i < 4; i++) {
                                        // 生成随机的偏移量
                                        for (int j = 0; j < 3; j++) {
                                            double startX = target.getX();
                                            double startY = target.getY();
                                            double startZ = target.getZ();
                                            double offsetX = (random.nextDouble()) * distance;
                                            double offsetY = (random.nextDouble()) * distance;
                                            double offsetZ = (random.nextDouble()) * distance;

                                            // 计算粒子的起点和终点
                                            double startOffsetX = startX + offsetX;
                                            double startOffsetY = startY + offsetY;
                                            double startOffsetZ = startZ + offsetZ;

                                            double endOffsetX = startX - offsetX;
                                            double endOffsetY = startY - offsetY;
                                            double endOffsetZ = startZ - offsetZ;
                                            GuhaoMod.NETWORK_CHANNEL.send(
                                                    PacketDistributor.TRACKING_ENTITY.with(() -> target),
                                                    new ParticlePacket(
                                                            ParticleType.GUHAO_LASER.get(),
                                                            startOffsetX, startOffsetY, startOffsetZ,
                                                            endOffsetX, endOffsetY, endOffsetZ
                                                    )
                                            );
                                            GuhaoMod.NETWORK_CHANNEL.send(
                                                    PacketDistributor.TRACKING_ENTITY.with(() -> target),
                                                    new ParticlePacket(
                                                            ParticleType.ONE_JC_BLOOD_JUDGEMENT_LONG.get(),
                                                            startX, startY + 20, startZ,
                                                            startX, startY - 20, startZ
                                                    )
                                            );
                                        }
                                    }
                                    target.hurt(new DamageSource(target.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.FELL_OUT_OF_WORLD), entitypatch.getOriginal()), target.getHealth()*10.0f);
                            ArrayUtils.playSound(target,EpicFightSounds.LASER_BLAST.get(), 1.0f,1.0f,1.0f);
                            ArrayUtils.playSound(target, com.guhao.init.Sounds.BIU.get(), 1.0f,1.0f,1.0f);

                        });
                }, AnimationEvent.Side.BOTH)), 0.0F);
                executer.getSkill(SkillSlots.WEAPON_INNATE).setStack(0);
                super.executeOnServer(executer, args);
                return;
            }
            if (executer.getOriginal().isShiftKeyDown() && (executer.getSkill(SkillSlots.WEAPON_INNATE).getStack() >= 10)) {
                if (executer.getOriginal().hasEffect(Effect.GUHAO.get())) {
                    if (isSheathed) {
                        executer.playAnimationSynchronized(GuHaoAnimations.BLOOD_JUDGEMENT, -0.3F);
                        executer.getSkill(SkillSlots.WEAPON_INNATE).setStack(4);
                        executer.setStamina(executer.getStamina() * 0.66F);
                        super.executeOnServer(executer, args);
                    } else {
                        executer.playAnimationSynchronized(GuHaoAnimations.BLOOD_JUDGEMENT, 0.0F);
                        executer.getSkill(SkillSlots.WEAPON_INNATE).setStack(3);
                        executer.setStamina(executer.getStamina() * 0.5F);
                        super.executeOnServer(executer, args);
                    }
                } else {
                    executer.playAnimationSynchronized(GuHaoAnimations.SACRIFICE, 0.0F);
                    executer.getSkill(SkillSlots.WEAPON_INNATE).setStack(0);
                    executer.setStamina(0.0F);
                    super.executeOnServer(executer, args);
                }
                return;
            }
            if (this.comboAnimation.containsKey(animation)) {
                executer.playAnimationSynchronized(this.comboAnimation.get(animation), 0.0F);
                super.executeOnServer(executer, args);
                return;
            }
///////////////////////////////////////////////////////////////////////////
            if (executer.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(IS_CTRL_DOWN.get())) {
                int i = args.readInt();
                executer.playAnimationSynchronized(this.animations[i].get(), 0.0F);
                super.executeOnServer(executer, args);
                return;
            }
///////////////////////////////////////////////////////////////////////////
            if (isOnGround && !isStop && isSheathed) {
                executer.playAnimationSynchronized(GuHaoAnimations.SETTLEMENT, 0.0F);
                super.executeOnServer(executer, args);
                return;
            } else {
                    if (isSheathed) {
                        executer.playAnimationSynchronized(GuHaoAnimations.GUHAO_BATTOJUTSU_DASH, -0.694F);
                        super.executeOnServer(executer, args);
                        return;
                    } else {
                        executer.playAnimationSynchronized(GuHaoAnimations.GUHAO_BATTOJUTSU_DASH, 0.0F);
                        super.executeOnServer(executer, args);
                        return;
                    }
            }
        }
    }
}