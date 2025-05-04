
package com.guhao.client.gui;

import com.guhao.epicfight.skills.GuHaoSkills;
import com.guhao.utils.ArrayUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;



@Mod.EventBusSubscriber({Dist.CLIENT})
public class PassiveOverlay {
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		Minecraft minecraft = Minecraft.getInstance();

            int w = event.getWindow().getGuiScaledWidth();
            int h = event.getWindow().getGuiScaledHeight();
            int posX = w / 2;
            int posY = h / 2;
            LocalPlayerPatch lpp = EpicFightCapabilities.getEntityPatch(minecraft.player, LocalPlayerPatch.class);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.setShaderColor(1, 1, 1, 1);
            if (lpp != null && lpp.getSkill(GuHaoSkills.GUHAO_PASSIVE) != null && lpp.getAnimator().getPlayerFor(null).getAnimation() instanceof StaticAnimation staticAnimation && ArrayUtils.isEyes(staticAnimation) && lpp.getEntityState().getLevel() == 3) {
                event.getGuiGraphics().blit(new ResourceLocation("guhao:textures/gui/skills/guhao_passive.png"), posX - 16, posY - 16, 0, 0, 32, 32, 32, 32);
            }
        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);


	}
}
