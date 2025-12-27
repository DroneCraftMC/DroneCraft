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
import net.minecraft.world.level.block.state.BlockState;

public class FindTreeAction implements IAction {
  private int searchRadius = 16;
  private BlockPos foundTree = null;

  @Override
  public ActionResult execute(ActionContext context) {
    Level level = context.getLevel();
    BlockPos center = context.getExecutorBlockPos();

    // Search for logs in radius
    for (int x = -searchRadius; x <= searchRadius; x++) {
      for (int y = -searchRadius; y <= searchRadius; y++) {
        for (int z = -searchRadius; z <= searchRadius; z++) {
          BlockPos checkPos = center.offset(x, y, z);
          BlockState state = level.getBlockState(checkPos);

          // Check if it's a log
          if (state.is(BlockTags.LOGS)) {
            foundTree = checkPos;
            context.setVariable("target_tree", checkPos);
            return ActionResult.SUCCESS;
          }
        }
      }
    }

    // No tree found
    return ActionResult.FAILURE;
  }

  @Override
  public boolean canExecute(ActionContext context) {
    return true;
  }

  @Override
  public Component getDisplayName() {
    return Component.literal("Find Tree");
  }

  @Override
  public ActionCategory getCategory() {
    return ActionCategory.UTILITY;
  }

  @Override
  public CompoundTag serializeNBT() {
    CompoundTag tag = new CompoundTag();
    tag.putString("action_type", "find_tree");
    tag.putInt("search_radius", searchRadius);
    if (foundTree != null) {
      tag.putLong("found_tree", foundTree.asLong());
    }
    return tag;
  }

  @Override
  public void deserializeNBT(CompoundTag tag) {
    searchRadius = tag.getInt("search_radius").orElse(16);
    if (tag.getLong("found_tree").isPresent()) {
      foundTree = BlockPos.of(tag.getLong("found_tree").get());
    }
  }

  @Override
  public IAction copy() {
    FindTreeAction copy = new FindTreeAction();
    copy.searchRadius = this.searchRadius;
    return copy;
  }
}
