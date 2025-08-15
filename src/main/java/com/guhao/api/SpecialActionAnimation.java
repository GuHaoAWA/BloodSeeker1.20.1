package com.guhao.api;


import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class SpecialActionAnimation extends ActionAnimation {
    public SpecialActionAnimation(float convertTime, String path, Armature armature) {
        this(convertTime, Float.MAX_VALUE, path, armature);
    }

    public SpecialActionAnimation(float convertTime, float postDelay, String path, Armature armature) {
        super(convertTime, path, armature);
        this.stateSpectrumBlueprint.clear().newTimePair(0.0F, postDelay).addState(EntityState.UPDATE_LIVING_MOTION, false).addState(EntityState.CAN_BASIC_ATTACK, false).addState(EntityState.CAN_SKILL_EXECUTION, false).newTimePair(0.01F, postDelay).addState(EntityState.TURNING_LOCKED, true).newTimePair(0.0F, Float.MAX_VALUE).addState(EntityState.INACTION, true);
    }

    public <V> SpecialActionAnimation addProperty(AnimationProperty.ActionAnimationProperty<V> propertyType, V value) {
        this.properties.put(propertyType, value);
        return this;
    }

    protected boolean shouldMove(float currentTime) {
        return true;
    }

    @Override
    protected void move(LivingEntityPatch<?> entitypatch, DynamicAnimation animation) {
    }

}
