package com.guhao.client;

import com.dfdyz.epicacg.client.screeneffect.HsvFilterEffect;
import com.dfdyz.epicacg.event.ScreenEffectEngine;
import com.guhao.network.GuHaoEffectPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientHandler {
    public static void handleGuHaoEffect(GuHaoEffectPacket packet) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.player != null) {
            HsvFilterEffect effect = new HsvFilterEffect(packet.pos(), packet.radius());
            ScreenEffectEngine.PushScreenEffect(effect);
        }
    }
}