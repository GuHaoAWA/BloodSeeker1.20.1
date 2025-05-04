package com.guhao.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

public class ApartEntityPatch extends HumanoidMobPatch<ApartEntity> {

    public ApartEntityPatch() {
        super(Faction.VILLAGER);

    }

    @Override
    public EpicFightDamageSource getDamageSource(StaticAnimation animation, InteractionHand hand) {
        EpicFightDamageSource newEpicFightDamage;
        if(this.getOriginal().getOwner() != null){
            newEpicFightDamage = EpicFightCapabilities.getEntityPatch(this.getOriginal().getOwner(), PlayerPatch.class).getDamageSource(animation, hand);
        } else {
            newEpicFightDamage = super.getDamageSource(animation, hand);
        }
        return newEpicFightDamage;
    }

    @Override
    public void initAnimator(Animator animator) {
        animator.addLivingAnimation(LivingMotions.DEATH, null);
    }

    @Override
    public void updateMotion(boolean b) {
        super.commonAggressiveMobUpdateMotion(b);
    }
    @Override
    public boolean isTeammate(Entity entityIn) {
        if(entityIn instanceof ApartEntity apartEntity && apartEntity.getOwner() == this.getOriginal().getOwner()){
            return false;
        }
        if(entityIn.equals(this.getOriginal().getOwner())){
            return false;
        }
        return super.isTeammate(entityIn);
    }

}
