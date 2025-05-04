package com.guhao.init;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.guhao.GuhaoMod.MODID;

public class Items {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> GUHAO = REGISTRY.register("guhao", com.guhao.item.GUHAO::new);
    public static final RegistryObject<Item> SHEATH = REGISTRY.register("sheath", com.guhao.item.SheathItem::new);
}
