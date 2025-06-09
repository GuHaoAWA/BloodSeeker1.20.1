package com.guhao.client.sky;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import com.mojang.math.Axis;


@Mod.EventBusSubscriber({Dist.CLIENT})
public class SkyEyeRenderer {
    private static final ResourceLocation EYE_TEXTURE = new ResourceLocation("guhao:eyes/sky_eye.png");
    private static final float EYE_SIZE = 800.0F;
    private static final float EYE_HEIGHT = 320.0F;
    private static final float BLINK_DURATION = 125; // ms
    private static final float MIN_BLINK_INTERVAL = 3000; // ms
    private static final float MAX_BLINK_INTERVAL = 10000; // ms
    public static boolean isOpen = false;

    private static float blinkProgress = 0;
    private static boolean isBlinking = false;
    private static long lastBlinkTime = 0;
    private static long nextBlinkTime = 0;

    @SubscribeEvent
    public void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        if (!isOpen) return;


        // 获取相机和玩家信息
        Camera camera = event.getCamera();
        Vec3 cameraPos = camera.getPosition();
        Vec3 playerPos = mc.player.getEyePosition(event.getPartialTick());

        // 设置渲染状态
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, EYE_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.texParameter(3553, 10242, 33071);
        RenderSystem.texParameter(3553, 10243, 33071);
        RenderSystem.texParameter(3553, 10240, 9729);
        RenderSystem.texParameter(3553, 10241, 9729);

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();

        // 定位到玩家上方（中心点调整）
        poseStack.translate(
                playerPos.x - cameraPos.x,
                playerPos.y + EYE_HEIGHT - cameraPos.y,
                playerPos.z - cameraPos.z
        );

        // 计算注视方向
        Vec3 toPlayer = new Vec3(0, -EYE_HEIGHT, 0).normalize();
        float yaw = (float)Math.atan2(toPlayer.z, toPlayer.x);
        float pitch = (float)Math.asin(toPlayer.y);

        // 应用旋转（调整旋转中心）
        float half = EYE_SIZE / 2;
        poseStack.translate(0, -half * 0, 0); // 将旋转中心上移
        poseStack.mulPose(Axis.YP.rotation(-yaw + (float)Math.PI/2));
        poseStack.mulPose(Axis.XP.rotation(-pitch));
        poseStack.translate(0, half * 0, 0); // 移回原位

        // 更新眨眼动画
        updateNaturalBlinkAnimation();

        // 渲染四边形（中心点对齐）
        Matrix4f matrix = poseStack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        float uvCenterY = 0.5f; // 假设瞳孔在纹理垂直方向25%处
        float blinkOffset = isBlinking ? blinkProgress * 0.3f : 0f;

        // 顶点数据（中心点对准上方）
        buffer.vertex(matrix, -half, -half, 0).uv(0, uvCenterY * 2 - blinkOffset).endVertex(); // 左下
        buffer.vertex(matrix, half, -half, 0).uv(1, uvCenterY * 2 - blinkOffset).endVertex();  // 右下
        buffer.vertex(matrix, half, half, 0).uv(1, 0).endVertex();                            // 右上
        buffer.vertex(matrix, -half, half, 0).uv(0, 0).endVertex();                           // 左上

        BufferUploader.drawWithShader(buffer.end());
        poseStack.popPose();

        // 恢复渲染状态
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    private void updateNaturalBlinkAnimation() {
        long now = System.currentTimeMillis();

        if (isBlinking) {
            float progress = (now - lastBlinkTime) / BLINK_DURATION;
            blinkProgress = Mth.clamp(progress, 0, 1);

            // 使用缓动函数（先慢后快）
            blinkProgress = (float) Math.sin(blinkProgress * Math.PI / 2);

            if (blinkProgress >= 1) {
                isBlinking = false;
                nextBlinkTime = now + (long)(MIN_BLINK_INTERVAL +
                        Math.random() * (MAX_BLINK_INTERVAL - MIN_BLINK_INTERVAL));
            }
        }
        else if (now >= nextBlinkTime) {
            isBlinking = true;
            lastBlinkTime = now;
            blinkProgress = 0;
        }
    }
}