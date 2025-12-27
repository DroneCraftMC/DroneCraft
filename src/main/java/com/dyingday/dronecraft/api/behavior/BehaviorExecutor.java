/* (C)2025 */
package com.dyingday.dronecraft.api.behavior;

import com.dyingday.dronecraft.api.behavior.action.ActionResult;
import com.dyingday.dronecraft.api.behavior.action.IAction;
import com.dyingday.dronecraft.api.context.ActionContext;
import java.util.List;

/**
 * Manages the execution of a behavior's action sequence. The executor handles the sequential
 * execution of actions, state transitions, looping behavior, and execution control
 * (pause/resume/stop). It processes one action per tick and manages the progression through the
 * action list based on action results.
 *
 * <p>The executor maintains its own execution state separate from the behavior itself, allowing
 * multiple executors to run the same behavior independently.
 */
public class BehaviorExecutor {
  /** The behavior being executed */
  private final Behavior behavior;

  /** The context providing world access and state for action execution */
  private final ActionContext context;

  /** Current execution state of this executor */
  private BehaviorExecutorState state;

  /**
   * Create a new behavior executor. The executor starts in the RUNNING state and begins execution
   * on the first tick.
   *
   * @param behavior The behavior to execute
   * @param context The action context providing world access and executor state
   */
  public BehaviorExecutor(Behavior behavior, ActionContext context) {
    this.behavior = behavior;
    this.context = context;
    state = BehaviorExecutorState.RUNNING;
  }

  /**
   * Execute one tick of the behavior. This processes the current action and handles state
   * transitions based on the result. Only executes when in the RUNNING state.
   *
   * <p>Action result handling:
   *
   * <ul>
   *   <li>SUCCESS: Advances to the next action and resets execution state
   *   <li>FAILURE: Currently advances to next action (TODO: implement failure handling)
   *   <li>CONTINUE: Keeps executing the same action on the next tick
   *   <li>BLOCKING: Currently no special handling (TODO: implement blocking states)
   * </ul>
   *
   * <p>When the end of the action list is reached:
   *
   * <ul>
   *   <li>Looping behaviors reset to the beginning and clear persistent data
   *   <li>Non-looping behaviors transition to COMPLETED state
   * </ul>
   */
  public void tick() {
    if (state != BehaviorExecutorState.RUNNING) {
      return;
    }

    List<IAction> actions = behavior.getActions();
    if (actions.isEmpty()) {
      state = BehaviorExecutorState.COMPLETED;
      return;
    }

    int currentIndex = behavior.getCurrentActionIndex();
    if (currentIndex >= actions.size()) {
      if (behavior.isLooping()) {
        behavior.reset();
        context.clearPersistentData();
        currentIndex = 0;
      } else {
        state = BehaviorExecutorState.COMPLETED;
        return;
      }
    }

    IAction currentAction = actions.get(currentIndex);

    // Execute current action
    ActionResult result = currentAction.execute(context);
    context.tick();

    // Handle result
    switch (result) {
      case SUCCESS -> {
        // Move to next action
        behavior.setCurrentActionIndex(currentIndex + 1);
        context.resetExecutionState();
      }
      case FAILURE -> {
        // TODO handle failure states
        behavior.setCurrentActionIndex(currentIndex + 1);
        context.resetExecutionState();
      }
      case CONTINUE -> {
        // Keep executing the same action until next tick
      }
      case BLOCKING -> {
        // Todo handle blocking states

      }
    }
  }

  /**
   * Get the current execution state of this executor.
   *
   * @return The current state (IDLE, RUNNING, COMPLETED, FAILED, or PAUSED)
   */
  public BehaviorExecutorState getState() {
    return state;
  }

  /**
   * Pause execution of the behavior. Execution can be resumed by calling {@link #resume()}. The
   * current action position and context state are preserved.
   */
  public void pause() {
    state = BehaviorExecutorState.PAUSED;
  }

  /**
   * Resume execution of a paused behavior. Only has an effect if the executor is currently in the
   * PAUSED state. Execution will continue from the same action and context state where it was
   * paused.
   */
  public void resume() {
    if (state == BehaviorExecutorState.PAUSED) {
      state = BehaviorExecutorState.RUNNING;
    }
  }

  /**
   * Stop execution and mark the behavior as failed. This transitions to the FAILED state and
   * prevents further execution. Use {@link #reset()} to restart execution from the beginning.
   */
  public void stop() {
    state = BehaviorExecutorState.FAILED;
  }

  /**
   * Reset the executor to start execution from the beginning. This resets the behavior's action
   * index, clears persistent data, and transitions back to the RUNNING state.
   */
  public void reset() {
    behavior.reset();
    context.clearPersistentData();
    state = BehaviorExecutorState.RUNNING;
  }

  /** Represents the execution state of a behavior executor. */
  public enum BehaviorExecutorState {
    /** Executor is idle and not processing actions */
    IDLE,
    /** Executor is actively processing actions each tick */
    RUNNING,
    /** All actions have completed successfully */
    COMPLETED,
    /** Execution has failed or been stopped */
    FAILED,
    /** Execution is paused and can be resumed */
    PAUSED
  }
}
