package com.guhao.events;

import com.dfdyz.epicacg.registry.Sounds;
import com.guhao.entity.ApartEntity;
import com.guhao.init.Items;
import com.guhao.init.ParticleType;
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
        if (!apartEntities.isEmpty()) {
            return;
        }
        if (EpicFightCapabilities.getEntityPatch(owner, PlayerPatch.class).getTarget() != null && owner.getMainHandItem().getItem() == Items.GUHAO.get() && (EpicFightCapabilities.getEntityPatch(owner, PlayerPatch.class).getEntityState().getLevel() == 2 || EpicFightCapabilities.getEntityPatch(owner, PlayerPatch.class).getEntityState().getLevel() == 3)) {
            Vec3 viewVec = EpicFightCapabilities.getEntityPatch(owner, PlayerPatch.class).getTarget().getViewVector(1.0F);
            Vec3 pos = new Vec3(EpicFightCapabilities.getEntityPatch(owner, PlayerPatch.class).getTarget().getX() - viewVec.x() * 1.25, EpicFightCapabilities.getEntityPatch(owner, PlayerPatch.class).getTarget().getEyeY() - 1.25, EpicFightCapabilities.getEntityPatch(owner, PlayerPatch.class).getTarget().getZ() - viewVec.z() * 1.25);

            if (owner instanceof ServerPlayer serverPlayer) {
                ApartEntity apartEntity = new ApartEntity(serverPlayer);
                apartEntity.setPos(pos);
                world.addFreshEntity(apartEntity);
                EpicFightCapabilities.getEntityPatch(owner, PlayerPatch.class).playSound(Sounds.DualSword_SA1_1.get(), 1.2f, 1f, 1f);
            }
            owner.level().addParticle(ParticleType.ENTITY_AFTER_IMG_BLOOD.get(), pos.x, pos.y, pos.z, Double.longBitsToDouble(owner.getId()), 0.0, 0.0);
        }

    }
}
