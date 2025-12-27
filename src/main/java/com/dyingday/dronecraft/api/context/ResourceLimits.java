/* (C)2025 */
package com.dyingday.dronecraft.api.context;

public record ResourceLimits(
    int maxItemMovesPerTick, int maxBlockModificationsPerTick, int maxEnergyPerTick) {
  public static final ResourceLimits DEFAULT = new ResourceLimits(64, 16, 10_000);
  public static final ResourceLimits UNLIMITED =
      new ResourceLimits(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
}
