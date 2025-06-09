package com.guhao.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ParticlePacket {
    private final ParticleType<?> particleType;
    private final double x1, y1, z1, x2, y2, z2;

    public ParticlePacket(ParticleType<?> type, double x1, double y1, double z1, double x2, double y2, double z2) {
        this.particleType = type;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }

    // 编解码方法
    public static void encode(ParticlePacket packet, FriendlyByteBuf buffer) {
        buffer.writeRegistryId(ForgeRegistries.PARTICLE_TYPES, packet.particleType);
        buffer.writeDouble(packet.x1);
        buffer.writeDouble(packet.y1);
        buffer.writeDouble(packet.z1);
        buffer.writeDouble(packet.x2);
        buffer.writeDouble(packet.y2);
        buffer.writeDouble(packet.z2);
    }

    public static ParticlePacket decode(FriendlyByteBuf buffer) {
        ParticleType<?> type = buffer.readRegistryIdSafe(ParticleType.class);
        return new ParticlePacket(
                type,
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble()
        );
    }

    // 处理数据包的方法（客户端侧）
    public static void handle(ParticlePacket packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                level.addParticle((ParticleOptions) packet.particleType,
                        packet.x1, packet.y1, packet.z1,
                        packet.x2, packet.y2, packet.z2);
            }
        });
        context.setPacketHandled(true);
    }
}