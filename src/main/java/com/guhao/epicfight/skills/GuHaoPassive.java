package com.guhao.epicfight.skills;

import com.guhao.epicfight.GuHaoAnimations;
import com.guhao.epicfight.GuHaoSkillDataKeys;
import com.guhao.init.Effect;
import com.guhao.init.Items;
import com.guhao.init.Key;
import com.guhao.init.ParticleType;
import io.redspace.ironsspellbooks.entity.spells.blood_needle.BloodNeedle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.types.DodgeAnimation;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPPlayAnimation;
import yesman.epicfight.skill.*;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.Random;
import java.util.UUID;

import static com.guhao.epicfight.GuHaoSkillDataKeys.IS_RIGHT_DOWN;
import static com.guhao.epicfight.GuHaoSkillDataKeys.SHEATH;

public class GuHaoPassive extends PassiveSkill {

    private static final UUID EVENT_UUID;

    public GuHaoPassive(Builder<? extends Skill> builder) {
        super(builder);
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
            if (!(event.getAnimation() instanceof DodgeAnimation)) {
                container.getSkill().setConsumptionSynchronize(event.getPlayerPatch(), 0.0F);
                container.getSkill().setStackSynchronize(event.getPlayerPatch(), 0);
            }
        });
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID, (event) -> {
            if (event.getDamageSource().is(DamageTypes.FALL)) {
                event.setResult(AttackResult.ResultType.MISSED);
                event.setCanceled(true);
            }
        });
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID, (event) -> this.onReset(container));
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.BASIC_ATTACK_EVENT, EVENT_UUID, (event) -> {
            if (container.getExecuter().getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().getDataValue(IS_RIGHT_DOWN.get())) {
                container.getExecuter().getOriginal().stopUsingItem();
                event.setCanceled(true);
                container.getExecuter().playAnimationSynchronized(GuHaoAnimations.JIANQIE,0.0F);
            }
        });
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.DODGE_SUCCESS_EVENT, EVENT_UUID, (event) -> {
            if (event.getDamageSource().getEntity() instanceof LivingEntity livingEntity) {
                Level world = livingEntity.level();
                Random random = new Random();
                int count = 1;
                float damage = random.nextFloat(20F, 30F);
                Vec3 center = livingEntity.position().add(0, livingEntity.getEyeHeight() / 2, 0);
                float degreesPerNeedle = 360f / count;
                for (int i = 0; i < count; i++) {
                    Vec3 offset = new Vec3(0, Math.random(), .55).normalize().scale(livingEntity.getBbWidth() + 2.75f).yRot(degreesPerNeedle * i * Mth.DEG_TO_RAD);
                    Vec3 spawn = center.add(offset);
                    Vec3 motion = center.subtract(spawn).normalize();

                    BloodNeedle needle = new BloodNeedle(world, event.getPlayerPatch().getOriginal());
                    needle.moveTo(spawn);
                    needle.shoot(motion.scale(.45f));
                    needle.setDamage(damage);
                    needle.setScale(1.25f);
                    world.addFreshEntity(needle);
                }
            }
        });
    }

    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        if (container.getExecuter().getOriginal().getMainHandItem().getItem() == Items.GUHAO.get() && container.getExecuter().getOriginal().hasEffect(Effect.GUHAO.get())) {
            container.getExecuter().getOriginal().level().addParticle(ParticleType.RING.get(), container.getExecuter().getOriginal().getX(), container.getExecuter().getOriginal().getY() + 0.05, container.getExecuter().getOriginal().getZ(), 0, 0, 0);
        }
        if(container.getExecuter().isLogicalClient()) {
            container.getDataManager().setDataSync(IS_RIGHT_DOWN.get(), Key.RIGHT.isDown(), ((LocalPlayer)container.getExecuter().getOriginal()));
        }
    }
    @Override
    public void onRemoved(SkillContainer container) {
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.ACTION_EVENT_SERVER, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.BASIC_ATTACK_EVENT, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.DODGE_SUCCESS_EVENT, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID);
    }
    @Override
    public void onReset(SkillContainer container) {
        PlayerPatch<?> executer = container.getExecuter();
        if (!executer.isLogicalClient() && container.getDataManager().getDataValue(SHEATH.get())) {
            ServerPlayerPatch playerpatch = (ServerPlayerPatch)executer;
            container.getDataManager().setDataSync(SHEATH.get(), false, playerpatch.getOriginal());
            playerpatch.modifyLivingMotionByCurrentItem();
            container.getSkill().setConsumptionSynchronize(playerpatch, 0.0F);
        }

    }
    @Override
    public void setConsumption(SkillContainer container, float value) {
        PlayerPatch<?> executer = container.getExecuter();
        if (!executer.isLogicalClient() && container.getMaxResource() < value) {
            ServerPlayer serverPlayer = (ServerPlayer)executer.getOriginal();
            container.getDataManager().setDataSync(SHEATH.get(), true, serverPlayer);
            ((ServerPlayerPatch)container.getExecuter()).modifyLivingMotionByCurrentItem(false);
            SPPlayAnimation msg3 = new SPPlayAnimation(Animations.BIPED_UCHIGATANA_SCRAP, serverPlayer.getId(), 0.0F);
            EpicFightNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(msg3, serverPlayer);
        }

        super.setConsumption(container, value);
    }
    @Override
    public boolean shouldDeactivateAutomatically(PlayerPatch<?> executer) {
        return true;
    }
    @Override
    public float getCooldownRegenPerSecond(PlayerPatch<?> player) {
        if (player.getSkill(this).getDataManager().getDataValue(GuHaoSkillDataKeys.SHEATH.get())) {
            return 0.0F;
        }
        return player.getOriginal().isUsingItem() ? 0.0F : 1.0F;
    }

    static {
        EVENT_UUID = UUID.fromString("a416c93a-42cb-11eb-b378-0242ac130002");
    }
}
