package com.guhao.utils;

import net.minecraft.world.damagesource.DamageSource;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

public class CastUtils {
    public EpicFightDamageSource getEpicFightDamageSources(DamageSource damageSource) {
        if (damageSource instanceof EpicFightDamageSource epicfightDamageSource) {
            return epicfightDamageSource;
        } else {
            return null;
        }
    }
}