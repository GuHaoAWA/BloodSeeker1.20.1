package com.guhao.utils;

import com.dfdyz.epicacg.client.screeneffect.ColorDispersionEffect;
import com.guhao.entity.ApartEntity;
import com.guhao.init.*;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.spells.blood_needle.BloodNeedle;
import io.redspace.ironsspellbooks.entity.spells.blood_slash.BloodSlashProjectile;
import io.redspace.ironsspellbooks.entity.spells.devour_jaw.DevourJaw;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.HurtableEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.effect.EpicFightMobEffects;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.*;

import static com.github.alexthe666.alexsmobs.effect.AMEffectRegistry.EXSANGUINATION;

public class BattleUtils {
    public BattleUtils() {
    }
    public static class Guhao_Battle_utils {
        public static void ender(LivingEntityPatch<?> livingEntityPatch) {
            float speed = 2.5F;
            Entity _shootFrom = livingEntityPatch.getOriginal();
            Level projectileLevel = _shootFrom.level();
            Projectile _entityToSpawn = new Object() {
                public Projectile getProjectile() {
                    Level level = livingEntityPatch.getOriginal().level();
                    Entity shooter = livingEntityPatch.getOriginal();
                    Projectile entityToSpawn = new ThrownEnderpearl(EntityType.ENDER_PEARL, level);
                    entityToSpawn.setOwner(shooter);
                    return entityToSpawn;
                }
            }.getProjectile();
            _entityToSpawn.setPos(_shootFrom.getX(), _shootFrom.getEyeY() - 0.09, _shootFrom.getZ());
            _entityToSpawn.shoot(_shootFrom.getLookAngle().x, _shootFrom.getLookAngle().y, _shootFrom.getLookAngle().z, speed, 0);
            projectileLevel.addFreshEntity(_entityToSpawn);
        }
        public static void blood_needles(LivingEntityPatch<?> ep) {
            ep.playSound(SoundRegistry.BLOOD_CAST.get(), 1.0F, 1.0F);
                Random random = new Random();
                Level world = ep.getOriginal().level();
                LivingEntity entity = ep.getOriginal();
                int count = 10;
                float damage = random.nextFloat(6f, 12f);
                int degreesPerNeedle = 360 / count;
                var raycast = Utils.raycastForEntity(world, entity, 32, true);
                for (int i = 0; i < count; i++) {
                    BloodNeedle needle = new BloodNeedle(world, entity);
                    int rotation = degreesPerNeedle * i - (degreesPerNeedle / 2);
                    needle.setDamage(damage);
                    needle.setZRot(rotation);
                    Vec3 spawn = entity.getEyePosition().add(new Vec3(0, 1.5, 0).zRot(rotation * Mth.DEG_TO_RAD).xRot(-entity.getXRot() * Mth.DEG_TO_RAD).yRot(-entity.getYRot() * Mth.DEG_TO_RAD));
                    needle.moveTo(spawn);
                    needle.shoot(raycast.getLocation().subtract(spawn).normalize());
                    world.addFreshEntity(needle);
                }
        }
        public static void blood_blade(LivingEntityPatch<?> ep) {
            ep.playSound(SoundRegistry.BLOOD_CAST.get(),1.0F,1.0F);
            Random random = new Random();
            Level world = ep.getOriginal().level();
            LivingEntity entity = ep.getOriginal();
            BloodSlashProjectile bloodSlash = new BloodSlashProjectile(world, entity);
            bloodSlash.setPos(entity.getEyePosition());
            bloodSlash.shoot(new Vec3(entity.getLookAngle().x(),entity.getLookAngle().y(),entity.getLookAngle().z()));
//            Vec3 look = entity.getLookAngle();
//            double x = look.x;
//            double z = look.z;
//            double y = look.y;
//            bloodSlash.setDeltaMovement(x * 1.0, y * 1.0, z * 1.0);
            bloodSlash.setRadius(10f);
            bloodSlash.setDamage(random.nextFloat(28f,34f));
            world.addFreshEntity(bloodSlash);
        }
        public static void sacrifice(LivingEntityPatch<?> livingEntityPatch){
            livingEntityPatch.playSound(Sounds.LAUGH.get(),1.0F,1.0F,1.0F);
            Level level = livingEntityPatch.getOriginal().level();
            Vec3 position = livingEntityPatch.getOriginal().position();
            double x = position.x;
            double y = position.y;
            double z = position.z;
            if (level instanceof ServerLevel _level) {
                LightningBolt entityToSpawn = EntityType.LIGHTNING_BOLT.create(level);
                entityToSpawn.moveTo(Vec3.atBottomCenterOf(new BlockPos((int) x, (int) y, (int) z)));
                entityToSpawn.setVisualOnly(true);
                _level.addFreshEntity(entityToSpawn);
                _level.sendParticles(ParticleType.RED_RING.get(), x, y, z, 1, 0.1, 0.1, 0.1, 0);
                _level.sendParticles(ParticleType.RED_RING.get(), x, y+0.1, z, 1, 0.1, 0.1, 0.1, 0);
                _level.sendParticles(ParticleType.RED_RING.get(), x, y+0.2, z, 1, 0.1, 0.1, 0.1, 0);
                _level.sendParticles(ParticleType.CONQUEROR_HAKI.get(), x, y+1.0, z, 1, 0.1, 0.1, 0.1, 0);
                _level.sendParticles(ParticleType.CONQUEROR_HAKI_FLOOR.get(), x, y+1.0, z, 1, 0.1, 0.1, 0.1, 0);
                _level.sendParticles(EpicFightParticles.EVISCERATE.get(), x, y+0.45, z, 1, 0, 0, 0, 0);
            }
            livingEntityPatch.getOriginal().clearFire();
            livingEntityPatch.getOriginal().addEffect(new MobEffectInstance(Effect.GUHAO.get(), 1210, 0, false, true));
            livingEntityPatch.getOriginal().addEffect(new MobEffectInstance(EpicFightMobEffects.STUN_IMMUNITY.get(), 1210, 0, false, true));
            livingEntityPatch.getOriginal().addEffect(new MobEffectInstance(com.guhao.stars.regirster.Effect.REALLY_STUN_IMMUNITY.get(), 1210, 0, false, true));

            List<Entity> _entfound = level.getEntitiesOfClass(Entity.class, new AABB(position, position).inflate(50 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(position))).toList();
            for (Entity entityiterator : _entfound) {
                if ((entityiterator instanceof LivingEntity livingEntity) && (!((entityiterator == livingEntityPatch.getOriginal()) || entityiterator instanceof Player))) {
                    if (livingEntity.getAttributes().hasAttribute(EpicFightAttributes.STAMINA_REGEN.get())) livingEntity.getAttribute(EpicFightAttributes.STAMINA_REGEN.get()).setBaseValue(livingEntity.getAttribute(EpicFightAttributes.STAMINA_REGEN.get()).getValue() * 0.5);
                    if (livingEntity.getAttributes().hasAttribute(EpicFightAttributes.MAX_STAMINA.get())) livingEntity.getAttribute(EpicFightAttributes.MAX_STAMINA.get()).setBaseValue(livingEntity.getAttribute(EpicFightAttributes.MAX_STAMINA.get()).getValue() * 0.5);
                    if (livingEntity.getAttributes().hasAttribute(livingEntity.getAttribute(Attributes.ARMOR).getAttribute())) livingEntity.getAttribute(Attributes.ARMOR).setBaseValue(0);
                    if (livingEntity.getAttributes().hasAttribute(Attributes.ARMOR_TOUGHNESS)) livingEntity.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(0);
                    livingEntity.setTicksFrozen(120);
                    livingEntity.hurt(new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MAGIC), livingEntityPatch.getOriginal()), 1.0f);
                    LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(livingEntity, LivingEntityPatch.class);
                    if (entitypatch != null) {
//                        entitypatch.playAnimationSynchronized(Animations.BIPED_KNOCKDOWN, 0.0F);
                        entitypatch.applyStun(StunType.KNOCKDOWN,5.0f);
                    }
                    if (livingEntity.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleType.TWO_EYE.get(), (livingEntity.getX()), (livingEntity.getY()+1), (livingEntity.getZ()), 1, 0, 0,0,0);
                        serverLevel.sendParticles(ParticleType.ONE_JC_BLOOD_JUDGEMENT_LONG.get(), (livingEntity.getX()), (livingEntity.getY()+1), (livingEntity.getZ()), 1, 0, 0,0,0);
                    }
                }
            }

        }

        public static void blood_judgement_sound_1(LivingEntityPatch<?> ep) {
            ep.playSound(Sounds.DAO1.get(),1.2F,1.2F);
        }
        public static void blood_judgement_sound_2(LivingEntityPatch<?> ep) {
            ep.playSound(Sounds.DAO2.get(),1.2F,1.2F);
        }
        public static void blood_judgement_sound_3(LivingEntityPatch<?> ep) {
            ep.playSound(Sounds.DAO3.get(),1.2F,1.2F);
        }
        public static void blood_judgement_p1(LivingEntityPatch<?> ep) {
            Vec3 vec3 = ep.getOriginal().position();
            double x = vec3.x;
            double y = vec3.y;
            double z = vec3.z;
            Level world = ep.getOriginal().level();
            if (world instanceof ServerLevel _level) {
                for (int i = 0; i < 2; i++) {
                    _level.sendParticles(ParticleType.BLOOD_JUDGEMENT.get(), x + 5, (y + 8), z, 1, 0, 0, 0, 0);
                    _level.sendParticles(ParticleType.BLOOD_JUDGEMENT.get(), x - 5, (y + 8), z, 1, 0, 0, 0, 0);
                    _level.sendParticles(ParticleType.BLOOD_JUDGEMENT.get(), x, (y + 8), z + 5, 1, 0, 0, 0, 0);
                    _level.sendParticles(ParticleType.BLOOD_JUDGEMENT.get(), x, (y + 8), z - 5, 1, 0, 0, 0, 0);
                }
            }
        }
        public static void blood_judgement_cut(LivingEntityPatch<?> ep) {
            Vec3 vec3 = ep.getOriginal().position();
            double x = vec3.x;
            double y = vec3.y;
            double z = vec3.z;
            Level world = ep.getOriginal().level();

            {
                final Vec3 _center = new Vec3((x + 4), y, z);
                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                for (Entity entityiterator : _entfound) {
                    if (!(world.getNearestPlayer(ep.getOriginal(),-1) == null)) entityiterator.hurt(new DamageSource(ep.getOriginal().level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MAGIC), ep.getOriginal()), 12.0f);
                    entityiterator.setAirSupply(0);
                    LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(entityiterator, LivingEntityPatch.class);
                    HurtableEntityPatch<?> hurtableEntityPatch = EpicFightCapabilities.getEntityPatch(entityiterator, HurtableEntityPatch.class);
                    if (entitypatch != null) {
                        entitypatch.cancelAnyAction();
                        if (hurtableEntityPatch != null) {
                            hurtableEntityPatch.applyStun(StunType.HOLD,1.5F);
                        }
                    }
                }
            }
            {
                final Vec3 _center = new Vec3((x - 4), y, z);
                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                for (Entity entityiterator : _entfound) {
                    if (!(world.getNearestPlayer(ep.getOriginal(),-1) == null)) entityiterator.hurt(new DamageSource(ep.getOriginal().level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MAGIC), ep.getOriginal()), 12.0f);
                    entityiterator.setAirSupply(0);
                    LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(entityiterator, LivingEntityPatch.class);
                    HurtableEntityPatch<?> hurtableEntityPatch = EpicFightCapabilities.getEntityPatch(entityiterator, HurtableEntityPatch.class);
                    if (entitypatch != null) {
                        entitypatch.cancelAnyAction();
                        if (hurtableEntityPatch != null) {
                            hurtableEntityPatch.applyStun(StunType.HOLD,1.5F);
                        }
                    }
                }
            }
            {
                final Vec3 _center = new Vec3(x, y, z+4);
                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                for (Entity entityiterator : _entfound) {
                    if (!(world.getNearestPlayer(ep.getOriginal(),-1) == null)) entityiterator.hurt(new DamageSource(ep.getOriginal().level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MAGIC), ep.getOriginal()), 12.0f);
                    entityiterator.setAirSupply(0);
                    LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(entityiterator, LivingEntityPatch.class);
                    HurtableEntityPatch<?> hurtableEntityPatch = EpicFightCapabilities.getEntityPatch(entityiterator, HurtableEntityPatch.class);
                    if (entitypatch != null) {
                        entitypatch.cancelAnyAction();
                        if (hurtableEntityPatch != null) {
                            hurtableEntityPatch.applyStun(StunType.HOLD,1.5F);
                        }
                    }
                }
            }
            {
                final Vec3 _center = new Vec3(x, y, z-4);
                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                for (Entity entityiterator : _entfound) {
                    if (!(world.getNearestPlayer(ep.getOriginal(),-1) == null)) entityiterator.hurt(new DamageSource(ep.getOriginal().level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MAGIC), ep.getOriginal()), 12.0f);
                    entityiterator.setAirSupply(0);
                    LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(entityiterator, LivingEntityPatch.class);
                    HurtableEntityPatch<?> hurtableEntityPatch = EpicFightCapabilities.getEntityPatch(entityiterator, HurtableEntityPatch.class);
                    if (entitypatch != null) {
                        entitypatch.cancelAnyAction();
                        if (hurtableEntityPatch != null) {
                            hurtableEntityPatch.applyStun(StunType.HOLD,1.5F);
                        }
                    }
                }
            }
        }

        public static void blood_judgement_p2(LivingEntityPatch<?> ep) {
            Entity entity = ep.getOriginal();
            entity.level().addParticle(ParticleType.ENTITY_AFTER_IMG_BLOOD.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0.0, 0.0);
        }

        public static void blood_judgement_post(LivingEntityPatch<?> ep) {
            Vec3 pos = Minecraft.getInstance().player.position();
            ColorDispersionEffect effect = new ColorDispersionEffect(pos);
            effect.lifetime = 58;
            //ScreenEffectEngine.PushScreenEffect(effect);
        }

        public static void blood_judgement_hurt(LivingEntityPatch<?> ep) {
            if (ep == null) {
                // 处理 ep 为 null 的情况
                return;
            }

            if (ep.isLogicalClient()) {
                // 处理 ep 是逻辑客户端的情况
                return;
            }

            Collection<?> currentlyAttackedEntities = ep.getCurrenltyAttackedEntities();
            if (currentlyAttackedEntities == null || currentlyAttackedEntities.isEmpty()) {
                // 处理 currentlyAttackedEntities 为 null 或空的情况
                return;
            }

            currentlyAttackedEntities.forEach((entity) -> {
                if (entity instanceof LivingEntity) {
                    LivingEntity le = (LivingEntity) entity;
                    if (le.equals(ep.getOriginal())) {
                        return;
                    }

                    HurtableEntityPatch<?> lep = EpicFightCapabilities.getEntityPatch(le, HurtableEntityPatch.class);
                    if (lep == null) {
                        return;
                    }

                    EpicFightParticles.EVISCERATE.get().spawnParticleWithArgument((ServerLevel) lep.getOriginal().level(), HitParticleType.MIDDLE_OF_ENTITIES, HitParticleType.ZERO, lep.getOriginal(), lep.getOriginal());
                    lep.applyStun(StunType.KNOCKDOWN, 5.0F);
                }
            });
        }

        public static void blood_burst(LivingEntityPatch<?> ep) {
            Vec3 vec3 = ep.getOriginal().position();
            double x = vec3.x;
            double y = vec3.y;
            double z = vec3.z;
            Level world = ep.getOriginal().level();
            {
                final Vec3 _center = new Vec3(x, y, z);
                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(60.0 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                for (Entity entityiterator : _entfound) {
                    if (entityiterator instanceof LivingEntity livingEntity && livingEntity.hasEffect(new MobEffectInstance(EXSANGUINATION.get()).getEffect()) && !(livingEntity instanceof Player)) {
                        int amplifier = livingEntity.getEffect(new MobEffectInstance(EXSANGUINATION.get()).getEffect()).getAmplifier();
                        if (world instanceof ServerLevel _level) {
                            ArrayUtils.playSound(ep.getOriginal(),Sounds.BLOOD.get(),1.0f,0.85f,1.15f);

                            _level.sendParticles(EpicFightParticles.EVISCERATE.get(), livingEntity.getX(), livingEntity.getY() + 1.15, livingEntity.getZ(), 1, 0.25, 0.25, 0.25, 0);
                            _level.sendParticles(ParticleType.EYE.get(), livingEntity.getX(), livingEntity.getY() + 1.15, livingEntity.getZ(), 1, 0.25, 0.25, 0.25, 0);
                        }
                        livingEntity.hurt(new DamageSource(ep.getOriginal().level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MAGIC), ep.getOriginal()), (amplifier+1) * 3);
                        livingEntity.removeEffect(EXSANGUINATION.get());
                        //LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(entityiterator, LivingEntityPatch.class);
                        HurtableEntityPatch<?> hurtableEntityPatch = EpicFightCapabilities.getEntityPatch(entityiterator, HurtableEntityPatch.class);
                        if (hurtableEntityPatch != null) {
                            hurtableEntityPatch.applyStun(StunType.LONG, 4.0F);
                        }
                    }
                }
            }
        }

        public static void blood_judgement_effect(LivingEntityPatch<?> ep) {
            Vec3 vec3 = ep.getOriginal().position();
            double x = vec3.x;
            double y = vec3.y;
            double z = vec3.z;
            Level world = ep.getOriginal().level();
            {
                final Vec3 _center = new Vec3((x + 4), y, z);
                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                for (Entity entityiterator : _entfound) {
                    if (entityiterator instanceof LivingEntity livingEntity) livingEntity.addEffect(new MobEffectInstance(new MobEffectInstance(com.guhao.stars.regirster.Effect.EXECUTED.get()).getEffect(), 50, 0, false, true));
                }
            }
            {
                final Vec3 _center = new Vec3((x - 4), y, z);
                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                for (Entity entityiterator : _entfound) {
                    if (entityiterator instanceof LivingEntity livingEntity) livingEntity.addEffect(new MobEffectInstance(new MobEffectInstance(com.guhao.stars.regirster.Effect.EXECUTED.get()).getEffect(), 50, 0, false, true));
                }
            }
            {
                final Vec3 _center = new Vec3(x, y, z+4);
                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                for (Entity entityiterator : _entfound) {
                    if (entityiterator instanceof LivingEntity livingEntity) livingEntity.addEffect(new MobEffectInstance(new MobEffectInstance(com.guhao.stars.regirster.Effect.EXECUTED.get()).getEffect(), 50, 0, false, true));
                }
            }
            {
                final Vec3 _center = new Vec3(x, y, z-4);
                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                for (Entity entityiterator : _entfound) {
                        if (entityiterator instanceof LivingEntity livingEntity) livingEntity.addEffect(new MobEffectInstance(new MobEffectInstance(com.guhao.stars.regirster.Effect.EXECUTED.get()).getEffect(), 50, 0, false, true));
                    }
                }
            }

        public static void explosion(LivingEntityPatch<?> ep) {
            if (ep.getOriginal().level() instanceof ServerLevel _level) {
                _level.sendParticles(ParticleTypes.FLAME, ep.getOriginal().getX(), ep.getOriginal().getY(), ep.getOriginal().getZ(), 10, 2, 2, 2, 0.2);
                _level.sendParticles(ParticleTypes.EXPLOSION, ep.getOriginal().getX(), ep.getOriginal().getY(), ep.getOriginal().getZ(), 1, 0, 0, 0, 0);
            }
        }

        public static void explosion2(LivingEntityPatch<?> ep) {
//            epExplosion(ep,0,0,0);
//            epExplosion(ep,4,0,0);
//            epExplosion(ep,8,0,0);
//            epExplosion(ep,0,0,4);
//            epExplosion(ep,0,0,8);
//            epExplosion(ep,0,0,-4);
//            epExplosion(ep,0,0,-8);
//            epExplosion(ep,-4,0,0);
//            epExplosion(ep,-8,0,0);
//            epExplosion(ep,4,0,4);
//            epExplosion(ep,4,0,-4);
//            epExplosion(ep,-4,0,4);
//            epExplosion(ep,-4,0,-4);
        }
        public static void epExplosion(LivingEntityPatch<?> ep, double xo, double yo, double zo) {
//            ep.getOriginal().level().explode(ep.getOriginal(), ep.getOriginal().getX() + xo, ep.getOriginal().getY() + yo, ep.getOriginal().getZ() + zo, 16, true, Explosion.BlockInteraction.DESTROY);
        }

        public static void explosionEffect(LivingEntityPatch<?> ep) {
            ep.getOriginal().addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,220,36,false,false));
        }


        public static void dodge(LivingEntityPatch<?> ep) {
            PlayerPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(ep.getOriginal(), PlayerPatch.class);
            if (entitypatch != null) {
                entitypatch.getEventListener().removeListener(PlayerEventListener.EventType.HURT_EVENT_PRE, UUID.fromString("f6f8c8d8-6e54-4b02-8f18-7c6f3e6e3f6f"));
            }
        }

        public static void blood_devour(LivingEntityPatch<?> ep) {
            if (ep.getOriginal().level instanceof ServerLevel world) {
                Vec3 position = ep.getOriginal().position();
                double x = position.x;
                double y = position.y;
                double z = position.z;
                world.sendParticles(ParticleType.RED_RING.get(), x, y + 0.3, z, 1, 0.1, 0.1, 0.1, 0);
                world.sendParticles(ParticleType.CONQUEROR_HAKI_FLOOR.get(), x, y+1.0, z, 1, 0.1, 0.1, 0.1, 0);
                ep.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.totem.use")),1.0f,1.0f,1.0f);

            }
            List<LivingEntity> attackedEntities = ep.getCurrenltyAttackedEntities();
            if (attackedEntities != null) {
                for (LivingEntity entity : attackedEntities) {
                    if (entity != null) {
                        DevourJaw devour = new DevourJaw(ep.getOriginal().level(), ep.getOriginal(), entity);
                        devour.setPos(entity.position());
                        devour.setYRot(ep.getOriginal().getYRot());
                        devour.setDamage(24);
                        devour.vigorLevel = 12;
                        ep.getOriginal().level().addFreshEntity(devour);
                    }
                }
            }
        }

        public static void blood_judgement_p3(LivingEntityPatch<?> ep) {
            Entity entity = ep.getOriginal();
            entity.level().addParticle(ParticleType.ENTITY_AFTER_IMG_BLOOD.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0.0, 0.0);
            if (entity.level() instanceof ServerLevel _level) {
                _level.sendParticles(ParticleType.CONQUEROR_HAKI.get(), entity.getX(), entity.getY() + 1.0, entity.getZ(), 1, 0.1, 0.1, 0.1, 0);
            }
        }
    }
}