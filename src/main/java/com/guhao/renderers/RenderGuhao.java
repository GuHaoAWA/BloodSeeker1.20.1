package com.guhao.renderers;
import com.guhao.epicfight.GuHaoSkillDataKeys;
import com.guhao.init.Items;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@OnlyIn(Dist.CLIENT)

public class RenderGuhao extends RenderItemBase {

    public void renderItemInHand(ItemStack stack, LivingEntityPatch<?> entitypatch, InteractionHand hand, HumanoidArmature armature, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight) {

        LivingEntity entity = entitypatch.getOriginal();
        PlayerPatch playerpatch = EpicFightCapabilities.getEntityPatch(entity, PlayerPatch.class);
        if(entitypatch == null)
            return;
        if (playerpatch.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().hasData(GuHaoSkillDataKeys.SHEATH.get()) &&
                playerpatch.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().getDataValue(GuHaoSkillDataKeys.SHEATH.get()))
        {
            OpenMatrix4f modelMatrix = new OpenMatrix4f(this.mainhandcorrectionMatrix);
            modelMatrix.mulFront(poses[armature.toolL.getId()]);
            stack = new ItemStack(Items.GUHAO.get());

            poseStack.pushPose();
            this.mulPoseStack(poseStack, modelMatrix);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, null, 0);
            poseStack.popPose();
        }
        else
        {
            OpenMatrix4f modelMatrix = new OpenMatrix4f(this.mainhandcorrectionMatrix);
            modelMatrix.mulFront(poses[armature.toolR.getId()]);
            OpenMatrix4f modelMatrix_1 = new OpenMatrix4f(this.mainhandcorrectionMatrix);
            modelMatrix_1.mulFront(poses[armature.toolL.getId()]);
            stack = new ItemStack(Items.GUHAO.get());

            poseStack.pushPose();
            this.mulPoseStack(poseStack, modelMatrix);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, null, 0);
            poseStack.popPose();

            poseStack.pushPose();
            this.mulPoseStack(poseStack, modelMatrix_1);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, null, 0);
            poseStack.popPose();

        }
    }
}