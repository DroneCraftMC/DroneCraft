/* (C)2025 */
package com.dyingday.dronecraft.behavior.action;

import com.dyingday.dronecraft.api.behavior.action.ActionCategory;
import com.dyingday.dronecraft.api.behavior.action.ActionResult;
import com.dyingday.dronecraft.api.behavior.action.IAction;
import com.dyingday.dronecraft.api.context.ActionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PlantSaplingAction implements IAction {
  private BlockPos targetTree;
  private BlockPos plantPosition;

  @Override
  public ActionResult execute(ActionContext context) {
    Level level = context.getLevel();

    // Get the original tree position
    if (targetTree == null) {
      targetTree = context.getVariable("target_tree", BlockPos.class).orElse(null);
      if (targetTree == null) {
        return ActionResult.FAILURE;
      }
    }

    // Find a suitable planting position
    if (plantPosition == null) {
      plantPosition = findPlantPosition(level, targetTree);
      if (plantPosition == null) {
        return ActionResult.FAILURE;
      }
    }

    // Check if position is suitable
    BlockState groundState = level.getBlockState(plantPosition.below());
    BlockState airState = level.getBlockState(plantPosition);

    if (!airState.isAir()) {
      return ActionResult.FAILURE;
    }

    if (!groundState.is(BlockTags.DIRT)) {
      return ActionResult.FAILURE;
    }

    // Plant oak sapling
    level.setBlock(plantPosition, Blocks.OAK_SAPLING.defaultBlockState(), 3);

    return ActionResult.SUCCESS;
  }

  private BlockPos findPlantPosition(Level level, BlockPos treePos) {
    // Move down to find the base
    BlockPos checkPos = treePos;
    while (level.isInsideBuildHeight(checkPos.getY())) {
      BlockState below = level.getBlockState(checkPos.below());
      if (below.is(BlockTags.LOGS)) {
        checkPos = checkPos.below();
      } else {
        return checkPos;
      }
    }
    return null;
  }

  @Override
  public boolean canExecute(ActionContext context) {
    return context.hasVariable("target_tree");
  }

  @Override
  public Component getDisplayName() {
    return Component.literal("Plant Sapling");
  }

  @Override
  public ActionCategory getCategory() {
    return ActionCategory.INTERACTION;
  }

  @Override
  public CompoundTag serializeNBT() {
    CompoundTag tag = new CompoundTag();
    tag.putString("action_type", "plant_sapling");
    if (targetTree != null) {
      tag.putLong("target_tree", targetTree.asLong());
    }
    if (plantPosition != null) {
      tag.putLong("plant_position", plantPosition.asLong());
    }
    return tag;
  }

  @Override
  public void deserializeNBT(CompoundTag tag) {
    if (tag.getLong("target_tree").isPresent()) {
      targetTree = BlockPos.of(tag.getLong("target_tree").get());
    }
    if (tag.getLong("plant_position").isPresent()) {
      plantPosition = BlockPos.of(tag.getLong("plant_position").get());
    }
  }

  @Override
  public IAction copy() {
    return new PlantSaplingAction();
  }
}
