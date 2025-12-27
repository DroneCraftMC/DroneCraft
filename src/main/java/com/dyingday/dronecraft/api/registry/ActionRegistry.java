/* (C)2025 */
package com.dyingday.dronecraft.api.registry;

import com.dyingday.dronecraft.api.behavior.action.ActionCategory;
import com.dyingday.dronecraft.api.behavior.action.IAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.Identifier;

/**
 * Central registry for all action types in the DroneCraft behavior system. This registry maps
 * resource identifiers to action factories, enabling dynamic action creation and discovery. Actions
 * must be registered during mod initialization before they can be used in behavior trees or the
 * visual programming interface.
 *
 * <p>Thread-safety: This registry is not thread-safe and should only be accessed during
 * single-threaded initialization or synchronized contexts.
 */
public class ActionRegistry {
  /** Map of action identifiers to their factory instances */
  private static final Map<Identifier, ActionFactory<?>> ACTIONS = new HashMap<>();

  /**
   * Register an action factory with a unique identifier. This should be called during mod
   * initialization to make actions available to the behavior system and visual programming
   * interface.
   *
   * @param <A> The type of action being registered
   * @param id The unique identifier for this action (e.g., "dronecraft:move_forward")
   * @param factory The factory that creates instances of this action
   * @throws IllegalStateException if an action with this ID is already registered
   */
  public static <A extends IAction> void registerAction(Identifier id, ActionFactory<A> factory) {
    if (ACTIONS.containsKey(id)) {
      throw new IllegalStateException("Action already registered: " + id);
    }
    ACTIONS.put(id, factory);
  }

  /**
   * Create a new action instance from a registered factory. The returned action will have default
   * parameter values as defined by its factory.
   *
   * @param id The identifier of the action to create
   * @return A new action instance with default configuration
   * @throws IllegalStateException if no action is registered with the given ID
   */
  public static IAction createAction(Identifier id) {
    ActionFactory<?> factory = ACTIONS.get(id);
    if (factory == null) {
      throw new IllegalStateException("No action registered for: " + id);
    }
    return factory.create();
  }

  /**
   * Get all registered action identifiers for a specific category. This is useful for populating UI
   * menus or filtering available actions by their functional category (movement, inventory, combat,
   * etc.).
   *
   * @param category The action category to filter by
   * @return List of action identifiers in the specified category
   */
  public static List<Identifier> getActionsForCategory(ActionCategory category) {
    return ACTIONS.entrySet().stream()
        .filter(e -> e.getValue().getCategory() == category)
        .map(Map.Entry::getKey)
        .toList();
  }

  /**
   * Check if an action with the given identifier is registered.
   *
   * @param id The action identifier to check
   * @return True if an action is registered with this ID, false otherwise
   */
  public static boolean isRegistered(Identifier id) {
    return ACTIONS.containsKey(id);
  }
}
