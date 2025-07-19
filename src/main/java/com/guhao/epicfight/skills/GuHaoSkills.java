package com.guhao.epicfight.skills;

import com.guhao.stars.efmex.skills.ShadowPassive;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.damagesource.EpicFightDamageType;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.StunType;

import java.util.Set;

import static com.guhao.GuhaoMod.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class GuHaoSkills {

    public static Skill SACRIFICE;
    public static Skill GUHAO_PASSIVE;
    public GuHaoSkills() {
    }


    @SubscribeEvent
    public static void buildSkillEvent(SkillBuildEvent event) {
        SkillBuildEvent.ModRegistryWorker modRegistry = event.createRegistryWorker(MODID);
        WeaponInnateSkill sacrifice = modRegistry.build("sacrifice", SacrificeSkill::new, WeaponInnateSkill.createWeaponInnateBuilder());
        sacrifice.newProperty().addProperty(AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(25.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(2.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.56F))
                .addProperty(AnimationProperty.AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
                .addProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageType.WEAPON_INNATE))
                .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get());
        SACRIFICE = sacrifice;
        GUHAO_PASSIVE = modRegistry.build("guhao_passive", GuHaoPassive::new, Skill.createBuilder().setActivateType(Skill.ActivateType.DURATION).setResource(Skill.Resource.COOLDOWN).setCategory(SkillCategories.WEAPON_PASSIVE));
    }
}