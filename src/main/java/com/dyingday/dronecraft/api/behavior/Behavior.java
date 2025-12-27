/* (C)2025 */
package com.dyingday.dronecraft.api.behavior;

import com.dyingday.dronecraft.api.behavior.action.IAction;
import java.util.Collections;
import java.util.List;
import net.minecraft.resources.Identifier;

/**
 * Represents a sequence of actions that can be executed by a drone or executor. A behavior is an
 * ordered list of actions that execute sequentially, with optional looping behavior. The behavior
 * tracks the current execution position and can be reset to start from the beginning.
 *
 * <p>Behaviors are typically created through the visual programming interface and stored/loaded via
 * codecs for persistence.
 */
public class Behavior {
  /** Unique identifier for this behavior */
  private final Identifier id;

  /** Ordered list of actions to execute */
  private final List<IAction> actions;

  /** Whether this behavior should loop back to the start when complete */
  private final boolean looping;

  /** Current position in the action sequence (0-indexed) */
  private int currentActionIndex = 0;

  /**
   * Create a new behavior with the specified actions.
   *
   * @param id Unique identifier for this behavior
   * @param actions Ordered list of actions to execute sequentially
   * @param looping Whether the behavior should loop
   */
  public Behavior(Identifier id, List<IAction> actions, boolean looping) {
    this.id = id;
    this.actions = actions;
    this.looping = looping;
  }

  /**
   * Get the unique identifier for this behavior.
   *
   * @return The behavior's identifier
   */
  public Identifier getId() {
    return id;
  }

  /**
   * Get the ordered list of actions in this behavior.
   *
   * @return Unmodifiable view of the action list
   */
  public List<IAction> getActions() {
    return Collections.unmodifiableList(actions);
  }

  /**
   * Check if this behavior should loop back to the start after completing all actions.
   *
   * @return True if the behavior loops, false if it stops after the last action
   */
  public boolean isLooping() {
    return looping;
  }

  /**
   * Get the current execution position in the action sequence.
   *
   * @return The 0-indexed position of the action currently being executed
   */
  public int getCurrentActionIndex() {
    return currentActionIndex;
  }

  /**
   * Set the current execution position in the action sequence. This allows jumping to specific
   * actions or resuming from a saved state.
   *
   * @param currentActionIndex The 0-indexed position to set (should be within bounds of action
   *     list)
   */
  public void setCurrentActionIndex(int currentActionIndex) {
    this.currentActionIndex = currentActionIndex;
  }

  /**
   * Reset the behavior to start execution from the beginning. This sets the current action index
   * back to 0.
   */
  public void reset() {
    currentActionIndex = 0;
  }
}
