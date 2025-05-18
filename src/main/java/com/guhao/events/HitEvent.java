package com.guhao.events;

import com.guhao.GuhaoMod;
import com.guhao.epicfight.GuHaoAnimations;
import com.guhao.init.Effect;
import com.guhao.init.ParticleType;
import com.guhao.init.Sounds;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import reascer.wom.gameasset.WOMAnimations;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.damagesource.StunType;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static com.github.alexthe666.alexsmobs.effect.AMEffectRegistry.EXSANGUINATION;
import static com.guhao.utils.ArrayUtils.playSound;


public class HitEvent {
    public static void execute(LevelAccessor world, double x, double y, double z, LivingEntity entity, LivingEntity player) {
        if (entity == null)
            return;


        if (!entity.hasEffect(new MobEffectInstance(EXSANGUINATION.get()).getEffect())) {
                entity.addEffect(new MobEffectInstance(new MobEffectInstance(EXSANGUINATION.get()).getEffect(), 200, 1, false, true));
            }
            else {
                entity.addEffect(new MobEffectInstance(new MobEffectInstance(EXSANGUINATION.get()).getEffect(), 200, entity.getEffect(EXSANGUINATION.get()).getAmplifier() + 1, false, true));
            }

        if (world instanceof ServerLevel _level) {
            _level.sendParticles(ParticleType.TWO_EYE.get(), x, y + 0.5, z, 1, 0.5, 0.5, 0.5, 0);
        }
        Player player1 = (Player) player;
        PlayerPatch<?> pp = EpicFightCapabilities.getEntityPatch(player1, PlayerPatch.class);
        DynamicAnimation animation = pp.getAnimator().getPlayerFor(null).getAnimation();
        if (pp.getAnimator().getPlayerFor(null).getAnimation() == WOMAnimations.AGONY_CLAWSTRIKE) {
            entity.push(entity.getX(),entity.getEyeY() + 25.0f,entity.getZ());
        }
        GuhaoMod.queueServerWork(6,() -> {
                if (animation == GuHaoAnimations.SETTLEMENT) {
                    pp.playSound(Sounds.DAO3.get(),1.0f,1f,1f);
                    Random random = new Random();
                    Vec3 papos;
                    Vec3 pos = new Vec3(x, y + 1, z);
                    boolean add = random.nextBoolean();
                    double offsetX = add ? random.nextDouble(5.0, 10.0) : -random.nextDouble(5.0, 10.0);
                    double offsetZ = add ? random.nextDouble(5.0, 10.0) : -random.nextDouble(5.0, 10.0);
                    papos = new Vec3(x + offsetX, y + 1, z + offsetZ);
                    Vec3 velocity = pos.subtract(papos).normalize();
                    if (world instanceof ServerLevel serverLevel) {
                        ParticleOptions particle = ParticleType.ONE_JC_BLOOD_JUDGEMENT_WIDE.get();
                        serverLevel.sendParticles(particle, x, y, z, 10, velocity.x, velocity.y, velocity.z, 100.0);
                    }
                    LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
                    if (entitypatch != null && entity.isAlive()) entitypatch.applyStun(StunType.HOLD, 5.0f);

                    entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MAGIC), player1), random.nextFloat(25.0F, 36.0F));
                }
            });
        
        if (player1.hasEffect(Effect.GUHAO.get())) {

               GuhaoMod.queueServerWork(1,() -> {
                    {
                        final Vec3 _center = new Vec3((player.getX()), (player.getY()), (player.getZ()));
                        List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(8.2d / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                                .toList();
                        for (Entity entityiterator : _entfound) {
                            if (!(entityiterator instanceof Player)) {
                                if (world instanceof ServerLevel _level) {
                                    _level.sendParticles(ParticleType.TWO_EYE.get(), entityiterator.getX(), entityiterator.getY()+1, entityiterator.getZ(), 1, 0.5, 0.5, 0.5, 0);
                                }
                                player1.setHealth(player1.getHealth() + 1.0F);
                                entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MAGIC), player1), 7.5F);
                            }
                        }
                    }
                        GuhaoMod.queueServerWork(14,() -> {
                            entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MAGIC), player1), 7.5F);
                            LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
                            if (entitypatch != null && entity.isAlive()) entitypatch.applyStun(StunType.HOLD, 3.0f);
                            if (world instanceof ServerLevel _level) {
                                Random r = new Random();
                                _level.sendParticles(ParticleType.ONE_JC_BLOOD_JUDGEMENT.get(), x, y, z, 1, 0.15, 0, 0.15, 0);
                                playSound(entity,Sounds.BIU.get(),1.0f,0.75f,1.25f);
                            }

                                GuhaoMod.queueServerWork(10, () -> {
                                    if (entitypatch != null && entity.isAlive()) entitypatch.applyStun(StunType.HOLD, 3.0f);
                                    if (world instanceof ServerLevel _level) {
                                        Random r = new Random();
                                        _level.sendParticles(ParticleType.ONE_JC_BLOOD_JUDGEMENT.get(), x, y, z, 1, 0.15, 0, 0.15, 0);
                                        playSound(entity,Sounds.BIU.get(),1.0f,0.75f,1.25f);
                                    }
                                    entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MAGIC), player1), 7.5F);
                                });
                        });
                });
        }
    }
}