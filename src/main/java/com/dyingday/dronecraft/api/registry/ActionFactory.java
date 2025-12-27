/* (C)2025 */
package com.dyingday.dronecraft.api.registry;

import com.dyingday.dronecraft.api.behavior.action.ActionCategory;
import com.dyingday.dronecraft.api.behavior.action.ActionParameter;
import com.dyingday.dronecraft.api.behavior.action.IAction;
import java.util.List;
import net.minecraft.network.chat.Component;

/**
 * Factory interface for creating and describing action instances. Action factories are registered
 * with the action registry and provide metadata about actions including their category, display
 * name, and configurable parameters. This enables the visual programming system to present actions
 * with proper UI elements.
 *
 * @param <A> The type of action this factory creates
 */
public interface ActionFactory<A extends IAction> {
  /**
   * Create a new instance of this action with default configuration. Each call should return a
   * fresh instance with default parameter values.
   *
   * @return A new action instance
   */
  A create();

  /**
   * Get the category this action belongs to for organization in the UI. Categories group related
   * actions together in the visual programming interface.
   *
   * @return The action category
   */
  ActionCategory getCategory();

  /**
   * Get the display name shown to users in the visual programming interface. This should be a
   * user-friendly, translatable name.
   *
   * @return The localized display name component
   */
  Component getDisplayName();

  /**
   * Get the list of configurable parameters for this action. Parameters define what inputs the
   * action accepts and how they should be presented in the visual programming interface (text
   * fields, dropdowns, etc.).
   *
   * @return List of action parameters, empty list if the action has no parameters
   */
  List<ActionParameter> getParameters();
}
