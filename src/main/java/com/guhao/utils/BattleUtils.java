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
import net.minecraft.world.entity.npc.Villager;
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
    private static void spawnBloodExplosionServer(LivingEntityPatch<?> livingEntityPatch) {
        if (livingEntityPatch.getOriginal().level() instanceof ServerLevel serverLevel) {
            double x = livingEntityPatch.getOriginal().getX();
            double y = livingEntityPatch.getOriginal().getY() + livingEntityPatch.getOriginal().getEyeHeight();
            double z = livingEntityPatch.getOriginal().getZ();

            int particleCount = 140;
            double radius = 4.0;

            for (int i = 0; i < particleCount; i++) {
                double angle = serverLevel.random.nextDouble() * Math.PI * 2;
                double distance = serverLevel.random.nextDouble() * radius;

                double px = x + Math.cos(angle) * distance;
                double py = y + (serverLevel.random.nextDouble() - 0.5) * radius;
                double pz = z + Math.sin(angle) * distance;

                double dx = (px - x) * 0.1;
                double dy = (py - y) * 0.1;
                double dz = (pz - z) * 0.1;

                serverLevel.sendParticles(EpicFightParticles.BLOOD.get(),
                        px, py, pz,
                        1, dx, dy, dz, 0.0);
            }
        }
    }
    public static void bloodRitualEffect(LivingEntityPatch<?> livingEntityPatch) {
        Level level = livingEntityPatch.getOriginal().level();
        Vec3 center = livingEntityPatch.getOriginal().position();
        int runes = 8;
        double radius = 3.0;
        double time = level.getGameTime() * 0.03;



        // 中心血柱
        for (int i = 0; i < 20; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2;
            double offset = level.random.nextDouble() * 0.5;

            double x = center.x + offset * Math.cos(angle);
            double z = center.z + offset * Math.sin(angle);

            level.addParticle(EpicFightParticles.BLOOD.get(),
                    x, center.y, z,
                    0, 0.3, 0);
        }

        // 地面血雾
        for (int i = 0; i < 30; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2;
            double distance = level.random.nextDouble() * radius;

            double x = center.x + distance * Math.cos(angle);
            double z = center.z + distance * Math.sin(angle);

            level.addParticle(ParticleTypes.DRIPPING_HONEY,
                    x, center.y + 0.1, z,
                    (level.random.nextDouble() - 0.5) * 0.1,
                    0.05,
                    (level.random.nextDouble() - 0.5) * 0.1);
        }
    }
    public static void bloodTornado(LivingEntityPatch<?> livingEntityPatch) {
        Level level = livingEntityPatch.getOriginal().level();
        Vec3 center = livingEntityPatch.getOriginal().position();
        double height = 6.0;
        double radius = 2.5;
        double time = level.getGameTime() * 0.05;

        // 龙卷风螺旋
        int spirals = 4;
        for (int s = 0; s < spirals; s++) {
            double spiralOffset = (double)s / spirals;

            for (double y = 0; y < height; y += 0.3) {
                double progress = y / height;
                double currentRadius = radius * (0.2 + 0.8 * progress);
                double angle = time * 5 + y * 0.5 + spiralOffset * Math.PI * 2;

                double x = center.x + currentRadius * Math.cos(angle);
                double z = center.z + currentRadius * Math.sin(angle);

                // 血滴粒子
                level.addParticle(EpicFightParticles.BLOOD.get(),
                        x, center.y + y, z,
                        -Math.cos(angle) * 0.1,
                        0.2,
                        -Math.sin(angle) * 0.1);

                // 随机血雾
                if (level.random.nextDouble() < 0.3) {
                    level.addParticle(ParticleTypes.DRIPPING_HONEY,
                            x, center.y + y, z,
                            (level.random.nextDouble() - 0.5) * 0.1,
                            0.1,
                            (level.random.nextDouble() - 0.5) * 0.1);
                }
            }
        }

        // 顶部漩涡
        for (int i = 0; i < 40; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2;
            double distance = level.random.nextDouble() * radius * 0.5;

            double x = center.x + distance * Math.cos(angle);
            double z = center.z + distance * Math.sin(angle);

            level.addParticle(ParticleType.BLOOD_FIRE_FLAME.get(),
                    x, center.y + height, z,
                    0, 0.3, 0);
        }

        // 地面血浪
        for (int i = 0; i < 40; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2;
            double distance = radius * (0.8 + level.random.nextDouble() * 0.4);

            double x = center.x + distance * Math.cos(angle);
            double z = center.z + distance * Math.sin(angle);

            level.addParticle(EpicFightParticles.BLOOD.get(),
                    x, center.y + 0.1, z,
                    -Math.cos(angle) * 0.05,
                    0,
                    -Math.sin(angle) * 0.05);
        }

    }
    public static void bloodAvatarEffect(LivingEntityPatch<?> livingEntityPatch) {
        Level level = livingEntityPatch.getOriginal().level();
        Vec3 center = livingEntityPatch.getOriginal().position().add(0, livingEntityPatch.getOriginal().getBbHeight() / 2, 0);
        double height = livingEntityPatch.getOriginal().getBbHeight();
        double time = level.getGameTime() * 0.04;

        // 血魔轮廓
        int layers = 5;
        for (int l = 0; l < layers; l++) {
            double layerHeight = height * l / layers;
            double layerRadius = 0.8 * (1.0 - (double)l / layers);
            int particles = 30;

            for (int i = 0; i < particles; i++) {
                double angle = 2 * Math.PI * i / particles + time;
                double pulse = 0.9 + 0.1 * Math.sin(time * 3 + l);

                double x = center.x + layerRadius * Math.cos(angle) * pulse;
                double z = center.z + layerRadius * Math.sin(angle) * pulse;
                double y = center.y + layerHeight;

                level.addParticle(ParticleType.BLOOD_FIRE_FLAME.get(),
                        x, y, z,
                        0, 0.02, 0);
            }
        }

        // 血雾环绕
        for (int i = 0; i < 40; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2;
            double distance = 1.2 + level.random.nextDouble() * 0.6;
            double yOffset = level.random.nextDouble() * height;

            double x = center.x + distance * Math.cos(angle);
            double y = center.y + yOffset;
            double z = center.z + distance * Math.sin(angle);

            // 旋转的血雾
            double dx = -Math.sin(angle + time) * 0.05;
            double dz = Math.cos(angle + time) * 0.05;

            level.addParticle(ParticleTypes.DRIPPING_HONEY,
                    x, y, z,
                    dx, 0, dz);
        }

        // 地面血环
        for (int i = 0; i < 60; i++) {
            double angle = 2 * Math.PI * i / 60;
            double pulse = 0.9 + 0.1 * Math.sin(time * 2 + angle);
            double distance = 1.5 * pulse;

            double x = center.x + distance * Math.cos(angle);
            double z = center.z + distance * Math.sin(angle);

            level.addParticle(EpicFightParticles.BLOOD.get(),
                    x, center.y, z,
                    0, 0.01, 0);
        }

        // 随机血滴上升
        for (int i = 0; i < 20; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2;
            double distance = level.random.nextDouble();

            double x = center.x + distance * Math.cos(angle);
            double z = center.z + distance * Math.sin(angle);

            level.addParticle(ParticleType.BLOOD_FIRE_FLAME.get(),
                    x, center.y, z,
                    (level.random.nextDouble() - 0.5) * 0.1,
                    0.2,
                    (level.random.nextDouble() - 0.5) * 0.1);
        }
    }
    private static void drawBloodLine(Level level, double x1, double y1, double z1,
                                      double x2, double y2, double z2) {
        double distance = Math.sqrt(
                (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) + (z2-z1)*(z2-z1)
        );
        int particles = (int)(distance * 5);

        for (int i = 0; i <= particles; i++) {
            double progress = (double)i / particles;
            double x = x1 + progress * (x2 - x1);
            double y = y1 + progress * (y2 - y1);
            double z = z1 + progress * (z2 - z1);

            level.addParticle(EpicFightParticles.BLOOD.get(), x, y, z, 0, 0, 0);
        }
    }

    public static void bloodGodIncarnation(LivingEntityPatch<?> player) {
        Level level = player.getOriginal().level();
        Vec3 center = player.getOriginal().position().add(0, player.getOriginal().getBbHeight() / 2, 0);
        double avatarHeight = 5.0;
        double time = level.getGameTime() * 0.03;

        // 血神轮廓
        int layers = 10;
        for (int l = 0; l < layers; l++) {
            double layerHeight = avatarHeight * l / layers;
            double layerRadius = 1.5 * (1.0 - (double)l / layers);
            int particles = 40;

            for (int i = 0; i < particles; i++) {
                double angle = 2 * Math.PI * i / particles + time;
                double pulse = 0.9 + 0.1 * Math.sin(time * 3 + l);

                double x = center.x + layerRadius * Math.cos(angle) * pulse;
                double z = center.z + layerRadius * Math.sin(angle) * pulse;
                double y = center.y + layerHeight;

                level.addParticle(ParticleType.BLOOD_FIRE_FLAME.get(),
                        x, y, z,
                        0, 0.01, 0);
            }
        }

        // 血神光环
        int rings = 3;
        for (int r = 0; r < rings; r++) {
            double ringHeight = avatarHeight * 0.7 + r * 0.5;
            double ringRadius = 2.5;
            int ringParticles = 60;

            for (int i = 0; i < ringParticles; i++) {
                double angle = 2 * Math.PI * i / ringParticles + time;
                double x = center.x + ringRadius * Math.cos(angle);
                double z = center.z + ringRadius * Math.sin(angle);
                double y = center.y + ringHeight;

                level.addParticle(ParticleTypes.DRIPPING_HONEY,
                        x, y, z,
                        -Math.cos(angle) * 0.1,
                        0,
                        -Math.sin(angle) * 0.1);
            }
        }

        // 血雨效果
        for (int i = 0; i < 100; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2;
            double distance = 8.0 + level.random.nextDouble() * 4.0;
            double x = center.x + distance * Math.cos(angle);
            double z = center.z + distance * Math.sin(angle);
            double y = center.y + 10.0 + level.random.nextDouble() * 5.0;

            level.addParticle(EpicFightParticles.BLOOD.get(),
                    x, y, z,
                    0, -0.5, 0);
        }

        // 地面血浪
        for (int i = 0; i < 200; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2;
            double distance = 5.0 + level.random.nextDouble() * 3.0;
            double x = center.x + distance * Math.cos(angle);
            double z = center.z + distance * Math.sin(angle);

            level.addParticle(EpicFightParticles.BLOOD.get(),
                    x, center.y, z,
                    -Math.cos(angle) * 0.05,
                    0.01,
                    -Math.sin(angle) * 0.05);
        }
    }
    public static void bloodBladeWhirlwind(LivingEntityPatch<?> player) {
        Level level = player.getOriginal().level();
        Vec3 center = player.getOriginal().position().add(0, 0.5, 0);
        int blades = 12;
        double radius = 6.0;
        double height = 3.0;
        double time = level.getGameTime() * 0.1;

        for (int b = 0; b < blades; b++) {
            double bladeAngle = 2 * Math.PI * b / blades + time;
            double bladeHeight = height * (0.3 + 0.7 * Math.abs(Math.sin(time * 2 + b)));

            // 刀刃位置
            double x = center.x + radius * Math.cos(bladeAngle);
            double z = center.z + radius * Math.sin(bladeAngle);
            double y = center.y + bladeHeight;

            // 刀刃粒子
            for (int i = 0; i < 10; i++) {
                double offset = (i - 5) * 0.3;
                double bladeX = x + offset * Math.cos(bladeAngle + Math.PI/2);
                double bladeZ = z + offset * Math.sin(bladeAngle + Math.PI/2);

                level.addParticle(EpicFightParticles.BLOOD.get(),
                        bladeX, y, bladeZ,
                        0, 0, 0);
            }

            // 刀光轨迹
            double prevAngle = bladeAngle - 0.2;
            double prevX = center.x + radius * Math.cos(prevAngle);
            double prevZ = center.z + radius * Math.sin(prevAngle);
            double prevY = center.y + height * (0.3 + 0.7 * Math.abs(Math.sin(time * 2 + b - 0.2)));

            drawBloodLine(level, prevX, prevY, prevZ, x, y, z);
        }

        // 旋风中心
        for (int i = 0; i < 20; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2;
            double distance = level.random.nextDouble() * radius * 0.5;
            double yOffset = level.random.nextDouble() * height;

            double x = center.x + distance * Math.cos(angle);
            double z = center.z + distance * Math.sin(angle);
            double y = center.y + yOffset;

            level.addParticle(ParticleTypes.DRIPPING_HONEY,
                    x, y, z,
                    -Math.cos(angle) * 0.2,
                    0.1,
                    -Math.sin(angle) * 0.2);
        }
    }
    public static class Guhao_Battle_utils {
        public static void blood_explosion(LivingEntityPatch<?> livingEntityPatch) {BattleUtils.spawnBloodExplosionServer(livingEntityPatch);}
        public static void blood_avatar(LivingEntityPatch<?> livingEntityPatch) {BattleUtils.bloodAvatarEffect(livingEntityPatch);}
        public static void blood_tornado(LivingEntityPatch<?> livingEntityPatch) {BattleUtils.bloodTornado(livingEntityPatch);}
        public static void blood_ritual(LivingEntityPatch<?> livingEntityPatch) {BattleUtils.bloodRitualEffect(livingEntityPatch);}
        public static void blood_god_incarnation(LivingEntityPatch<?> livingEntityPatch) {BattleUtils.bloodGodIncarnation(livingEntityPatch);}
        public static void blood_blade_whirl_wind(LivingEntityPatch<?> livingEntityPatch) {BattleUtils.bloodBladeWhirlwind(livingEntityPatch);}
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
                int count = 12;
                float damage = random.nextFloat(8f, 10f);
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
            bloodSlash.setDamage(random.nextFloat(16f,24f));
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
                if ((entityiterator instanceof LivingEntity livingEntity) && (!((entityiterator == livingEntityPatch.getOriginal()) || entityiterator instanceof Player)) && !(entityiterator instanceof Villager)) {
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
                        serverLevel.sendParticles(ParticleType.ONE_JC_BLOOD_JUDGEMENT_LONG.get(), (livingEntity.getX()), (livingEntity.getEyeY()+6), (livingEntity.getZ()), 1, 0, 0,0,0);
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
                    if (!(world.getNearestPlayer(ep.getOriginal(),-1) == null)) entityiterator.hurt(new DamageSource(ep.getOriginal().level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MAGIC), ep.getOriginal()), 10.0f);
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
                    if (!(world.getNearestPlayer(ep.getOriginal(),-1) == null)) entityiterator.hurt(new DamageSource(ep.getOriginal().level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MAGIC), ep.getOriginal()), 10.0f);
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
                    if (!(world.getNearestPlayer(ep.getOriginal(),-1) == null)) entityiterator.hurt(new DamageSource(ep.getOriginal().level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MAGIC), ep.getOriginal()), 10.0f);
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
                    if (!(world.getNearestPlayer(ep.getOriginal(),-1) == null)) entityiterator.hurt(new DamageSource(ep.getOriginal().level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MAGIC), ep.getOriginal()), 10.0f);
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
                        float damage = amplifier+1 * 2f;
                        if (damage > 300) damage = 300f;
                        livingEntity.hurt(new DamageSource(ep.getOriginal().level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MAGIC), ep.getOriginal()), damage);
                        livingEntity.removeEffect(EXSANGUINATION.get());
                        //LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(entityiterator, LivingEntityPatch.class);
                        HurtableEntityPatch<?> hurtableEntityPatch = EpicFightCapabilities.getEntityPatch(entityiterator, HurtableEntityPatch.class);
                        if (hurtableEntityPatch != null) {
                            hurtableEntityPatch.applyStun(StunType.KNOCKDOWN, 4.0F);
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
                        devour.setDamage(20);
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