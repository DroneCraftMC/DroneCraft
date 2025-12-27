/* (C)2025 */
package com.dyingday.dronecraft.entity;

import com.dyingday.dronecraft.DroneCraft;
import com.dyingday.dronecraft.api.behavior.Behavior;
import com.dyingday.dronecraft.api.behavior.BehaviorExecutor;
import com.dyingday.dronecraft.api.context.ActionContext;
import com.dyingday.dronecraft.api.context.ResourceLimits;
import com.dyingday.dronecraft.api.context.executor.IExecutorEntity;
import com.dyingday.dronecraft.registration.DCEntities;
import java.util.Optional;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jetbrains.annotations.NotNull;

public class DroneEntity extends PathfinderMob implements IExecutorEntity {
  private final ItemStacksResourceHandler inventory = new ItemStacksResourceHandler(9);
  private final EnergyHandler energy = new SimpleEnergyHandler(10_000, 100, 100);

  private BehaviorExecutor behaviorExecutor;

  public DroneEntity(EntityType<? extends @NotNull DroneEntity> entityType, Level level) {
    super(entityType, level);
  }

  public static void registerCapabilities(RegisterCapabilitiesEvent event) {
    event.registerEntity(
        Capabilities.Item.ENTITY, DCEntities.DRONE.get(), (entity, context) -> entity.inventory);

    event.registerEntity(
        Capabilities.Energy.ENTITY, DCEntities.DRONE.get(), (entity, context) -> entity.energy);
  }

  @Override
  public Identifier getExecutorType() {
    return Identifier.fromNamespaceAndPath(DroneCraft.MODID, "drone");
  }

  @Override
  public Optional<Entity> asEntity() {
    return Optional.of(this);
  }

  @Override
  public void tick() {
    super.tick();

    if (!level().isClientSide() && behaviorExecutor != null) {
      behaviorExecutor.tick();
    }
  }

  public void setBehavior(Behavior behavior) {
    ActionContext context =
        new ActionContext.Builder((Entity) this)
            .trackHistory(true)
            .resourceLimits(new ResourceLimits(32, 8, 5_000))
            .build();
    this.behaviorExecutor = new BehaviorExecutor(behavior, context);
  }
}
