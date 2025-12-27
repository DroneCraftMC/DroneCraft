/* (C)2025 */
package com.dyingday.dronecraft.api.context.executor;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

/** Common interface for anything that can execute behaviors (entities, blocks, etc.) */
public interface IExecutor {
  /** Get the level this executor exists in */
  Level getLevel();

  /** Get the current position of this executor */
  BlockPos getBlockPos();

  /** Get the precise position (for entities that aren't block-aligned) */
  Vec3 getPosition();

  /** Check if this executor has been removed from the world */
  boolean isRemoved();

  /** Get unique identifier fro this executor */
  UUID getExecutorId();

  /** Get the executor type */
  Identifier getExecutorType();

  /** Get rotation/facing */
  Direction getFacing();

  /** Get look vector for raycasting, targeting, etc. */
  Vec3 getLookVector();

  /** Get the underlying object Use sparingly - prefer using the interface methods */
  Object getUnderlyingExecutor();

  /** Get a capability from this executor */
  @Nullable <T> T getCapability(
      BlockCapability<@NotNull T, @Nullable Direction> capability, @Nullable Direction side);

  @Nullable <T> T getCapability(EntityCapability<@NotNull T, Void> capability);

  @Nullable <T> T getCapability(
      EntityCapability<@NotNull T, @Nullable Direction> capability, @Nullable Direction side);

  @Nullable <T> T getCapability(ItemCapability<@NotNull T, ?> capability);

  /** Check if this is a block-based executor */
  default boolean isBlockBased() {
    return getUnderlyingExecutor() instanceof BlockEntity;
  }

  /** Check if this is an entity-based executor */
  default boolean isEntityBased() {
    return getUnderlyingExecutor() instanceof Entity;
  }

  /** Check if this is an item-based executor */
  default boolean isItemBased() {
    return getUnderlyingExecutor() instanceof ItemEntity;
  }

  /** Get as BlockEntity (if applicable) */
  default Optional<BlockEntity> asBlockEntity() {
    return isBlockBased() ? Optional.of((BlockEntity) getUnderlyingExecutor()) : Optional.empty();
  }

  /** Get as Entity (if applicable) */
  default Optional<Entity> asEntity() {
    return isEntityBased() ? Optional.of((Entity) getUnderlyingExecutor()) : Optional.empty();
  }

  /** Get as ItemEntity (if applicable) */
  default Optional<ItemEntity> asItemEntity() {
    return isItemBased() ? Optional.of((ItemEntity) getUnderlyingExecutor()) : Optional.empty();
  }
}
