package net.satisfyu.meadow.block.cookingPot;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.satisfyu.meadow.entity.ModEntities;
import net.satisfyu.meadow.util.GeneralUtil;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CookingPotBlock extends BlockWithEntity {
    private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.25, 0.00625, 0.203125, 0.796875, 0.084375, 0.75), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.1875, 0.625, 0.1875, 0.8125, 0.7421875, 0.265625), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.1875, 0, 0.1875, 0.8125, 0.625, 0.265625), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.1875, 0, 0.734375, 0.8125, 0.625, 0.8125), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.8125, 0.5859375, 0.2265625, 0.96875, 0.703125, 0.3046875), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.03125, 0.5859375, 0.2265625, 0.1875, 0.703125, 0.3046875), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.8125, 0.5859375, 0.6953125, 0.96875, 0.703125, 0.7734375), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.03125, 0.5859375, 0.6953125, 0.1875, 0.703125, 0.7734375), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.734375, 0.625, 0.265625, 0.8125, 0.7421875, 0.734375), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.734375, 0, 0.265625, 0.8125, 0.625, 0.734375), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.1875, 0, 0.265625, 0.265625, 0.625, 0.734375), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.890625, 0.5859375, 0.3046875, 0.96875, 0.703125, 0.6953125), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.03125, 0.5859375, 0.3046875, 0.109375, 0.703125, 0.6953125), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.1875, 0.625, 0.265625, 0.265625, 0.7421875, 0.734375), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.1875, 0.625, 0.734375, 0.8125, 0.7421875, 0.8125), BooleanBiFunction.OR);
        return shape;
    };

    public static final Map<Direction, VoxelShape> SHAPE = Util.make(new HashMap<>(), map -> {
        for (Direction direction : Stream.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST).toList()) {
            map.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
        }
    });

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty HAS_RAW_BEEF = BooleanProperty.of("has_raw_beef");
    public static final Property<Boolean> COOKING = BooleanProperty.of("cooking");

    public CookingPotBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(HAS_RAW_BEEF, false).with(COOKING, false));
    }


    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        final BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof NamedScreenHandlerFactory factory) {
            player.openHandledScreen(factory);
            return ActionResult.SUCCESS;
        } else {
            return super.onUse(state, world, pos, player, hand, hit);
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CookingPotBlockEntity pot) {
                if (world instanceof ServerWorld) {
                    ItemScatterer.spawn(world, pos, pot);
                }
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HAS_RAW_BEEF, COOKING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE.get(state.get(FACING));
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(COOKING)) {
            double x = (double)pos.getX() + 0.5 + random.nextDouble() / 4.0 * (double)(random.nextBoolean() ? 1 : -1);
            double y = (double)pos.getY() + 0.7;
            double z = (double)pos.getZ() + 0.5 + random.nextDouble() / 4.0 * (double)(random.nextBoolean() ? 1 : -1);
            /*
            if (random.nextDouble() < 0.3) {
                world.playSound(d, e, f, VinerySoundEvents.BLOCK_COOKING_POT_JUICE_BOILING, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }

             */

            double j = random.nextDouble() * 6.0 / 16.0;
            world.addParticle(ParticleTypes.SMOKE, x, y + j, z, 0, 0.0, 0);
            world.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, x, y + j, z, 0, 0.0, 0);
            world.addParticle(ParticleTypes.BUBBLE, x, y + j, z, 0, 0.0, 0);
            world.addParticle(ParticleTypes.BUBBLE_POP, x, y + j, z, 0, 0.0, 0);
        }
    }



    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModEntities.COOKING_POT_BLOCK_ENTITY, (world1, pos, state1, be) -> be.tick(world1, pos, state1, be));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CookingPotBlockEntity(pos, state);
    }
}