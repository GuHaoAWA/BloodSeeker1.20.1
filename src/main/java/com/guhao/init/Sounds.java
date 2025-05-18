package com.guhao.init;

import com.guhao.GuhaoMod;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.io.IOException;

@Mod.EventBusSubscriber(
        modid = GuhaoMod.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class Sounds {

    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, GuhaoMod.MODID);
    public static final RegistryObject<SoundEvent> LAUGH = registerSoundEvent("laugh");
    public static final RegistryObject<SoundEvent> DAO1 = registerSoundEvent("dao1");
    public static final RegistryObject<SoundEvent> DAO2 = registerSoundEvent("dao2");
    public static final RegistryObject<SoundEvent> DAO3 = registerSoundEvent("dao3");
    public static final RegistryObject<SoundEvent> BIU = registerSoundEvent("biu");
    public static final RegistryObject<SoundEvent> BLOOD = registerSoundEvent("blood");
    public static final RegistryObject<SoundEvent> CHARGE = registerSoundEvent("charge");

    public Sounds() {
    }
    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return REGISTRY.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(GuhaoMod.MODID, name)));
    }

}

