package com.guhao.renderers;

import com.guhao.init.Effect;
import com.guhao.item.GUHAO;
import com.guhao.item.model.GUHAO2Model;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;


import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("deprecated")
@OnlyIn(Dist.CLIENT)
//壁虎立体阿尔法通道渐变变色呼吸灯式增添关键帧事件的mc事例判断的动态变换模型的动态模型和动态动画的动画搭配if判断的动态发光贴图联动efm的模型
public class GUHAORenderer extends GeoItemRenderer<GUHAO> {
	public GUHAORenderer() {
		super(new GUHAO2Model());
	}

	@Override
	public RenderType getRenderType(GUHAO animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	private static final float SCALE_RECIPROCAL = 1.0f / 16.0f;
	protected boolean renderArms = false;
	protected MultiBufferSource currentBuffer;
	protected RenderType renderType;
	public ItemDisplayContext transformType;
	protected GUHAO animatable;
	private final Set<String> hiddenBones = new HashSet<>();
	private final Set<String> suppressedBones = new HashSet<>();

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int p_239207_6_) {
		this.transformType = transformType;
		if (this.animatable != null)
			this.animatable.getTransformType(transformType);
		super.renderByItem(stack, transformType, matrixStack, bufferIn, combinedLightIn, p_239207_6_);
	}

	@Override
	public void actuallyRender(PoseStack matrixStackIn, GUHAO animatable, BakedGeoModel model, RenderType type, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, boolean isRenderer, float partialTicks, int packedLightIn,
							   int packedOverlayIn, float red, float green, float blue, float alpha) {
		this.currentBuffer = renderTypeBuffer;
		this.renderType = type;
		this.animatable = animatable;
		if (this.renderArms) {
			this.renderArms = false;
		}
		packedLightIn = 0xf000ff;
		Minecraft mc = Minecraft.getInstance();
		alpha = 112.5f;
		float minAlpha = 20.0f; // 最小alpha值
		float maxAlpha = 255.0f; // 最大alpha值
		float minRed = 0.0f; // 最小Red值
		float minGreen = 30.0f; //
		float minBlue = 30.0f; //
		float maxRed = 255.0f; // 最大Red值
		float maxGreen = 255.0f; //
		float maxBlue = 255.0f; //
		float time = mc.getFrameTime();
		float cycleTime = 5.0f; // 一个渐变循环的速率
		float progress = (float) Math.sin(Math.PI * 2 * (time % cycleTime) / cycleTime);

// 将 progress 的范围从 [-1, 1] 转换到 [0, 1]
		progress = (progress + 1) / 2;
		alpha = Mth.lerp(progress, minAlpha, maxAlpha);
		if (mc.player != null && mc.player.hasEffect(Effect.GUHAO.get())) {
			green = Mth.lerp(progress, minGreen, maxGreen);
			blue = Mth.lerp(progress, minBlue, maxBlue);
		}
		super.actuallyRender(matrixStackIn, animatable, model, type, renderTypeBuffer, vertexBuilder, isRenderer, partialTicks, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	@Override
	public ResourceLocation getTextureLocation(GUHAO instance) {
		return super.getTextureLocation(instance);
	}

}
