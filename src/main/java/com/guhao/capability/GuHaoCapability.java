package com.guhao.capability;

import com.guhao.GuHaoColliderPreset;
import com.guhao.GuhaoMod;
import com.guhao.epicfight.GuHaoAnimations;
import com.guhao.epicfight.GuHaoSkillDataKeys;
import com.guhao.epicfight.skills.GuHaoSkills;
import com.guhao.init.ParticleType;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import reascer.wom.gameasset.WOMAnimations;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.forgeevent.WeaponCapabilityPresetRegistryEvent;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCapability;

import java.util.function.Function;

public class GuHaoCapability {
    public static final Function<Item, CapabilityItem.Builder> GUHAO = (item) -> WeaponCapability.builder()
            .category(CapabilityItem.WeaponCategories.TACHI)
            .styleProvider((entitypatch) -> {
                if (entitypatch instanceof PlayerPatch<?> playerpatch) {
                    if (playerpatch.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().hasData(GuHaoSkillDataKeys.SHEATH.get()) && playerpatch.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().getDataValue(GuHaoSkillDataKeys.SHEATH.get())) {
                        return CapabilityItem.Styles.SHEATH;
                    }
                }

                return CapabilityItem.Styles.TWO_HAND;
            })
            .passiveSkill(GuHaoSkills.GUHAO_PASSIVE)
            .hitSound(EpicFightSounds.BLADE_HIT.get())
            .hitParticle((HitParticleType) ParticleType.EYE.get())
            .collider(GuHaoColliderPreset.GUHAO)
            .canBePlacedOffhand(false)
            .newStyleCombo(CapabilityItem.Styles.SHEATH,
                    GuHaoAnimations.GUHAO_UCHIGATANA_SHEATHING_AUTO,
                    GuHaoAnimations.GUHAO_UCHIGATANA_SHEATHING_DASH,
                    GuHaoAnimations.GUHAO_UCHIGATANA_SHEATH_AIR_SLASH)
            .newStyleCombo(CapabilityItem.Styles.TWO_HAND,
                    WOMAnimations.KATANA_AUTO_1,
                    WOMAnimations.KATANA_AUTO_2,
                    WOMAnimations.KATANA_AUTO_3,
                    Animations.TACHI_AUTO2,
                    GuHaoAnimations.HERRSCHER_AUTO_3,
                    GuHaoAnimations.GUHAO_DASH_2,
                    GuHaoAnimations.NB_ATTACK)
            .newStyleCombo(CapabilityItem.Styles.MOUNT, Animations.SPEAR_MOUNT_ATTACK)
            .innateSkill(CapabilityItem.Styles.SHEATH, (itemstack) -> GuHaoSkills.SACRIFICE)
            .innateSkill(CapabilityItem.Styles.TWO_HAND, (itemstack) -> GuHaoSkills.SACRIFICE)
            .comboCancel((style) -> false)
            .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.IDLE, Animations.BIPED_HOLD_UCHIGATANA)
            .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.KNEEL, Animations.BIPED_HOLD_UCHIGATANA)
            .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.WALK, Animations.BIPED_WALK_UCHIGATANA)
            .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.CHASE, Animations.BIPED_WALK_UCHIGATANA)
            .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.RUN, Animations.BIPED_RUN_UCHIGATANA)
            .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.SNEAK, Animations.BIPED_WALK_UCHIGATANA)
            .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.SWIM, Animations.BIPED_HOLD_UCHIGATANA)
            .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.FLOAT, Animations.BIPED_HOLD_UCHIGATANA)
            .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.FALL, Animations.BIPED_HOLD_UCHIGATANA)
            .livingMotionModifier(CapabilityItem.Styles.SHEATH, LivingMotions.IDLE, Animations.BIPED_HOLD_UCHIGATANA_SHEATHING)
            .livingMotionModifier(CapabilityItem.Styles.SHEATH, LivingMotions.KNEEL, Animations.BIPED_HOLD_UCHIGATANA_SHEATHING)
            .livingMotionModifier(CapabilityItem.Styles.SHEATH, LivingMotions.WALK, Animations.BIPED_WALK_UCHIGATANA_SHEATHING)
            .livingMotionModifier(CapabilityItem.Styles.SHEATH, LivingMotions.CHASE, Animations.BIPED_HOLD_UCHIGATANA_SHEATHING)
            .livingMotionModifier(CapabilityItem.Styles.SHEATH, LivingMotions.RUN, Animations.BIPED_RUN_UCHIGATANA_SHEATHING)
            .livingMotionModifier(CapabilityItem.Styles.SHEATH, LivingMotions.SNEAK, Animations.BIPED_HOLD_UCHIGATANA_SHEATHING)
            .livingMotionModifier(CapabilityItem.Styles.SHEATH, LivingMotions.SWIM, Animations.BIPED_HOLD_UCHIGATANA_SHEATHING)
            .livingMotionModifier(CapabilityItem.Styles.SHEATH, LivingMotions.FLOAT, Animations.BIPED_HOLD_UCHIGATANA_SHEATHING)
            .livingMotionModifier(CapabilityItem.Styles.SHEATH, LivingMotions.FALL, Animations.BIPED_HOLD_UCHIGATANA_SHEATHING)
            .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.BLOCK, WOMAnimations.RUINE_BLOCK);

    public GuHaoCapability() {
    }

    public static void register(WeaponCapabilityPresetRegistryEvent event) {
        Logger LOGGER = LogUtils.getLogger();
        LOGGER.info("Loading BloodSeekerCapability");
        event.getTypeEntry().put(new ResourceLocation(GuhaoMod.MODID, "guhao"), GUHAO);
        LOGGER.info("BloodSeekerCapability Loaded");
    }
}
