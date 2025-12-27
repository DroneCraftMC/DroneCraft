/* (C)2025 */
package com.dyingday.dronecraft.api.context.executor;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public interface IExecutorEntity extends IExecutor {
  @Nullable default <R> R withEntity(Function<Entity, R> with, @Nullable R defaultValue) {
    Optional<Entity> result = asEntity();
    return result.map(with).orElse(defaultValue);
  }

  @Nullable default <R> R withEntity(Function<Entity, R> with) {
    return withEntity(with, null);
  }

  @Override
  default Object getUnderlyingExecutor() {
    return asEntity();
  }

  @Override
  default Level getLevel() {
    return withEntity(Entity::level);
  }

  @Override
  default BlockPos getBlockPos() {
    return withEntity(Entity::blockPosition);
  }

  @Override
  default Vec3 getPosition() {
    return withEntity(Entity::position);
  }

  @Override
  default <T> @Nullable T getCapability(
      BlockCapability<@NotNull T, @Nullable Direction> capability, @Nullable Direction side) {
    return null; // Entities don't have block entity capabilities
  }

  @Override
  default <T> @Nullable T getCapability(
      EntityCapability<@NotNull T, @Nullable Direction> capability, @Nullable Direction side) {
    return withEntity(e -> Objects.requireNonNull(e.getCapability(capability, side)));
  }

  @Override
  default <T> @Nullable T getCapability(ItemCapability<@NotNull T, ?> capability) {
    return null; // Entities don't have item capabilities
  }

  @Override
  default boolean isRemoved() {
    return Boolean.TRUE.equals(withEntity(Entity::isRemoved));
  }

  @Override
  default UUID getExecutorId() {
    return withEntity(Entity::getUUID);
  }

  @Override
  default Direction getFacing() {
    return withEntity(Entity::getDirection);
  }

  @Override
  default Vec3 getLookVector() {
    return withEntity(Entity::getLookAngle);
  }
}
