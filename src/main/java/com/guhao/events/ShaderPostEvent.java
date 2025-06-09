package com.guhao.events;

import com.guhao.GuhaoMod;
import com.guhao.client.ParticlePostProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid= GuhaoMod.MODID, value=Dist.CLIENT, bus= Mod.EventBusSubscriber.Bus.MOD)
public class ShaderPostEvent {
    @SubscribeEvent
    public void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            ParticlePostProcessor.applyPostEffect(
                    new GuiGraphics(Minecraft.getInstance(), Minecraft.getInstance().renderBuffers().bufferSource()),
                    event.getPartialTick()
            );
        }
    }
}
