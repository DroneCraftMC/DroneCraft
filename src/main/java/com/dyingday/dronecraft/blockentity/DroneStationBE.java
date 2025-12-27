/* (C)2025 */
package com.dyingday.dronecraft.blockentity;

import com.dyingday.dronecraft.DroneCraft;
import com.dyingday.dronecraft.api.behavior.Behavior;
import com.dyingday.dronecraft.api.behavior.BehaviorExecutor;
import com.dyingday.dronecraft.api.behavior.action.IAction;
import com.dyingday.dronecraft.api.context.ActionContext;
import com.dyingday.dronecraft.api.context.ResourceLimits;
import com.dyingday.dronecraft.api.context.executor.IExecutorBlockEntity;
import com.dyingday.dronecraft.behavior.action.FindTreeAction;
import com.dyingday.dronecraft.behavior.action.HarvestTreeAction;
import com.dyingday.dronecraft.behavior.action.PlantSaplingAction;
import com.dyingday.dronecraft.behavior.action.WaitAction;
import com.dyingday.dronecraft.registration.DCBlockEntities;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class DroneStationBE extends BlockEntity implements IExecutorBlockEntity {
  private final ItemStacksResourceHandler inventory = new ItemStacksResourceHandler(27);
  private final SimpleEnergyHandler energy = new SimpleEnergyHandler(10_000, 1_000);

  private BehaviorExecutor behaviorExecutor;

  public DroneStationBE(BlockPos pos, BlockState state) {
    super(DCBlockEntities.DRONE_STATION.get(), pos, state);
  }

  public static void registerCapabilities(RegisterCapabilitiesEvent event) {
    event.registerBlockEntity(
        Capabilities.Item.BLOCK, DCBlockEntities.DRONE_STATION.get(), (be, side) -> be.inventory);

    event.registerBlockEntity(
        Capabilities.Energy.BLOCK, DCBlockEntities.DRONE_STATION.get(), (be, side) -> be.energy);
  }

  @Override
  public Optional<BlockEntity> asBlockEntity() {
    return Optional.of(this);
  }

  @Override
  public Vec3 getPosition() {
    return worldPosition.getCenter();
  }

  @Override
  public Identifier getExecutorType() {
    return Identifier.fromNamespaceAndPath(DroneCraft.MODID, "drone_station");
  }

  @Override
  public DroneStationBE getUnderlyingExecutor() {
    return this;
  }

  @Override
  public <T> @Nullable T getCapability(EntityCapability<@NotNull T, Void> capability) {
    return null;
  }

  public void onPlaced() {
    if (level != null && !level.isClientSide()) {
      startBehavior();
    }
  }

  private void startBehavior() {
    List<IAction> actions = new ArrayList<>();
    actions.add(new FindTreeAction());
    actions.add(new HarvestTreeAction());
    actions.add(new PlantSaplingAction());
    actions.add(new WaitAction(100));

    Behavior treeFarmBehavior =
        new Behavior(Identifier.fromNamespaceAndPath(DroneCraft.MODID, "tree_farm"), actions, true);

    ActionContext context =
        new ActionContext.Builder((BlockEntity) this)
            .trackHistory(false)
            .maxTicksPerAction(1000)
            .resourceLimits(ResourceLimits.UNLIMITED)
            .build();
    this.behaviorExecutor = new BehaviorExecutor(treeFarmBehavior, context);
    setChanged();
  }

  public void tick(Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide()) {
      return;
    }

    if (behaviorExecutor == null) {
      startBehavior();
    }

    if (behaviorExecutor != null) {
      behaviorExecutor.tick();

      if (behaviorExecutor.getState() == BehaviorExecutor.BehaviorExecutorState.COMPLETED) {
        startBehavior();
      }
    }
  }

  public void setBehavior(Behavior behavior) {
    ActionContext context =
        new ActionContext.Builder((BlockEntity) this).trackHistory(true).build();
    this.behaviorExecutor = new BehaviorExecutor(behavior, context);
  }
}
