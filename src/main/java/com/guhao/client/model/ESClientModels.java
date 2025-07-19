package com.guhao.client.model;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.client.model.RawMesh;
import yesman.epicfight.api.forgeevent.ModelBuildEvent;

@Mod.EventBusSubscriber(
        modid = "guhao",
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = {Dist.CLIENT}
)
public class ESClientModels {
    public static RawMesh CONQUEROR_HAKI;
    public static RawMesh CONQUEROR_HAKI_FOOR;

    public ESClientModels() {
    }

    @SubscribeEvent
    public static void registerMeshes(ModelBuildEvent.MeshBuild event) {
        CONQUEROR_HAKI = event.getRaw("guhao", "particle/conqueror_haki", RawMesh::new);
        CONQUEROR_HAKI_FOOR = event.getRaw("guhao", "particle/conqueror_haki_particle", RawMesh::new);
    }
}
