/* (C)2025 */
package com.dyingday.dronecraft.api.behavior.action;

import com.dyingday.dronecraft.api.context.ActionContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public interface IAction {
  /**
   * Execute this action
   *
   * @param context The execution context
   * @return ActionResult indicating success, failure, or if the action needs more ticks
   */
  ActionResult execute(ActionContext context);

  /** Check if this action can execute in the current context */
  boolean canExecute(ActionContext context);

  /** Get the display name for UI purposes */
  Component getDisplayName();

  /** Get the category this action belongs to */
  ActionCategory getCategory();

  /** Serialize action parameters to NBT */
  CompoundTag serializeNBT();

  /** Deserialize action parameters from NBT */
  void deserializeNBT(CompoundTag nbt);

  /** Create a deep copy of this action */
  IAction copy();
}
