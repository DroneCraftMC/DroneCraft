/* (C)2025 */
package com.dyingday.dronecraft.api.context;

import net.neoforged.neoforge.transfer.transaction.Transaction;

class ResourceManager {
  private final ResourceLimits limits;
  private int itemsMoved;
  private int blocksModified;
  private int energyConsumed;

  public ResourceManager(ResourceLimits limits) {
    this.limits = limits;
  }

  public boolean consumeEnergy(int amount, ActionContext context) {
    if (energyConsumed + amount > limits.maxEnergyPerTick()) {
      return false;
    }

    Transaction transaction = Transaction.openRoot();
    int consumed = context.getEnergyHandler().extract(amount, transaction);

    if (consumed > 0) {
      energyConsumed += consumed;
    }
    transaction.commit();
    return consumed > 0;
  }

  public boolean hasEnergy(int amount, ActionContext context) {
    Transaction transaction = Transaction.openRoot();
    int toExtract = context.getEnergyHandler().extract(amount, transaction);
    transaction.close();
    return toExtract == amount;
  }

  public boolean canMoveItems(int count) {
    return itemsMoved + count <= limits.maxItemMovesPerTick();
  }

  public void trackItemsMoved(int count) {
    itemsMoved += count;
  }

  public boolean canModifyBlocks(int count) {
    return blocksModified + count <= limits.maxBlockModificationsPerTick();
  }

  public void trackBlocksModified(int count) {
    blocksModified += count;
  }

  public void reset() {
    itemsMoved = 0;
    blocksModified = 0;
    energyConsumed = 0;
  }
}
