/* (C)2025 */
package com.dyingday.dronecraft;

import com.dyingday.dronecraft.blockentity.DroneStationBE;
import com.dyingday.dronecraft.registration.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@Mod(DroneCraft.MODID)
public class DroneCraft {
  public static final String MODID = "dronecraft";

  public DroneCraft(IEventBus modBus) {
    DCBlocks.BLOCKS.register(modBus);
    DCBlockEntities.BLOCK_ENTITIES.register(modBus);
    DCItems.ITEMS.register(modBus);
    DCEntities.ENTITIES.register(modBus);
    DCCreativeTab.CREATIVE_MOD_TABS.register(modBus);

    modBus.addListener(this::registerCapabilities);
  }

  private void registerCapabilities(RegisterCapabilitiesEvent event) {
    DroneStationBE.registerCapabilities(event);
  }
}
