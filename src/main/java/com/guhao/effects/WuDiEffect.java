package com.guhao.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import org.jetbrains.annotations.NotNull;

public class WuDiEffect extends MobEffect {
    public WuDiEffect() {
        super(MobEffectCategory.BENEFICIAL, -2620636);
    }

    @Override
    public @NotNull String getDescriptionId() {
        return "effect.guhao.wudi";
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

}
