package com.guhao.item.model;

import com.guhao.init.Effect;
import com.guhao.item.GUHAO;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GUHAO2Model extends GeoModel<GUHAO> {
    @Override
    public ResourceLocation getAnimationResource(GUHAO animatable) {
        return new ResourceLocation("guhao", "animations/bloodslashingblade2_ex.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(GUHAO animatable) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.hasEffect(Effect.GUHAO.get())) return new ResourceLocation("guhao", "geo/bloodslashingblade2_ex.geo.json");
        else return new ResourceLocation("guhao", "geo/bloodslashingblade2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GUHAO animatable) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.hasEffect(Effect.GUHAO.get())) return new ResourceLocation("guhao", "textures/items/bloodslashingblade2_ex.png");
        else return new ResourceLocation("guhao", "textures/items/bloodslashingblade2.png");
    }
}
