package com.guhao.mixins;

import com.guhao.init.Effect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MilkBucketItem.class)
public class MilkMixin extends Item {
    public MilkMixin(Properties p_41383_) {
        super(p_41383_);
    }

    @Inject(at = @At("HEAD"),method = "finishUsingItem")
    public void finishUsingItem(ItemStack p_42923_, Level p_42924_, LivingEntity p_42925_, CallbackInfoReturnable<ItemStack> cir) {
        // 混合牛奶效果
        if (!p_42924_.isClientSide) {
            int i = 9999;
            int j = 9999;
            boolean p = p_42925_.hasEffect(Effect.GUHAO.get());
            if (p) {
                i = p_42925_.getEffect(Effect.GUHAO.get()).getAmplifier();
                j = p_42925_.getEffect(Effect.GUHAO.get()).getDuration();
            }
            p_42925_.curePotionEffects(p_42923_);
            if (p) {
                p_42925_.addEffect(new net.minecraft.world.effect.MobEffectInstance(Effect.GUHAO.get(), j, i, false, true));
            }
        }
    }
}
