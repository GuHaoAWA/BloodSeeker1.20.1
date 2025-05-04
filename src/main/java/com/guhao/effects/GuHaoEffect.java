package com.guhao.effects;

import com.guhao.events.GuHaoEffectTick;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class GuHaoEffect extends MobEffect {
    public GuHaoEffect() {
        super(MobEffectCategory.BENEFICIAL, -2620636);
    }

    @Override
    public @NotNull String getDescriptionId() {
        return "effect.guhao.guhao";
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
            GuHaoEffectTick.execute(entity);
    }
}
