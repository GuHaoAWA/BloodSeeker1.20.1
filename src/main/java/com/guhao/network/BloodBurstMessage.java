package com.guhao.network;


import com.guhao.GuhaoMod;
import com.guhao.events.BloodBurstEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BloodBurstMessage {
    int type, pressedms;

    public BloodBurstMessage(int type, int pressedms) {
        this.type = type;
        this.pressedms = pressedms;
    }

    public BloodBurstMessage(FriendlyByteBuf buffer) {
        this.type = buffer.readInt();
        this.pressedms = buffer.readInt();
    }

    public static void buffer(BloodBurstMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.type);
        buffer.writeInt(message.pressedms);
    }

    public static void handler(BloodBurstMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            pressAction(context.getSender(), message.type, message.pressedms);
        });
        context.setPacketHandled(true);
    }

    public static void pressAction(Player entity, int type, int pressedms) {
        Level world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        // security measure to prevent arbitrary chunk generation
        if (!world.hasChunkAt(entity.blockPosition()))
            return;
        if (type == 0) {

            BloodBurstEvent.execute(entity);
        }
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        GuhaoMod.addNetworkMessage(BloodBurstMessage.class, BloodBurstMessage::buffer, BloodBurstMessage::new, BloodBurstMessage::handler);
    }
}
