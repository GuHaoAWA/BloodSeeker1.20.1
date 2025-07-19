package com.guhao.events;

import com.guhao.GuhaoMod;
import com.guhao.init.Items;
import com.guhao.network.GuHaoEffectPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import reascer.wom.gameasset.WOMAnimations;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.HashMap;
import java.util.Map;

public class GuHaoEffectTick {
    // 存储每个实体的 tick 计数器
    private static final Map<LivingEntity, Integer> ENTITY_TICK_COUNTERS = new HashMap<>();
    // 发送间隔 (20 tick = 1 秒)
    private static final int SEND_INTERVAL = 30;

    public static void execute(LivingEntity entity) {
        PlayerPatch<?> pp = EpicFightCapabilities.getEntityPatch(entity, PlayerPatch.class);
        if (pp == null) return;
        // 只在服务端处理
        if (entity.level().isClientSide() || !entity.getMainHandItem().is(Items.GUHAO.get()) || pp.getAnimator().getPlayerFor(null).getAnimation() == WOMAnimations.AGONY_CLAWSTRIKE) {
            return;
        }

        // 获取或初始化该实体的 tick 计数器
        int tickCount = ENTITY_TICK_COUNTERS.getOrDefault(entity, 0);
        tickCount++;

        // 如果未达到发送间隔，更新计数器并返回
        if (tickCount < SEND_INTERVAL) {
            ENTITY_TICK_COUNTERS.put(entity, tickCount);
            return;
        }

        // 达到发送间隔，重置计数器
        ENTITY_TICK_COUNTERS.put(entity, 0);


        Vec3 pos;
        var target = pp.getTarget();

        if (target != null && target.isAlive()) {
            pos = target.position();
        } else {
            pos = pp.getOriginal().position();
        }

        // 发送数据包
        if (entity instanceof ServerPlayer serverPlayer) {
            GuhaoMod.NETWORK_CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new GuHaoEffectPacket(pos, 30)
            );
        } else {
            GuhaoMod.NETWORK_CHANNEL.send(
                    PacketDistributor.TRACKING_ENTITY.with(() -> entity),
                    new GuHaoEffectPacket(pos, 30)
            );
        }
    }
}