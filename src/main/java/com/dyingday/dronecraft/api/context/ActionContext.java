/* (C)2025 */
package com.dyingday.dronecraft.api.context;

import com.dyingday.dronecraft.api.behavior.action.ActionExecutionRecord;
import com.dyingday.dronecraft.api.behavior.action.ActionResult;
import com.dyingday.dronecraft.api.context.executor.IExecutor;
import com.dyingday.dronecraft.api.context.executor.IExecutorBlockEntity;
import com.dyingday.dronecraft.api.context.executor.IExecutorEntity;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides all context needed for action execution including executor state, world queries,
 * capability access, resource management, and execution tracking. This is the primary interface
 * between actions and the game world.
 */
public class ActionContext {
  /** The executor performing actions (drone, block entity, etc.) */
  private final IExecutor executor;

  /** The world/level the executor exists in */
  private final Level level;

  /** The executor's current position in the world */
  private final Vec3 executorPosition;

  /** The executor's current block position */
  private final BlockPos executorBlockPos;

  /** Temporary variables for action execution, cleared between action trees */
  private final Map<String, Object> variables;

  /** Persistent data that survives across executions */
  private final Map<String, Object> persistentData;

  /** Unique identifier for this execution session */
  private final UUID executionId;

  /** Action-specific temporary storage, cleared after each action */
  private final Map<String, Object> actionLocalData;

  /** Historical record of action executions for debugging and optimization */
  private final List<ActionExecutionRecord> executionHistory;

  /** Whether to track execution history */
  private final boolean trackHistory;

  /** Maximum number of ticks an action can execute before being considered timed out */
  private final int maxTicksPerAction;

  /** Manages resource consumption limits (energy, items, blocks) */
  private final ResourceManager resourceManager;

  /** Current number of ticks this action has been executing */
  private int ticksExecuting;

  /** Cached item capability handler */
  private ResourceHandler<@NotNull ItemResource> itemHandler;

  /** Cached fluid capability handler */
  private ResourceHandler<@NotNull FluidResource> fluidHandler;

  /** Cached energy capability handler */
  private EnergyHandler energyHandler;

  /** Whether capabilities have been cached yet */
  private boolean capabilitiesCached = false;

  /**
   * Private constructor - use Builder to create instances
   *
   * @param builder The builder containing configuration
   */
  private ActionContext(Builder builder) {
    this.executor = builder.executor;
    this.level = executor.getLevel();
    this.executorPosition = executor.getPosition();
    this.executorBlockPos = executor.getBlockPos();
    this.variables = new HashMap<>(builder.variables);
    this.persistentData = builder.persistentData;
    this.ticksExecuting = 0;
    this.executionId = UUID.randomUUID();
    this.actionLocalData = new HashMap<>();
    this.executionHistory = builder.trackHistory ? new ArrayList<>() : null;
    this.trackHistory = builder.trackHistory;
    this.maxTicksPerAction = builder.maxTicksPerAction;
    this.resourceManager = new ResourceManager(builder.resourceLimits);
  }

  /// Core Getters
  /**
   * Get the executor performing actions
   *
   * @return The executor instance
   */
  public IExecutor getExecutor() {
    return executor;
  }

  /**
   * Get the executor as a BlockEntity if it is block-based
   *
   * @return Optional containing the BlockEntity, or empty if not block-based
   */
  public Optional<BlockEntity> getExecutorAsBlockEntity() {
    return executor.asBlockEntity();
  }

  /**
   * Get the executor as an Entity if it is entity-based
   *
   * @return Optional containing the Entity, or empty if not entity-based
   */
  public Optional<Entity> getExecutorAsEntity() {
    return executor.asEntity();
  }

  /**
   * Get the level/world the executor exists in
   *
   * @return The level instance
   */
  public Level getLevel() {
    return level;
  }

  /**
   * Get the executor's current position as a Vec3
   *
   * @return The position vector
   */
  public Vec3 getExecutorPosition() {
    return executorPosition;
  }

  /**
   * Get the executor's current block position
   *
   * @return The block position
   */
  public BlockPos getExecutorBlockPos() {
    return executorBlockPos;
  }

