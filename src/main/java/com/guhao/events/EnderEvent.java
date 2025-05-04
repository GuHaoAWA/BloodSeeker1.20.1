package com.guhao.events;

import com.guhao.epicfight.GuHaoAnimations;
import com.guhao.init.Items;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class EnderEvent {
	public static void execute(Entity entity) {
		if (entity instanceof LivingEntity livingEntity) {
			LazyOptional<EntityPatch> optional = livingEntity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY);
			if (entity instanceof Player p) {
				optional.ifPresent(patch -> {
					if (patch instanceof LivingEntityPatch && p.getMainHandItem().getItem() == Items.GUHAO.get()) {
						if (!(((LivingEntityPatch<?>) patch).getEntityState().inaction())) ((LivingEntityPatch<?>) patch).playAnimationSynchronized(GuHaoAnimations.ENDER, 0F);
					}
				});
			}
		}
	}
}
