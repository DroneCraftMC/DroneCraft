/* (C)2025 */
package com.dyingday.dronecraft.registration;

import com.dyingday.dronecraft.DroneCraft;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class DCCreativeTab {
  public static final DeferredRegister<@NotNull CreativeModeTab> CREATIVE_MOD_TABS =
      DeferredRegister.create(
          Identifier.fromNamespaceAndPath(DroneCraft.MODID, DroneCraft.MODID), DroneCraft.MODID);

  public static final Supplier<CreativeModeTab> CREATIVE_TAB =
      CREATIVE_MOD_TABS.register(
          DroneCraft.MODID,
          () ->
              CreativeModeTab.builder()
                  .title(Component.translatable("itemGroup." + DroneCraft.MODID + ".title"))
                  .icon(() -> new ItemStack(DCItems.DRONE_STATION.get()))
                  .displayItems(
                      (params, output) -> {
                        output.accept(DCItems.DRONE_STATION.get());
                      })
                  .build());
}
