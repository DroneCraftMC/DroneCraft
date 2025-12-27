/* (C)2025 */
package com.dyingday.dronecraft.behavior.action;

import com.dyingday.dronecraft.api.behavior.action.ActionCategory;
import com.dyingday.dronecraft.api.behavior.action.ActionResult;
import com.dyingday.dronecraft.api.behavior.action.IAction;
import com.dyingday.dronecraft.api.context.ActionContext;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class HarvestTreeAction implements IAction {
  private final Set<BlockPos> logsToBreak = new HashSet<>();
  private final Set<BlockPos> leavesToBreak = new HashSet<>();
  private BlockPos targetTree;
  private boolean initialized = false;

  @Override
  public ActionResult execute(ActionContext context) {
    // Get target from variables
    if (targetTree == null) {
      targetTree = context.getVariable("target_tree", BlockPos.class).orElse(null);
      if (targetTree == null) {
        return ActionResult.FAILURE;
      }
    }

    // Check there is space for logs / drops
    // TODO

    // Initialize - find all logs and leaves
    if (!initialized) {
      findAllTreeBlocks(context);
      initialized = true;
    }

    Level level = context.getLevel();

    // Break logs first
    if (!logsToBreak.isEmpty()) {
      BlockPos logPos = logsToBreak.iterator().next();
      logsToBreak.remove(logPos);

      BlockState state = level.getBlockState(logPos);
      if (state.is(BlockTags.LOGS)) {
        // Todo: Store drops in inventory
        level.destroyBlock(logPos, false);
      }
      context.tick();
      return ActionResult.CONTINUE;
    }

    // Done
    return ActionResult.SUCCESS;
  }

  private void findAllTreeBlocks(ActionContext context) {
    Level level = context.getLevel();
    Queue<BlockPos> toCheck = new LinkedList<>();
    Set<BlockPos> checked = new HashSet<>();

    toCheck.add(targetTree);

    // Flood fill to find all connected logs
    while (!toCheck.isEmpty()) {
      BlockPos pos = toCheck.poll();
      if (checked.contains(pos)) {
        continue;
      }
      checked.add(pos);

      BlockState state = level.getBlockState(pos);

      if (state.is(BlockTags.LOGS)) {
        logsToBreak.add(pos);

        // Check all 6 adjacent blocks for more logs
        for (Direction dir : Direction.values()) {
          BlockPos neighbor = pos.relative(dir);
          if (!checked.contains(neighbor)) {
            toCheck.add(neighbor);
          }
        }
      }
    }
  }

  @Override
  public boolean canExecute(ActionContext context) {
    return context.hasVariable("target_tree");
  }

  @Override
  public Component getDisplayName() {
    return Component.literal("Harvest Tree");
  }

  @Override
  public ActionCategory getCategory() {
    return ActionCategory.INTERACTION;
  }

  @Override
  public CompoundTag serializeNBT() {
    CompoundTag tag = new CompoundTag();
    tag.putString("action_type", "harvest_tree");
    if (targetTree != null) {
      tag.putLong("target_tree", targetTree.asLong());
    }
    tag.putBoolean("initialized", initialized);
    return tag;
  }

  @Override
  public void deserializeNBT(CompoundTag tag) {
    if (tag.getLong("target_tree").isPresent()) {
      targetTree = BlockPos.of(tag.getLong("target_tree").get());
    }
    initialized = tag.getBoolean("initialized").orElse(false);
  }

  @Override
  public IAction copy() {
    return new HarvestTreeAction();
  }
}
