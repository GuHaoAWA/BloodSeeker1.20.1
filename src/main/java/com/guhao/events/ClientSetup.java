package com.guhao.events;

import com.guhao.GuhaoMod;
import com.guhao.client.ParticlePostProcessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
}