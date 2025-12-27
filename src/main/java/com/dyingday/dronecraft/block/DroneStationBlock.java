/* (C)2025 */
package com.dyingday.dronecraft.block;

import com.dyingday.dronecraft.blockentity.DroneStationBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class DroneStationBlock extends Block implements EntityBlock {
  public DroneStationBlock(Properties properties) {
    super(properties);
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
    return new DroneStationBE(pos, state);
  }

  @Override
  public @Nullable <T extends BlockEntity> BlockEntityTicker<@NotNull T> getTicker(
      @NotNull Level level,
      @NotNull BlockState state,
      @NotNull BlockEntityType<@NotNull T> blockEntityType) {
    if (level.isClientSide()) {
      return null;
    }

    return (lvl, pos, st, be) -> {
      if (be instanceof DroneStationBE station) {
        station.tick(lvl, pos, st);
      }
    };
  }

  @Override
  public void setPlacedBy(
      @NotNull Level level,
      @NotNull BlockPos pos,
      @NotNull BlockState state,
      @Nullable LivingEntity placer,
      @NotNull ItemStack stack) {
    super.setPlacedBy(level, pos, state, placer, stack);

    // Notify the block entity that it was placed
    if (level.getBlockEntity(pos) instanceof DroneStationBE station) {
      station.onPlaced();
    }
  }

  @Override
  protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
    return RenderShape.MODEL;
  }
}
