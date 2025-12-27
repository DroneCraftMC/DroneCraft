/* (C)2025 */
package com.dyingday.dronecraft.registration;

import com.dyingday.dronecraft.DroneCraft;
import com.dyingday.dronecraft.entity.DroneEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class DCEntities {
  public static final DeferredRegister.Entities ENTITIES =
      DeferredRegister.createEntities(DroneCraft.MODID);

  public static final DeferredHolder<
          @NotNull EntityType<?>, @NotNull EntityType<@NotNull DroneEntity>>
      DRONE =
          ENTITIES.register(
              "drone",
              () ->
                  EntityType.Builder.of(DroneEntity::new, MobCategory.MISC)
                      .build(
                          ResourceKey.create(
                              Registries.ENTITY_TYPE,
                              Identifier.fromNamespaceAndPath(DroneCraft.MODID, "entity"))));
}
