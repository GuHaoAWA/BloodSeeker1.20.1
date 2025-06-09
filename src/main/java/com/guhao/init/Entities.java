package com.guhao.init;


import com.guhao.GuhaoMod;
import com.guhao.entity.ApartEntity;
import com.guhao.entity.ApartEntityPatch;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.api.forgeevent.EntityPatchRegistryEvent;
import yesman.epicfight.api.forgeevent.ModelBuildEvent;
import yesman.epicfight.gameasset.Armatures;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Entities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GuhaoMod.MODID);
//    public static final RegistryObject<EntityType<ApartEntity>> APART = register("apart", EntityType.Builder.<ApartEntity>of(ApartEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(1000).noSave().fireImmune().sized(0.6f, 1.8f));
    public static final RegistryObject<EntityType<ApartEntity>> APART = REGISTRY.register("apart", () -> EntityType.Builder.<ApartEntity>of(ApartEntity::new, MobCategory.MONSTER).fireImmune().sized(0.6F, 1.8F).clientTrackingRange(10).noSave().build("apart"));
    private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
        return REGISTRY.register(registryname, () -> entityTypeBuilder.build(registryname));
    }


    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(APART.get(), ApartEntity.createAttributes().build());
    }
    @SubscribeEvent
    public static void setArmature(ModelBuildEvent.ArmatureBuild event) {
        Armatures.registerEntityTypeArmature(APART.get(), Armatures.BIPED);
    }
    @SubscribeEvent
    public static void setPatch(EntityPatchRegistryEvent event) {
        event.getTypeEntry().put(APART.get(), (entity) -> ApartEntityPatch::new);
    }
}
