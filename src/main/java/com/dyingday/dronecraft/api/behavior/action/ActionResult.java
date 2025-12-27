/* (C)2025 */
package com.dyingday.dronecraft.api.behavior.action;

/** Result of an action */
public enum ActionResult {
  /** Indicates an action has completed successfully */
  SUCCESS,
  /** Indicates an action failed to complete */
  FAILURE,
  /** Indicates an action needs to continue in the next tick */
  CONTINUE,
  /** Indicates something is blocking this action from being able to run */
  BLOCKING
}
