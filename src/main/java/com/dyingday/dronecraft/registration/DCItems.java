/* (C)2025 */
package com.dyingday.dronecraft.registration;

import com.dyingday.dronecraft.DroneCraft;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class DCItems {
  public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DroneCraft.MODID);

  public static final DeferredHolder<@NotNull Item, @NotNull BlockItem> DRONE_STATION =
      ITEMS.registerSimpleBlockItem(
          "drone_station",
          DCBlocks.DRONE_STATION,
          () -> new Item.Properties().useBlockDescriptionPrefix());
}
