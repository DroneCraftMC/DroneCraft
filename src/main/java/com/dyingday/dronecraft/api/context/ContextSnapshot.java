/* (C)2025 */
package com.dyingday.dronecraft.api.context;

import com.dyingday.dronecraft.api.behavior.action.ActionExecutionRecord;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

/**
 * Snapshot of context state for debugging
 *
 * @param executionId UUID of execution
 * @param position BlockPos of entity
 * @param ticksExecuting How long in ticks it has been executing for
 * @param variables Map of variable name to variable objects
 * @param persistentData Data persisted across action executions
 * @param localData Data for action being run
 * @param executionHistory Nullable history of executed actions
 */
public record ContextSnapshot(
    UUID executionId,
    BlockPos position,
    int ticksExecuting,
    Map<String, Object> variables,
    Map<String, Object> persistentData,
    Map<String, Object> localData,
    @Nullable List<ActionExecutionRecord> executionHistory) {
  public Map<String, Object> variables() {
    return Collections.unmodifiableMap(variables);
  }

  public Map<String, Object> persistentData() {
    return Collections.unmodifiableMap(persistentData);
  }

  public Map<String, Object> localData() {
    return Collections.unmodifiableMap(localData);
  }

  @Nullable public List<ActionExecutionRecord> executionHistory() {
    return executionHistory == null ? null : Collections.unmodifiableList(executionHistory);
  }
}
