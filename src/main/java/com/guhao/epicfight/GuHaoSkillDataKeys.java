package com.guhao.epicfight;

import com.guhao.epicfight.skills.GuHaoPassive;
import com.guhao.epicfight.skills.SacrificeSkill;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.skill.SkillDataKey;

import static com.guhao.GuhaoMod.MODID;

public class GuHaoSkillDataKeys {
    public static final DeferredRegister<SkillDataKey<?>> DATA_KEYS = DeferredRegister.create(new ResourceLocation("epicfight", "skill_data_keys"), MODID);
    public GuHaoSkillDataKeys() {
    }
    public static final RegistryObject<SkillDataKey<Boolean>> SHEATH = DATA_KEYS.register("bloodseeker_sheath", () -> SkillDataKey.createBooleanKey(false, false, GuHaoPassive.class));
    public static final RegistryObject<SkillDataKey<Boolean>> IS_RIGHT_DOWN = DATA_KEYS.register("is_right_down", () -> SkillDataKey.createBooleanKey(false, false, GuHaoPassive.class));;
    public static final RegistryObject<SkillDataKey<Boolean>> IS_CTRL_DOWN = DATA_KEYS.register("is_ctrl_down", () -> SkillDataKey.createBooleanKey(false, false, SacrificeSkill.class));;;
}
