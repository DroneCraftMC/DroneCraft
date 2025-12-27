/* (C)2025 */
package com.dyingday.dronecraft.api.behavior;

/** Result of a behavior */
public enum BehaviorResult {
  /** Indicates a behavior needs to continue into the next tick */
  RUNNING,
  /** Indicates a behavior has completed successfully */
  SUCCESS,
  /** Indicates a behavior has failed to complete */
  FAILURE
}
