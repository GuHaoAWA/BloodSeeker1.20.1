package com.guhao.init;

import com.guhao.renderers.RenderGuhao2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent;

@EventBusSubscriber(
        modid = "guhao",
        bus = Bus.MOD,
        value = {Dist.CLIENT}
)
public class GuHaoRenderEngine {
    public GuHaoRenderEngine(){}
    @SubscribeEvent
    public static void registerRenderer(PatchedRenderersEvent.Add event) {
        event.addItemRenderer(Items.GUHAO.get(), new RenderGuhao2());
    }

}
