/* (C)2025 */
package com.dyingday.dronecraft.registration;

import com.dyingday.dronecraft.DroneCraft;
import com.dyingday.dronecraft.block.DroneStationBlock;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class DCBlocks {
  public static final DeferredRegister.Blocks BLOCKS =
      DeferredRegister.createBlocks(DroneCraft.MODID);

  public static final DeferredHolder<@NotNull Block, @NotNull DroneStationBlock> DRONE_STATION =
      BLOCKS.registerBlock("drone_station", DroneStationBlock::new);
}
