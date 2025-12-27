/* (C)2025 */
package com.dyingday.dronecraft.api.context.executor;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public interface IExecutorBlockEntity extends IExecutor {
  @Nullable default <R> R withBlockEntity(Function<BlockEntity, R> with, @Nullable R defaultValue) {
    Optional<BlockEntity> result = asBlockEntity();
    return result.map(with).orElse(defaultValue);
  }

  @Nullable default <R> R withBlockEntity(Function<BlockEntity, R> with) {
    return withBlockEntity(with, null);
  }

  @Override
  default <T> @Nullable T getCapability(
      BlockCapability<@NotNull T, @Nullable Direction> capability, @Nullable Direction side) {
    Level level = getLevel();
    if (level == null) {
      return null;
    }
    return level.getCapability(capability, getBlockPos(), side);
  }

  @Override
  default <T> @Nullable T getCapability(
      EntityCapability<@NotNull T, @Nullable Direction> capability, @Nullable Direction side) {
    return null; // Block entities don't have entity capabilities
  }

  @Override
  default <T> @Nullable T getCapability(ItemCapability<@NotNull T, ?> capability) {
    return null; // Block entities don't have item capabilities
  }

  @Override
  default boolean isRemoved() {
    return Boolean.TRUE.equals(withBlockEntity(BlockEntity::isRemoved, false));
  }

  @Override
  default UUID getExecutorId() {
    // Store UUID in BlockEntity's persistent data
    CompoundTag tag = withBlockEntity(BlockEntity::getPersistentData);
    if (tag == null) {
      return null;
    }
    Optional<String> uuidStr = tag.getString("ExecutorId");
    if (uuidStr.isEmpty()) {
      UUID uuid = UUID.randomUUID();
      tag.putString("ExecutorId", uuid.toString());
      return uuid;
    }
    return UUID.fromString(uuidStr.get());
  }

  @Override
  default Direction getFacing() {
    BlockState state = withBlockEntity(BlockEntity::getBlockState);
    if (state != null) {
      if (state.hasProperty(BlockStateProperties.FACING)) {
        return state.getValue(BlockStateProperties.FACING);
      } else if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
        return state.getValue(BlockStateProperties.HORIZONTAL_FACING);
      }
    }
    return Direction.NORTH;
  }

  @Override
  default Vec3 getLookVector() {
    return Vec3.atLowerCornerOf(getFacing().getUnitVec3i());
  }
}
