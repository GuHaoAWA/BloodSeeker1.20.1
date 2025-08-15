package com.guhao.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public record SkyEyeStatePacket(boolean isOpen) {
    // 编码（服务端 -> 客户端）
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(isOpen);
    }

    // 解码（客户端接收）
    public static SkyEyeStatePacket decode(FriendlyByteBuf buf) {
        return new SkyEyeStatePacket(buf.readBoolean());
    }

    // 客户端处理逻辑
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            com.guhao.client.sky.SkyEyeRenderer.isOpen = isOpen;

        });
        ctx.get().setPacketHandled(true);
    }
}