  /**
   * Get the direction the executor is facing
   *
   * @return The facing direction
   */
  public Direction getFacing() {
    return executor.getFacing();
  }

  /**
   * Get the normalized look vector for the executor
   *
   * @return The look direction vector
   */
  public Vec3 getLookVector() {
    return executor.getLookVector();
  }

  /**
   * Get the unique identifier for this execution session
   *
   * @return The execution UUID
   */
  public UUID getExecutionId() {
    return executionId;
  }

  /**
   * Get the server instance if running server-side
   *
   * @return Optional containing the server, or empty if client-side
   */
  public Optional<MinecraftServer> getServer() {
    return level.isClientSide() ? Optional.empty() : Optional.of(level.getServer());
  }

  /**
   * Check if this context is valid for action execution
   *
   * @return True if the context is valid (server-side, executor exists, chunk loaded)
   */
  public boolean isValid() {
    return level != null
        && !level.isClientSide()
        && executor != null
        && !executor.isRemoved()
        && level.isLoaded(executorBlockPos);
  }

  /// Capability Access
  /** Initialise/cache capabilities for performance */
  private void ensureCapabilitiesCached() {
    if (capabilitiesCached) {
      return;
    }

    // For block-based executors
    if (executor.isBlockBased()) {
      this.itemHandler = executor.getCapability(Capabilities.Item.BLOCK, null);
      this.fluidHandler = executor.getCapability(Capabilities.Fluid.BLOCK, null);
      this.energyHandler = executor.getCapability(Capabilities.Energy.BLOCK, null);
    }
    // For entity-based executors
    else if (executor.isEntityBased()) {
      this.itemHandler = executor.getCapability(Capabilities.Item.ENTITY);
      this.fluidHandler = executor.getCapability(Capabilities.Fluid.ENTITY, null);
      this.energyHandler = executor.getCapability(Capabilities.Energy.ENTITY, null);
    }

    capabilitiesCached = true;
  }

  /**
   * Get item handler capability
   *
   * @return Item handler
   */
  public ResourceHandler<@NotNull ItemResource> getItemHandler() {
    ensureCapabilitiesCached();
    return itemHandler;
  }

  /**
   * Get item handler with specific side
   *
   * @param side Nullable direction to get item handler for
   * @return Sided item handler
   */
  public ResourceHandler<@NotNull ItemResource> getItemHandler(@Nullable Direction side) {
    if (executor.isBlockBased()) {
      return executor.getCapability(Capabilities.Item.BLOCK, side);
    } else if (executor.isEntityBased()) {
      return executor.getCapability(Capabilities.Item.ENTITY);
    }
    return null;
  }

  /**
   * Get fluid handler capability
   *
   * @return Fluid handler
   */
  public ResourceHandler<@NotNull FluidResource> getFluidHandler() {
    ensureCapabilitiesCached();
    return fluidHandler;
  }

  /**
   * Get fluid handler with specific side
   *
   * @param side Nullable direction for sided fluid handler
   * @return Sided fluid handler
   */
  public ResourceHandler<@NotNull FluidResource> getFluidHandler(@Nullable Direction side) {
    if (executor.isBlockBased()) {
      return executor.getCapability(Capabilities.Fluid.BLOCK, side);
    } else if (executor.isEntityBased()) {
      return executor.getCapability(Capabilities.Fluid.ENTITY, side);
    }
    return null;
  }

  /**
   * Get energy storage capability
   *
   * @return Energy handler
   */
  public EnergyHandler getEnergyHandler() {
    ensureCapabilitiesCached();
    return energyHandler;
  }

  /**
   * Get energy storage with specific side
   *
   * @param side Nullable direction to get energy handler
   * @return Sided energy handler
   */
  public EnergyHandler getEnergyHandler(@Nullable Direction side) {
    if (executor.isBlockBased()) {
      return executor.getCapability(Capabilities.Energy.BLOCK, side);
    } else if (executor.isEntityBased()) {
      return executor.getCapability(Capabilities.Energy.ENTITY, side);
    }
    return null;
  }

