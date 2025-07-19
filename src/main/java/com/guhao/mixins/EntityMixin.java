package com.guhao.mixins;

import com.guhao.init.Items;
import com.guhao.stars.effects.Red_Glow;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Shadow
    public Level level;

    @Inject(at = @At("HEAD"), method = "getTeamColor", cancellable = true)
    public void guhao$getTeamColor$renderShiny(CallbackInfoReturnable<Integer> cir) {
        Entity self = (Entity) (Object) this;
        if (self instanceof ItemEntity itemEntity && itemEntity.getItem().getItem() == Items.GUHAO.get()) {
            cir.setReturnValue(-65536);
        }
    }

}