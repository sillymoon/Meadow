package net.satisfyu.meadow.entity.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.satisfyu.meadow.registry.BlockEntityRegistry;

public class WheelBarrowBlockEntity extends FlowerPotBlockEntity {
	public WheelBarrowBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.WHEEL_BARROW_BLOCK_ENTITY.get(), pos, state);
		setFlower(null);
	}
}