  /**
   * Get arbitrary block capability from executor
   *
   * @param <T> Block capability type
   * @param capability Block capability
   * @param side Nullable side to get block capability
   * @return Generic block capability
   */
  public <T> T getBlockCapability(
      BlockCapability<@NotNull T, @Nullable Direction> capability, @Nullable Direction side) {
    if (!executor.isBlockBased()) {
      return null;
    }
    return executor.getCapability(capability, side);
  }

  /**
   * Get arbitrary entity capability from executor
   *
   * @param <T> Entity capability type
   * @param capability Entity capability
   * @param side Nullable side to get entity capability
   * @return Generic entity capability
   */
  public <T> T getEntityCapability(
      EntityCapability<@NotNull T, @Nullable Direction> capability, @Nullable Direction side) {
    if (!executor.isEntityBased()) {
      return null;
    }
    return executor.getCapability(capability, side);
  }

  /**
   * Get block capability at specific position
   *
   * @param <T> Block capability type
   * @param pos Position to get block capability
   * @param capability Block capability
   * @param side Nullable side to get block capability
   * @return Sided block capability at position
   */
  public <T> T getBlockCapabilityAt(
      BlockPos pos,
      BlockCapability<@NotNull T, @Nullable Direction> capability,
      @Nullable Direction side) {
    return level.getCapability(capability, pos, side);
  }

  /**
   * Get item handler at specific position
   *
   * @param pos Position to get item handler at
   * @param side Nullable side to get item handler
   * @return Item handler
   */
  public ResourceHandler<@NotNull ItemResource> getItemHandlerAt(
      BlockPos pos, @Nullable Direction side) {
    return getBlockCapabilityAt(pos, Capabilities.Item.BLOCK, side);
  }

  /**
   * Get energy storage at specific position
   *
   * @param pos Position to get energy handler at
   * @param side Nullable side to get energy handler
   * @return Energy handler
   */
  public EnergyHandler getEnergyStorageAt(BlockPos pos, @Nullable Direction side) {
    return getBlockCapabilityAt(pos, Capabilities.Energy.BLOCK, side);
  }

  /// Variable system
  /**
   * Set a temporary variable in the action context
   *
   * @param key The variable key
   * @param value The variable value
   */
  public void setVariable(String key, Object value) {
    variables.put(key, value);
  }

  /**
   * Get a variable with type checking
   *
   * @param <T> The expected type
   * @param key The variable key
   * @param type The expected class type
   * @return Optional containing the typed value, or empty if not found or wrong type
   */
  public <T> Optional<T> getVariable(String key, Class<T> type) {
    Object value = variables.get(key);
    if (type.isInstance(value)) {
      return Optional.of(type.cast(value));
    }
    return Optional.empty();
  }

  /**
   * Get a variable or return a default value
   *
   * @param <T> The expected type
   * @param key The variable key
   * @param defaultValue The default value to return if not found
   * @return The variable value or default
   */
  public <T> T getVariableOrDefault(String key, T defaultValue) {
    //noinspection unchecked
    return getVariable(key, (Class<T>) defaultValue.getClass()).orElse(defaultValue);
  }

  /**
   * Check if a variable exists
   *
   * @param key The variable key
   * @return True if the variable exists
   */
  public boolean hasVariable(String key) {
    return variables.containsKey(key);
  }

  /**
   * Remove a variable from the context
   *
   * @param key The variable key to remove
   */
  public void removeVariable(String key) {
    variables.remove(key);
  }

  /**
   * Get all variable keys currently stored
   *
   * @return Unmodifiable set of variable keys
   */
  public Set<String> getVariableKeys() {
    return Collections.unmodifiableSet(variables.keySet());
  }

  /**
   * Set persistent data that survives across executions
   *
   * @param key The data key
   * @param value The data value
   */
  public void setPersistentData(String key, Object value) {
    persistentData.put(key, value);
  }

  /**
   * Get persistent data with type checking
   *
   * @param <T> The expected type
   * @param key The data key
   * @param type The expected class type
   * @return Optional containing the typed value, or empty if not found or wrong type
   */
  public <T> Optional<T> getPersistentData(String key, Class<T> type) {
    Object value = persistentData.get(key);
    if (type.isInstance(value)) {
      return Optional.of(type.cast(value));
    }
    return Optional.empty();
  }

