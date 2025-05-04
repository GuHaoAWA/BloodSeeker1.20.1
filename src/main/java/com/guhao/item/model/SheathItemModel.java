package com.guhao.item.model;

import com.guhao.item.SheathItem;
import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;


public class SheathItemModel extends GeoModel<SheathItem> {
	@Override
	public ResourceLocation getAnimationResource(SheathItem animatable) {
		return new ResourceLocation("guhao", "animations/sheath.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(SheathItem animatable) {
		return new ResourceLocation("guhao", "geo/sheath.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SheathItem animatable) {
		return new ResourceLocation("guhao", "textures/items/sheath.png");
	}
}
