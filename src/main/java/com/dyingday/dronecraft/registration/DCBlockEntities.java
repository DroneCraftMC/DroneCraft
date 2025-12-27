/* (C)2025 */
package com.dyingday.dronecraft.registration;

import com.dyingday.dronecraft.DroneCraft;
import com.dyingday.dronecraft.blockentity.DroneStationBE;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

/** Registry class for */
public class DCBlockEntities {
  public static final DeferredRegister<@NotNull BlockEntityType<?>> BLOCK_ENTITIES =
      DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, DroneCraft.MODID);

  public static final DeferredHolder<
          @NotNull BlockEntityType<?>, @NotNull BlockEntityType<@NotNull DroneStationBE>>
      DRONE_STATION =
          BLOCK_ENTITIES.register(
              "drone_station",
              () -> new BlockEntityType<>(DroneStationBE::new, DCBlocks.DRONE_STATION.get()));
}
