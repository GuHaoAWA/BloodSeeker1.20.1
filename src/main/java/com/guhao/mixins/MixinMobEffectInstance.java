package com.guhao.mixins;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Optional;

@Mixin(MobEffectInstance.class)
public abstract class MixinMobEffectInstance {
    /**
     * 修改 NBT 存储：用 putInt 替代 putByte
     */
    @Redirect(
            method = "writeDetailsTo",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/nbt/CompoundTag;putByte(Ljava/lang/String;B)V"
            )
    )
    private void redirectPutByte(CompoundTag tag, String key, byte value) {
        tag.putInt(key, ((MobEffectInstance)(Object)this).amplifier);
    }

    /**
     * 修改 NBT 加载：用 getInt 替代 getByte
     */
    @Redirect(
            method = "loadSpecifiedEffect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/nbt/CompoundTag;getByte(Ljava/lang/String;)B"
            )
    )
    private static byte redirectGetByte(CompoundTag tag, String key) {
        return (byte) tag.getInt(key); // 安全转换（确保兼容性）
    }

    @Inject(
            method = "<init>(Lnet/minecraft/world/effect/MobEffect;IIZZZLnet/minecraft/world/effect/MobEffectInstance;Ljava/util/Optional;)V",
            at = @At("TAIL")
    )
    private void onConstruct(MobEffect effect, int duration, int amplifier, boolean ambient,
                             boolean visible, boolean showIcon,
                             @Nullable MobEffectInstance hiddenEffect,
                             Optional<MobEffectInstance.FactorData> factorData,
                             CallbackInfo ci) {
        ((MobEffectInstance)(Object)this).amplifier = amplifier;
    }
}