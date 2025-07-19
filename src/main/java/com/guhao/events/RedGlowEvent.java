package com.guhao.events;

import com.guhao.init.Items;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class RedGlowEvent {
    @SubscribeEvent
    public static void onItemTick(EntityJoinLevelEvent event) {
        execute(event, event.getEntity());
    }

    public static void execute(Entity entity) {
        execute(null, entity);
    }

    private static void execute(@Nullable Event event, Entity entity) {
        if (entity instanceof ItemEntity itemEntity && itemEntity.getItem().getItem() == Items.GUHAO.get()) {
            itemEntity.setGlowingTag(true);
        }
    }
}
