package com.guhao.events;

import com.dfdyz.epicacg.registry.Sounds;
import com.guhao.entity.ApartEntity;
import com.guhao.init.Items;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.List;

public class RedFistEvent {

    public static void execute(LevelAccessor world, double x, double y, double z, Player owner) {
        List<ApartEntity> apartEntities = world.getEntitiesOfClass(ApartEntity.class, new AABB(x - 100, y - 100, z - 100, x + 100, y + 100, z + 100));
        if (!apartEntities.isEmpty() || !(owner instanceof ServerPlayer)) {
            return;
        }
        if (EpicFightCapabilities.getEntityPatch(owner, PlayerPatch.class).getTarget() != null && owner.getMainHandItem().getItem() == Items.GUHAO.get() && EpicFightCapabilities.getEntityPatch(owner, PlayerPatch.class).getEntityState().getLevel() == 2) {
            Vec3 viewVec = EpicFightCapabilities.getEntityPatch(owner, PlayerPatch.class).getTarget().getViewVector(1.0F);
            ApartEntity apartEntity = new ApartEntity((ServerPlayer) owner);
            apartEntity.setPos(new Vec3(EpicFightCapabilities.getEntityPatch(owner, PlayerPatch.class).getTarget().getX() - viewVec.x() * 0.95, EpicFightCapabilities.getEntityPatch(owner, PlayerPatch.class).getTarget().getEyeY() - 1.2, EpicFightCapabilities.getEntityPatch(owner, PlayerPatch.class).getTarget().getZ() - viewVec.z() * 0.95));
            world.addFreshEntity(apartEntity);
            EpicFightCapabilities.getEntityPatch(owner, PlayerPatch.class).playSound(Sounds.DualSword_SA1_1.get(),1.2f,1f,1f);
        }
    }
}