  /**
   * Get persistent data or return a default value
   *
   * @param <T> The expected type
   * @param key The data key
   * @param defaultValue The default value to return if not found
   * @return The data value or default
   */
  public <T> T getPersistentDataOrDefault(String key, T defaultValue) {
    //noinspection unchecked
    return getPersistentData(key, (Class<T>) defaultValue.getClass()).orElse(defaultValue);
  }

  /**
   * Increment a counter stored in persistent data
   *
   * @param key The counter key
   * @return The new counter value after incrementing
   */
  public int incrementCounter(String key) {
    int current = getPersistentDataOrDefault(key, 0);
    int next = current + 1;
    setPersistentData(key, next);
    return next;
  }

  /** Clear all persistent data */
  public void clearPersistentData() {
    persistentData.clear();
  }

  /**
   * Set action-local temporary data (cleared after each action)
   *
   * @param key The data key
   * @param value The data value
   */
  public void setLocalData(String key, Object value) {
    actionLocalData.put(key, value);
  }

  /**
   * Get action-local data with type checking
   *
   * @param <T> The expected type
   * @param key The data key
   * @param type The expected class type
   * @return Optional containing the typed value, or empty if not found or wrong type
   */
  public <T> Optional<T> getLocalData(String key, Class<T> type) {
    Object value = actionLocalData.get(key);
    if (type.isInstance(value)) {
      return Optional.of(type.cast(value));
    }
    return Optional.empty();
  }

  /** Clear all action-local data */
  public void clearLocalData() {
    actionLocalData.clear();
  }

  ///  World query helpers
  /**
   * Get the block state at a specific position
   *
   * @param pos The block position
   * @return The block state at that position
   */
  public BlockState getBlockState(BlockPos pos) {
    return level.getBlockState(pos);
  }

  /**
   * Get the block entity at a specific position
   *
   * @param pos The block position
   * @return Optional containing the block entity, or empty if none exists
   */
  public Optional<BlockEntity> getBlockEntity(BlockPos pos) {
    return Optional.ofNullable(level.getBlockEntity(pos));
  }

  /**
   * Get a typed block entity at a specific position
   *
   * @param <T> The block entity type
   * @param pos The block position
   * @param type The expected block entity class
   * @return Optional containing the typed block entity, or empty if wrong type or doesn't exist
   */
  public <T extends BlockEntity> Optional<T> getBlockEntity(BlockPos pos, Class<T> type) {
    BlockEntity be = level.getBlockEntity(pos);
    if (type.isInstance(be)) {
      return Optional.of(type.cast(be));
    }
    return Optional.empty();
  }

  /**
   * Get all entities within a radius of the executor
   *
   * @param radius The search radius in blocks
   * @return List of entities within radius
   */
  public List<Entity> getEntitiesInRadius(double radius) {
    AABB searchBox = new AABB(executorBlockPos).inflate(radius);
    return level.getEntities((Entity) null, searchBox, entity -> true);
  }

  /**
   * Get all entities of a specific type within a radius
   *
   * @param <T> The entity type
   * @param entityType The entity class to search for
   * @param radius The search radius in blocks
   * @return List of typed entities within radius
   */
  public <T extends Entity> List<T> getEntitiesInRadius(Class<T> entityType, double radius) {
    AABB searchBox = new AABB(executorBlockPos).inflate(radius);
    return level.getEntitiesOfClass(entityType, searchBox);
  }

