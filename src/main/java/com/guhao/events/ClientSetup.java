package com.guhao.events;

import com.guhao.GuhaoMod;
import com.guhao.client.ParticlePostProcessor;
import com.guhao.client.sky.SkyEyeRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.io.IOException;

import static com.guhao.GuhaoMod.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) {
        try {
            ParticlePostProcessor.registerShaders(event.getResourceProvider());
        } catch (IOException e) {
            GuhaoMod.LOGGER.error("Failed to load shaders", e);
        }
    }
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // 注册事件监听器
        event.enqueueWork(() -> {
            MinecraftForge.EVENT_BUS.register(new SkyEyeRenderer());
        });
    }
}