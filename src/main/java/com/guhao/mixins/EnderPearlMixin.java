package com.guhao.mixins;

import com.guhao.init.Items;
import com.guhao.init.ParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ThrownEnderpearl.class, priority = 5000)
public abstract class EnderPearlMixin extends ThrowableItemProjectile {
    public EnderPearlMixin(EntityType<? extends ThrowableItemProjectile> p_37442_, Level p_37443_) {
        super(p_37442_, p_37443_);
    }

    @Inject(method = "onHit", at = @At("HEAD"))
    protected void REonHit(HitResult p_37504_, CallbackInfo ci) {
        Entity entity1 = this.getOwner();
        super.onHit(p_37504_);
        if (entity1 instanceof LivingEntity E && E.getMainHandItem().getItem() == Items.GUHAO.get()) {
            for (int i = 0; i < 66; ++i) {
                this.level().addParticle(ParticleType.BLOOD_FIRE_FLAME.get(), this.getX(), this.getY() + this.random.nextDouble() * 2.0D, this.getZ(), this.random.nextGaussian(), 0.0D, this.random.nextGaussian());
            }
        }
    }
}