  /**
   * Perform a raycast in a specific direction
   *
   * @param direction The direction to cast
   * @param maxDistance Maximum distance to cast
   * @return BlockHitResult containing hit information, or null if executor is not an entity
   */
  public BlockHitResult raycast(Vec3 direction, double maxDistance) {
    Vec3 start = executorPosition;
    Vec3 end = start.add(direction.normalize().scale(maxDistance));
    Entity entity = executor.asEntity().orElse(null);

    if (entity == null) {
      return null;
    }

    return level.clip(
        new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity));
  }

  /**
   * Perform a raycast in the executor's look direction
   *
   * @param maxDistance Maximum distance to cast
   * @return BlockHitResult containing hit information
   */
  public BlockHitResult raycast(double maxDistance) {
    return raycast(getLookVector(), maxDistance);
  }

  /**
   * Perform a raycast to find entities in the look direction
   *
   * @param maxDistance Maximum distance to cast
   * @return Optional containing EntityHitResult if an entity was hit
   */
  public Optional<EntityHitResult> raycastEntity(double maxDistance) {
    Vec3 start = executorPosition;
    Vec3 direction = getLookVector();
    Vec3 end = start.add(direction.normalize().scale(maxDistance));

    AABB searchBox = new AABB(executorBlockPos).inflate(maxDistance);

    Entity entity = executor.asEntity().orElse(null);

    if (entity == null) {
      return Optional.empty();
    }

    EntityHitResult result =
        ProjectileUtil.getEntityHitResult(
            level, entity, start, end, searchBox, e -> !e.isSpectator() && e.isPickable(), 1.0f);

    return Optional.ofNullable(result);
  }

  /**
   * Check if a block position is loaded
   *
   * @param pos The block position to check
   * @return True if the position is loaded
   */
  public boolean isLoaded(BlockPos pos) {
    return level.isLoaded(pos);
  }

  /**
   * Check if there is a clear line of sight to a target position
   *
   * @param target The target block position
   * @return True if there is unobstructed line of sight
   */
  public boolean hasLineOfSight(BlockPos target) {
    Vec3 end = Vec3.atCenterOf(target);
    Entity entity = executor.asEntity().orElse(null);
    if (entity == null) {
      return false;
    }
    BlockHitResult result =
        level.clip(
            new ClipContext(
                executorPosition, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
    return result.getBlockPos().equals(target);
  }

  /**
   * Get the block position the executor is looking at
   *
   * @param maxDistance Maximum raycast distance
   * @return Optional containing the block position if looking at a block
   */
  public Optional<BlockPos> getLookingAtBlock(double maxDistance) {
    BlockHitResult hit = raycast(maxDistance);
    if (hit.getType() == HitResult.Type.BLOCK) {
      return Optional.of(hit.getBlockPos());
    }
    return Optional.empty();
  }

  /**
   * Get the entity the executor is looking at
   *
   * @param maxDistance Maximum raycast distance
   * @return Optional containing the entity if looking at one
   */
  public Optional<Entity> getLookingAtEntity(double maxDistance) {
    return raycastEntity(maxDistance).map(EntityHitResult::getEntity);
  }

  /// Movement helpers
  /**
   * Get the executor's current velocity
   *
   * @return Optional containing velocity vector, or empty if not an entity
   */
  public Optional<Vec3> getVelocity() {
    return executor.asEntity().map(Entity::getDeltaMovement);
  }

  /**
   * Set the executor's velocity
   *
   * @param velocity The velocity vector to set
   * @return True if velocity was set successfully
   */
  public boolean setVelocity(Vec3 velocity) {
    return executor
        .asEntity()
        .map(
            e -> {
              e.setDeltaMovement(velocity);
              return true;
            })
        .orElse(false);
  }

  /**
   * Check if the executor is on the ground
   *
   * @return True if on ground, or true by default for block-based executors
   */
  public boolean isOnGround() {
    return executor.asEntity().map(Entity::onGround).orElse(true);
  }

  /**
   * Get the executor's bounding box
   *
   * @return The AABB bounding box
   */
  public AABB getBoundingBox() {
    return executor
        .asEntity()
        .map(Entity::getBoundingBox)
        .orElseGet(() -> new AABB(executorBlockPos));
  }

  /** Increment the tick counter for this execution */
  public void tick() {
    ticksExecuting++;
  }

  /**
   * Get the number of ticks this action has been executing
   *
   * @return The tick count
   */
  public int getTicksExecuting() {
    return ticksExecuting;
  }

  /** Reset the execution state (ticks and local data) */
  public void resetExecutionState() {
    ticksExecuting = 0;
    clearLocalData();
  }

  /**
   * Check if the execution has exceeded the maximum tick limit
   *
   * @return True if timed out
   */
  public boolean hasExceededMaxTicks() {
    return ticksExecuting > maxTicksPerAction;
  }

  ///  Resource management
  /**
   * Attempt to consume energy from the executor
   *
   * @param amount The amount of energy to consume
   * @return True if energy was successfully consumed
   */
  public boolean consumeEnergy(int amount) {
    return resourceManager.consumeEnergy(amount, this);
  }

  /**
   * Check if the executor has enough energy
   *
   * @param amount The amount of energy to check
   * @return True if sufficient energy is available
   */
  public boolean hasEnergy(int amount) {
    return resourceManager.hasEnergy(amount, this);
  }

  /**
   * Get the current amount of energy stored
   *
   * @return The energy amount as an integer
   */
  public int getEnergyStored() {
    return getEnergyHandler().getAmountAsInt();
  }

  /**
   * Check if the executor can move a certain number of items (within rate limits)
   *
   * @param count The number of items to check
   * @return True if within limits
   */
  public boolean canMoveItems(int count) {
    return resourceManager.canMoveItems(count);
  }

  /**
   * Track that items were moved for rate limiting
   *
   * @param count The number of items moved
   */
  public void trackItemsMoved(int count) {
    resourceManager.trackItemsMoved(count);
  }

  /**
   * Check if the executor can modify a certain number of blocks (within rate limits)
   *
   * @param count The number of blocks to check
   * @return True if within limits
   */
  public boolean canModifyBlocks(int count) {
    return resourceManager.canModifyBlocks(count);
  }

  /**
   * Track that blocks were modified for rate limiting
   *
   * @param count The number of blocks modified
   */
  public void trackBlocksModified(int count) {
    resourceManager.trackBlocksModified(count);
  }

  /**
   * Record an action execution in the history (if tracking is enabled)
   *
   * @param actionId The action identifier
   * @param result The execution result
   * @param nanos Execution time in nanoseconds
   */
  public void recordExecution(Identifier actionId, ActionResult result, long nanos) {
    if (trackHistory && executionHistory != null) {
      executionHistory.add(
          new ActionExecutionRecord(
              actionId, result, nanos, ticksExecuting, System.currentTimeMillis()));
    }
  }

  /**
   * Get the execution history records
   *
   * @return Unmodifiable list of execution records, or empty list if not tracking
   */
  public List<ActionExecutionRecord> getExecutionHistory() {
    return trackHistory ? Collections.unmodifiableList(executionHistory) : Collections.emptyList();
  }

  /**
   * Get the total execution time of all recorded actions
   *
   * @return Total time in nanoseconds, or 0 if not tracking
   */
  public long getTotalExecutionTime() {
    if (!trackHistory || executionHistory == null) {
      return 0;
    }
    return executionHistory.stream().mapToLong(ActionExecutionRecord::executionTimeNanos).sum();
  }

  /// Inventory helpers
  /**
   * Find the first slot containing a specific item
   *
   * @param stack The item stack to search for
   * @return Optional containing the slot index, or empty if not found
   */
  public Optional<Integer> findItemSlot(ItemStack stack) {
    ResourceHandler<@NotNull ItemResource> handler = getItemHandler();
    for (int i = 0; i < handler.size(); i++) {
      ItemResource slotResource = handler.getResource(i);
      if (slotResource.is(stack.getItem())) {
        return Optional.of(i);
      }
    }
    return Optional.empty();
  }

  /**
   * Count the total number of a specific item in the inventory
   *
   * @param item The item to count
   * @return The total count across all slots
   */
  public int countItems(Item item) {
    ResourceHandler<@NotNull ItemResource> handler = getItemHandler();
    int count = 0;
    for (int i = 0; i < handler.size(); i++) {
      ItemResource slotResource = handler.getResource(i);
      if (slotResource.is(item)) {
        count += slotResource.toStack().getCount();
      }
    }
    return count;
  }

  /**
   * Check if there is space in the inventory for an item stack
   *
   * @param stack The item stack to check
   * @return True if the full stack can be inserted
   */
  public boolean hasSpaceFor(ItemStack stack) {
    Transaction transaction = Transaction.openRoot();
    int inserted = getItemHandler().insert(ItemResource.of(stack), stack.getCount(), transaction);
    transaction.close();
    return inserted == stack.getCount();
  }

  /// Debugging
  /**
   * Create a snapshot of the current context state for debugging
   *
   * @return A ContextSnapshot containing current state
   */
  public ContextSnapshot createSnapshot() {
    return new ContextSnapshot(
        executionId,
        executorBlockPos,
        ticksExecuting,
        new HashMap<>(variables),
        new HashMap<>(persistentData),
        new HashMap<>(actionLocalData),
        trackHistory ? new ArrayList<>(executionHistory) : null);
  }

  @Override
  public String toString() {
    return "ActionContext{"
        + "pos="
        + executorBlockPos
        + ", ticks="
        + ticksExecuting
        + ", vars="
        + variables.size()
        + ", persistent="
        + persistentData.size()
        + ", valid="
        + isValid()
        + "}";
  }

  /// Builder
  /** Builder to make an ActionContext instance */
  public static final class Builder {
    /** The executor that will perform actions */
    private final IExecutor executor;

    /** Initial variables to populate the context with */
    private final Map<String, Object> variables = new HashMap<>();

    /** Persistent data that survives across executions */
    private Map<String, Object> persistentData = new HashMap<>();

    /** Whether to track execution history */
    private boolean trackHistory = false;

    /** Maximum ticks before timing out */
    private int maxTicksPerAction = 200;

    /** Resource consumption limits */
    private ResourceLimits resourceLimits = ResourceLimits.DEFAULT;

    /**
     * Create a builder with an executor
     *
     * @param executor The executor to build context for
     */
    public Builder(IExecutor executor) {
      this.executor = executor;
    }

    /**
     * Create a builder from a BlockEntity
     *
     * @param blockEntity The block entity (must implement IExecutorBlockEntity)
     * @throws IllegalArgumentException if the block entity doesn't implement IExecutorBlockEntity
     */
    public Builder(BlockEntity blockEntity) {
      if (!(blockEntity instanceof IExecutorBlockEntity)) {
        throw new IllegalArgumentException(
            "BlockEntity must implement IExecutorBlockEntity: " + blockEntity.getClass());
      }
      this.executor = (IExecutorBlockEntity) blockEntity;
    }

    /**
     * Create a builder from an Entity
     *
     * @param entity The entity (must implement IExecutorEntity)
     * @throws IllegalArgumentException if the entity doesn't implement IExecutorEntity
     */
    public Builder(Entity entity) {
      if (!(entity instanceof IExecutorEntity)) {
        throw new IllegalArgumentException(
            "Entity must implement IExecutorEntity: " + entity.getClass());
      }
      this.executor = (IExecutorEntity) entity;
    }

    /**
     * Add multiple variables to the context
     *
     * @param variables Map of variables to add
     * @return This builder for chaining
     */
    public Builder withVariables(Map<String, Object> variables) {
      this.variables.putAll(variables);
      return this;
    }

    /**
     * Add a single variable to the context
     *
     * @param key The variable key
     * @param value The variable value
     * @return This builder for chaining
     */
    public Builder withVariable(String key, Object value) {
      this.variables.put(key, value);
      return this;
    }

    /**
     * Set the persistent data for the context
     *
     * @param persistentData Map of persistent data
     * @return This builder for chaining
     */
    public Builder withPersistentData(Map<String, Object> persistentData) {
      this.persistentData = persistentData;
      return this;
    }

    /**
     * Enable or disable execution history tracking
     *
     * @param track True to enable history tracking
     * @return This builder for chaining
     */
    public Builder trackHistory(boolean track) {
      this.trackHistory = track;
      return this;
    }

    /**
     * Set the maximum ticks per action before timing out
     *
     * @param ticks Maximum tick count
     * @return This builder for chaining
     */
    public Builder maxTicksPerAction(int ticks) {
      this.maxTicksPerAction = ticks;
      return this;
    }

    /**
     * Set the resource consumption limits
     *
     * @param limits The resource limits configuration
     * @return This builder for chaining
     */
    public Builder resourceLimits(ResourceLimits limits) {
      this.resourceLimits = limits;
      return this;
    }

    /**
     * Build the ActionContext
     *
     * @return The constructed ActionContext
     * @throws IllegalStateException if executor or level is null
     */
    public ActionContext build() {
      if (executor == null) {
        throw new IllegalStateException("Executor cannot be null");
      }
      if (executor.getLevel() == null) {
        throw new IllegalStateException("Executor level cannot be null");
      }
      return new ActionContext(this);
    }
  }
}
