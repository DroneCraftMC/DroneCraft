/* (C)2025 */
package com.dyingday.dronecraft.api.behavior.action;

import net.minecraft.resources.Identifier;

/**
 * A record of an executed action
 *
 * @param actionId Id of the recorded action
 * @param result Result of the action
 * @param executionTimeNanos Time to execute in Nano
 * @param tickNumber Which tick number this action was executed in
 * @param timestamp Timestamp of action being executed
 */
public record ActionExecutionRecord(
    Identifier actionId,
    ActionResult result,
    long executionTimeNanos,
    int tickNumber,
    long timestamp) {
  /**
   * Get the executionTimeNanos in millis
   *
   * @return executionTimeNanos in millis
   */
  public double executionTimeMs() {
    return executionTimeNanos / 1_000_000.0;
  }
}
