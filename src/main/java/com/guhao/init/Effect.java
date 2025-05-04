package com.guhao.init;

import com.guhao.effects.GuHaoEffect;
import com.guhao.effects.WuDiEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import static com.guhao.GuhaoMod.MODID;

public class Effect {
    public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);
    public static final RegistryObject<MobEffect> GUHAO = REGISTRY.register("guhao", GuHaoEffect::new);
    public static final RegistryObject<MobEffect> WUDI = REGISTRY.register("wudi", WuDiEffect::new);
}
