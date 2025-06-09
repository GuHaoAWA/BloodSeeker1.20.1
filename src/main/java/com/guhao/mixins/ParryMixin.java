package com.guhao.mixins;

import com.guhao.init.Items;
import com.guhao.stars.entity.StarAttributes;
import com.guhao.stars.regirster.Effect;
import com.nameless.indestructible.world.capability.AdvancedCustomHumanoidMobPatch;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKeys;
import yesman.epicfight.skill.guard.GuardSkill;
import yesman.epicfight.skill.guard.ParryingSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.eventlistener.HurtEvent;

@Mixin(value = ParryingSkill.class,remap = false,priority = 99999)
public class ParryMixin extends GuardSkill {
    public ParryMixin(Builder builder) {
        super(builder);
    }
    @Inject(
            method = "guard",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true)
    public void guhao$guard(SkillContainer container, CapabilityItem itemCapability, HurtEvent.Pre event, float knockback, float impact, boolean advanced, CallbackInfo ci) {
        if (container.getExecuter().getOriginal().getMainHandItem().is(Items.GUHAO.get())) {
            ci.cancel();

            DamageSource damageSource = event.getDamageSource();
            if (this.isBlockableSource(damageSource, true)) {
                ServerPlayer playerentity = event.getPlayerPatch().getOriginal();
                event.getPlayerPatch().playSound(EpicFightSounds.CLASH.get(), -0.05F, 0.1F);
                EpicFightParticles.HIT_BLUNT.get().spawnParticleWithArgument((ServerLevel)playerentity.level(), HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, playerentity, damageSource.getDirectEntity());
                event.setParried(true);
                AdvancedCustomHumanoidMobPatch<?> longpatch = EpicFightCapabilities.getEntityPatch(event.getDamageSource().getEntity(), AdvancedCustomHumanoidMobPatch.class);
                if (longpatch != null) {
                    longpatch.setStamina((float) (longpatch.getStamina() - longpatch.getOriginal().getAttribute(StarAttributes.PARRY_STAMINA_LOSE.get()).getValue()));
                    if (longpatch.getOriginal().hasEffect(Effect.STA.get())) {
                        longpatch.setStamina(longpatch.getStamina() - longpatch.getOriginal().getEffect(Effect.STA.get()).getAmplifier() +1);
                    }
                }
                knockback *= 0.4F;
                container.getDataManager().setData(SkillDataKeys.LAST_ACTIVE.get(), 0);

                Entity var12 = damageSource.getDirectEntity();
                if (var12 instanceof LivingEntity livingentity) {
                    knockback += (float) EnchantmentHelper.getKnockbackBonus(livingentity) * 0.1F;
                }

                event.getPlayerPatch().knockBackEntity(damageSource.getDirectEntity().position(), knockback);

                GuardSkill.BlockType blockType = BlockType.ADVANCED_GUARD;
                StaticAnimation animation = this.getGuardMotion(event.getPlayerPatch(), itemCapability, blockType);
                if (animation != null) {
                    event.getPlayerPatch().playAnimationSynchronized(animation, 0.0F);
                }
                this.dealEvent(event.getPlayerPatch(), event, advanced);
            }
        }
    }
}