package com.guhao.network;

import com.guhao.client.ClientHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


public record GuHaoEffectPacket(Vec3 pos, int radius) {

    public static void encode(GuHaoEffectPacket msg, FriendlyByteBuf buffer) {
        buffer.writeDouble(msg.pos.x);
        buffer.writeDouble(msg.pos.y);
        buffer.writeDouble(msg.pos.z);
        buffer.writeInt(msg.radius);
    }

    public static GuHaoEffectPacket decode(FriendlyByteBuf buffer) {
        return new GuHaoEffectPacket(
                new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()),
                buffer.readInt()
        );
    }

    public static void handle(GuHaoEffectPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // 在客户端执行特效
            ClientHandler.handleGuHaoEffect(msg);
        });
        ctx.get().setPacketHandled(true);
    }
}