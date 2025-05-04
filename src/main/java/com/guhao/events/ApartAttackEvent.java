package com.guhao.events;

import com.guhao.entity.ApartEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

import java.util.Objects;

import static com.github.alexthe666.alexsmobs.effect.AMEffectRegistry.EXSANGUINATION;

@Mod.EventBusSubscriber
public class ApartAttackEvent {
    @SubscribeEvent
    public static void AttackEvent(LivingHurtEvent event) {
        if (event != null && event.getEntity() != null) {
            if (event.getEntity() instanceof Player) {
                event.setCanceled(true);
            }
            execute(event, event.getEntity(), event.getSource().getEntity());
        }
    }

    public static void execute(LivingEntity entity, Entity sourceentity) {
        execute(null, entity, sourceentity);
    }

    private static void execute(@Nullable Event event, LivingEntity livingEntity, Entity sourceentity) {
        if (livingEntity == null || sourceentity == null)
            return;
        if (sourceentity instanceof ApartEntity) {
            if (livingEntity instanceof Player) {
                if (event != null) {
                    event.setCanceled(true);
                }
            }
            if (!livingEntity.hasEffect(new MobEffectInstance(EXSANGUINATION.get()).getEffect())) livingEntity.addEffect(new MobEffectInstance(new MobEffectInstance(EXSANGUINATION.get()).getEffect(), 200, 1, false, true));
            else livingEntity.addEffect(new MobEffectInstance(new MobEffectInstance(EXSANGUINATION.get()).getEffect(), 200, Objects.requireNonNull(livingEntity.getEffect(EXSANGUINATION.get())).getAmplifier() + 1, false, true));
        }
    }
}