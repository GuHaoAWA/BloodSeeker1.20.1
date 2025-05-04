package com.guhao.renderers;

import com.guhao.item.SheathItem;
import com.guhao.item.model.SheathItemModel;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import java.util.Set;
import java.util.HashSet;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import software.bernie.geckolib.util.RenderUtils;


@OnlyIn(Dist.CLIENT)
public class SheathItemRenderer extends GeoItemRenderer<SheathItem> {
	public SheathItemRenderer() {
		super(new SheathItemModel());
	}

	@Override
	public RenderType getRenderType(SheathItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {

		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	private static final float SCALE_RECIPROCAL = 1.0f / 16.0f;
	protected boolean renderArms = false;
	protected MultiBufferSource currentBuffer;
	protected RenderType renderType;
	public ItemDisplayContext transformType;
	protected SheathItem animatable;
	private final Set<String> hiddenBones = new HashSet<>();
	private final Set<String> suppressedBones = new HashSet<>();

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int p_239207_6_) {
		this.transformType = transformType;
		if (this.animatable != null)
			this.animatable.getTransformType(transformType);
		if (stack.getItem() instanceof SheathItem item) {
			item.getTransformType(transformType);
		}
		super.renderByItem(stack, transformType, matrixStack, bufferIn, combinedLightIn, p_239207_6_);
	}
	@Override
	public void renderRecursively(PoseStack poseStack, SheathItem animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void actuallyRender(PoseStack matrixStackIn, SheathItem animatable, BakedGeoModel model, RenderType type, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, boolean isRenderer, float partialTicks, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		this.currentBuffer = renderTypeBuffer;
		this.renderType = type;
		this.animatable = animatable;
		packedLightIn = 0xf000ff;
		Minecraft mc = Minecraft.getInstance();
		float minAlpha = 20.0f; // 最小alpha值
		float maxAlpha = 255.0f; // 最大alpha值
		float time = mc.getFrameTime();
		float cycleTime = 5.0f; // 一个渐变循环的速率
		float progress = (float) Math.sin(Math.PI * 2 * (time % cycleTime) / cycleTime);

		progress = (progress + 1) / 2;
		alpha = Mth.lerp(progress, minAlpha, maxAlpha);

		super.actuallyRender(matrixStackIn, animatable, model, type, renderTypeBuffer, vertexBuilder, isRenderer, partialTicks, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		if (this.renderArms) {
			this.renderArms = false;
		}
	}

	@Override
	public ResourceLocation getTextureLocation(SheathItem instance) {
		return super.getTextureLocation(instance);
	}
}
