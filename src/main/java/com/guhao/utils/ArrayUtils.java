package com.guhao.utils;

import com.guhao.epicfight.GuHaoAnimations;
import net.corruptdog.cdm.gameasset.CorruptAnimations;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reascer.wom.gameasset.WOMAnimations;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;

import java.util.Arrays;

public record ArrayUtils() {
    static final StaticAnimation[] EYES;
    static {
        EYES = new StaticAnimation[] {
                GuHaoAnimations.GUHAO_UCHIGATANA_SHEATHING_AUTO,
                GuHaoAnimations.GUHAO_UCHIGATANA_SHEATHING_DASH,
                GuHaoAnimations.GUHAO_UCHIGATANA_SHEATH_AIR_SLASH,
                GuHaoAnimations.GUHAO_UCHIGATANA_SHEATHING_AUTO_EX,

                WOMAnimations.KATANA_AUTO_1,
                WOMAnimations.KATANA_AUTO_2,
                GuHaoAnimations.KATANA_AUTO_3,
                CorruptAnimations.SWORD_ONEHAND_AUTO3,
                GuHaoAnimations.GUHAO_DASH_2,
                GuHaoAnimations.GUHAO_DASH,
                GuHaoAnimations.HERRSCHER_AUTO_3,

                Animations.RUSHING_TEMPO3,
                Animations.RUSHING_TEMPO1,
                Animations.RUSHING_TEMPO2,
                GuHaoAnimations.EF_UCHIGATANA_SHEATHING_DASH,
                GuHaoAnimations.SETTLEMENT,

                GuHaoAnimations.GUHAO_BIU,
        };
    }
    public static boolean isEyes(StaticAnimation staticAnimation) {
        return Arrays.asList(EYES).contains(staticAnimation);
    }
    public static void playSound(LivingEntity livingEntity, SoundEvent sound, float volume, float pitchModifierMin, float pitchModifierMax) {
        if (sound != null) {
            float pitch = (livingEntity.getRandom().nextFloat() * 2.0F - 1.0F) * (pitchModifierMax - pitchModifierMin);
            if (!livingEntity.level().isClientSide()) {
                livingEntity.level().playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), sound, livingEntity.getSoundSource(), volume, 1.0F + pitch);
            } else {
                livingEntity.level().playLocalSound(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), sound, livingEntity.getSoundSource(), volume, 1.0F + pitch, false);
            }

        }
    }
}
