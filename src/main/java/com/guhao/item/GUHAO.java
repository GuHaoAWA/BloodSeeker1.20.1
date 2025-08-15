package com.guhao.item;

import com.guhao.client.text.ColorPutter;
import com.guhao.epicfight.GuHaoSkillDataKeys;
import com.guhao.epicfight.skills.GuHaoSkills;
import com.guhao.events.HitEvent;
import com.guhao.renderers.GUHAORenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.item.WeaponItem;

import java.util.List;
import java.util.function.Consumer;

public class GUHAO extends WeaponItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public String animationprocedure = "empty";
    public static ItemDisplayContext transformType;


    public GUHAO() {
        super(new Tier() {

            public int getUses() {return 666666;}

            public float getSpeed() {
                return 9.0f;
            }

            public float getAttackDamageBonus() {
                return 15f;
            }

            public int getLevel() {
                return 4;
            }

            public int getEnchantmentValue() {
                return 99;
            }

            public @NotNull Ingredient getRepairIngredient() {return Ingredient.of(new ItemStack(Items.ENDER_EYE));}

              }, 3, -2.45f,
                new Properties().fireResistant().rarity(Rarity.EPIC));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            private final BlockEntityWithoutLevelRenderer renderer = new GUHAORenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }

    public void getTransformType(ItemDisplayContext type) {
        transformType = type;
    }


    private PlayState idlePredicate(AnimationState event) {
        if (transformType != null) {
            if (this.animationprocedure.equals("empty")) {
                event.getController().setAnimation(RawAnimation.begin().thenLoop("0"));
                return PlayState.CONTINUE;
            }
        }
        return PlayState.STOP;
    }

    String prevAnim = "empty";

    private PlayState procedurePredicate(AnimationState event) {
        if (transformType != null) {
            if (!this.animationprocedure.equals("empty") && event.getController().getAnimationState() == AnimationController.State.STOPPED || (!this.animationprocedure.equals(prevAnim) && !this.animationprocedure.equals("empty"))) {
                if (!this.animationprocedure.equals(prevAnim))
                    event.getController().forceAnimationReset();
                event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
                if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
                    this.animationprocedure = "empty";
                    event.getController().forceAnimationReset();
                }
            } else if (this.animationprocedure.equals("empty")) {
                prevAnim = "empty";
                return PlayState.STOP;
            }
        }
        prevAnim = this.animationprocedure;
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        AnimationController procedureController = new AnimationController(this, "procedureController", 0, this::procedurePredicate);
        data.add(procedureController);
        AnimationController idleController = new AnimationController(this, "idleController", 0, this::idlePredicate);
        data.add(idleController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemstack, Level world, @NotNull List<Component> list, @NotNull TooltipFlag flag) {
        super.appendHoverText(itemstack, world, list, flag);
        list.add(Component.literal("\u00A74\u00A7k6666666666guhao6666666666666"));
        list.add(Component.literal("\u00A7c消散于迷雾之中吧..."));
        list.add(Component.literal("<GuHao_>\u00A7clord ot bthnkor ng gn'th'bthnk"));
        list.add(Component.literal("\u00A7cfeel r'luh ot GuHao_"));
//        list.add(Component.literal(ColorPutter.rainbow("\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7\u00A7")));
        list.add(Component.literal("\u00A74请查看jei"));
        list.add(Component.literal(ColorPutter.rainbow4(I18n.get("word.explain2"))));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(@NotNull ItemStack itemstack) {
        if (Minecraft.getInstance().player != null) {
            PlayerPatch<?> pp = EpicFightCapabilities.getEntityPatch(Minecraft.getInstance().player, PlayerPatch.class);
            return pp != null && pp.getSkill(GuHaoSkills.GUHAO_PASSIVE) != null && pp.getSkill(GuHaoSkills.GUHAO_PASSIVE).getDataManager().getDataValue(GuHaoSkillDataKeys.SHEATH.get());
        }
        return false;
    }
    @Override
    public boolean hurtEnemy(@NotNull ItemStack itemstack, @NotNull LivingEntity entity, @NotNull LivingEntity sourceentity) {
        boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
        HitEvent.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity, sourceentity);
        return retval;
    }


}