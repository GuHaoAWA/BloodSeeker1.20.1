package com.guhao.events;

import com.guhao.epicfight.skills.GuHaoSkills;
import com.guhao.init.Effect;
import com.guhao.init.Items;
import com.guhao.init.Key;
import com.guhao.utils.ArrayUtils;
import com.mafuyu404.smartkeyprompts.util.KeyUtils;
import com.mafuyu404.smartkeyprompts.util.PromptUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import static com.guhao.GuhaoMod.MODID;
import static com.guhao.epicfight.GuHaoSkillDataKeys.*;
import static com.mafuyu404.smartkeyprompts.util.PromptUtils.addDesc;

@Mod.EventBusSubscriber(modid= MODID, value=Dist.CLIENT)
public class BSKeyEvent {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = Minecraft.getInstance().player;
        PlayerPatch<?> pp = EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
        if (pp == null) return;
        SkillContainer sacrifice = pp.getSkill(GuHaoSkills.SACRIFICE);
        SkillContainer passive = pp.getSkill(GuHaoSkills.GUHAO_PASSIVE);
        if (sacrifice == null || passive == null || !pp.isBattleMode() || Minecraft.getInstance().screen != null || !player.getMainHandItem().is(Items.GUHAO.get())) return;
        boolean hasEffect = player.hasEffect(Effect.GUHAO.get());

        Skill skill = pp.getSkill(SkillSlots.WEAPON_INNATE).getSkill();
        int stack = sacrifice.getStack();
        boolean canExe = skill.canExecute(pp);
        boolean isStop = pp.getOriginal().isSprinting();
        boolean isOnGround = pp.getOriginal().onGround();
        DynamicAnimation animation = pp.getAnimator().getPlayerFor(null).getAnimation();
        if (Key.RIGHT.isDown) {
            PromptUtils.custom("epicfight", KeyUtils.getKeyByDesc("key.epicfight.attack"),"识破斩");
        } else {
            PromptUtils.show("epicfight", "key.epicfight.attack");
        }
        PromptUtils.show(MODID, "key.guhao.ender");
        PromptUtils.show(MODID, "key.guhao.blood_burst");

        if (canExe && (stack > 0 || player.isCreative())) {
            while (true) {
                boolean isSheathed = passive.getDataManager().getDataValue(SHEATH.get());
                if ((stack >= 10)) {
                    if (hasEffect) {
                        addDesc("§4鲜血审判").withKeyAlias("按住shift+" + KeyUtils.getKeyDisplayName("key.epicfight.weapon_innate_skill")).forKey(KeyUtils.getKeyByDesc("key.epicfight.weapon_innate_skill")).withCustom(true).toGroup("epicfight");
                    } else {
                        addDesc("§4献祭").withKeyAlias("按住shift+" + KeyUtils.getKeyDisplayName("key.epicfight.weapon_innate_skill")).forKey(KeyUtils.getKeyByDesc("key.epicfight.weapon_innate_skill")).withCustom(true).toGroup("epicfight");
                    }

                }
                addDesc("关刀技").withKeyAlias("按住ctrl+" + KeyUtils.getKeyDisplayName("key.epicfight.weapon_innate_skill")).forKey(KeyUtils.getKeyByDesc("key.epicfight.weapon_innate_skill")).withCustom(true).toGroup("epicfight");


                if (pp.getTarget() != null && (stack >= 13 && ((pp.getTarget().getHealth() <= pp.getTarget().getMaxHealth() * 0.1f) || (pp.getTarget().getHealth() <= 10.0f))) && !isOnGround) {
                    PromptUtils.custom("epicfight", KeyUtils.getKeyByDesc("key.epicfight.weapon_innate_skill"), "§4真·处决");
                    break;
                }

                if (animation instanceof StaticAnimation staticAnimation && ArrayUtils.isEyes(staticAnimation) && pp.getEntityState().getLevel() == 3) {
                    PromptUtils.custom("epicfight", KeyUtils.getKeyByDesc("key.epicfight.weapon_innate_skill"), "EX技");
                    break;
                }


                if (isOnGround && !isStop && isSheathed) {
                    PromptUtils.custom("epicfight", KeyUtils.getKeyByDesc("key.epicfight.weapon_innate_skill"), "大居合");
                    break;
                }
                PromptUtils.custom("epicfight", KeyUtils.getKeyByDesc("key.epicfight.weapon_innate_skill"), "小居合");
                break;
            }
        }
        if (pp.getTarget() != null && (pp.getEntityState().getLevel() == 2 || pp.getEntityState().getLevel() == 3)) {
            PromptUtils.show(MODID, "key.guhao.red_fist");
        }
    }
}
