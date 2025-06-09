package com.guhao;


import com.guhao.capability.GuHaoCapability;
import com.guhao.client.sky.SkyEyeRenderer;
import com.guhao.epicfight.GuHaoAnimations;
import com.guhao.epicfight.GuHaoSkillDataKeys;
import com.guhao.init.*;
import com.guhao.network.GuHaoEffectPacket;
import com.guhao.network.ParticlePacket;
import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;


@Mod(GuhaoMod.MODID)
public class GuhaoMod {
    public static final String MODID = "guhao";
    private static final String PROTOCOL_VERSION = "1";
    public static final String NETWORK_PROTOCOL = "1.0";
    public static final SimpleChannel NETWORK_CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MODID, "main"),
            () -> NETWORK_PROTOCOL,
            NETWORK_PROTOCOL::equals,
            NETWORK_PROTOCOL::equals
    );
    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static int messageID = 0;
    public static final Logger LOGGER = LogUtils.getLogger();
    public GuhaoMod() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setupClient);
        bus.addListener(GuHaoCapability::register);
        bus.addListener(GuHaoAnimations::registerAnimations);
        Entities.REGISTRY.register(bus);
        Sounds.REGISTRY.register(bus);
        Effect.REGISTRY.register(bus);
        ParticleType.PARTICLES.register(bus);
        Items.REGISTRY.register(bus);
        GuHaoSkillDataKeys.DATA_KEYS.register(bus);

        int packetId = 0;
        NETWORK_CHANNEL.registerMessage(
                packetId++,
                ParticlePacket.class,
                ParticlePacket::encode,
                ParticlePacket::decode,
                ParticlePacket::handle
        );
        NETWORK_CHANNEL.registerMessage(
                packetId++,
                GuHaoEffectPacket.class,
                GuHaoEffectPacket::encode,
                GuHaoEffectPacket::decode,
                GuHaoEffectPacket::handle
        );

        MinecraftForge.EVENT_BUS.register(SkyEyeRenderer.class);
    }

    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
        messageID++;
    }
    @Mod.EventBusSubscriber(modid = GuhaoMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }
    private void setupClient(final FMLClientSetupEvent event){
        event.enqueueWork(() -> {
            // 确保只在物理客户端执行
            if (FMLEnvironment.dist == Dist.CLIENT) {
                GuHaoAnimations.LoadCamAnims();
                // 其他客户端初始化...
            }
        });
        try {
            Config.Load(false);
        } catch (Exception var3) {
            Exception e = var3;
            throw new RuntimeException(e);
        }
    }

    @SubscribeEvent
    public void setupCommon(FMLCommonSetupEvent event) {
//        event.enqueueWork(Net::register);
    }


    private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

    public static void queueServerWork(int tick, Runnable action) {
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
            workQueue.add(new AbstractMap.SimpleEntry<>(action, tick));
    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
            workQueue.forEach(work -> {
                work.setValue(work.getValue() - 1);
                if (work.getValue() == 0)
                    actions.add(work);
            });
            actions.forEach(e -> e.getKey().run());
            workQueue.removeAll(actions);


            for (Player player : event.getServer().getPlayerList().getPlayers()) {
                if (player.getMainHandItem().is(Items.GUHAO.get()) && player.hasEffect(Effect.GUHAO.get())) {
                    SkyEyeRenderer.isOpen = true;
                    break;
                } else {
                    SkyEyeRenderer.isOpen = false;
                }
            }

        }
    }

}