package com.guhao.events;

import com.guhao.GuhaoMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid= GuhaoMod.MODID, value=Dist.CLIENT, bus= Mod.EventBusSubscriber.Bus.MOD)
public class ClientModBusEvent {
    @SubscribeEvent
    public static void addLayersEvent(EntityRenderersEvent.AddLayers event) {

    }
}
