/* (C)2025 */
package com.dyingday.dronecraft.behavior.action;

import com.dyingday.dronecraft.api.behavior.action.ActionCategory;
import com.dyingday.dronecraft.api.behavior.action.ActionResult;
import com.dyingday.dronecraft.api.behavior.action.IAction;
import com.dyingday.dronecraft.api.context.ActionContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class WaitAction implements IAction {
  private int ticksToWait = 20;
  private int ticksWaited = 0;

  public WaitAction() {}

  public WaitAction(int ticksToWait) {
    this.ticksToWait = ticksToWait;
  }

  @Override
  public ActionResult execute(ActionContext context) {
    ticksWaited++;

    if (ticksWaited >= ticksToWait) {
      ticksWaited = 0;
      return ActionResult.SUCCESS;
    }

    return ActionResult.CONTINUE;
  }

  @Override
  public boolean canExecute(ActionContext context) {
    return true;
  }

  @Override
  public Component getDisplayName() {
    return Component.literal("Wait " + ticksToWait + " ticks");
  }

  @Override
  public ActionCategory getCategory() {
    return ActionCategory.LOGIC;
  }

  @Override
  public CompoundTag serializeNBT() {
    CompoundTag tag = new CompoundTag();
    tag.putString("action_type", "wait");
    tag.putInt("ticks_to_wait", ticksToWait);
    tag.putInt("ticks_waited", ticksWaited);
    return tag;
  }

  @Override
  public void deserializeNBT(CompoundTag tag) {
    ticksToWait = tag.getInt("ticks_to_wait").orElse(0);
    ticksWaited = tag.getInt("ticks_waited").orElse(0);
  }

  @Override
  public IAction copy() {
    return new WaitAction(ticksToWait);
  }
}
