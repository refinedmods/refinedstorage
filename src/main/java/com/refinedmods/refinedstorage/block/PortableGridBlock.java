package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.grid.factory.PortableGridBlockGridFactory;
import com.refinedmods.refinedstorage.item.blockitem.PortableGridBlockItem;
import com.refinedmods.refinedstorage.tile.grid.portable.PortableGridDiskState;
import com.refinedmods.refinedstorage.tile.grid.portable.PortableGridTile;
import com.refinedmods.refinedstorage.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class PortableGridBlock extends BaseBlock implements EntityBlock {
    public static final EnumProperty<PortableGridDiskState> DISK_STATE = EnumProperty.create("disk_state", PortableGridDiskState.class);
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 13.2, 16);

    private final PortableGridBlockItem.Type type;

    public PortableGridBlock(PortableGridBlockItem.Type type) {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.type = type;
        this.registerDefaultState(defaultBlockState().setValue(DISK_STATE, PortableGridDiskState.NONE).setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);

        builder.add(DISK_STATE);
        builder.add(ACTIVE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockDirection getDirection() {
        return BlockDirection.HORIZONTAL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PortableGridTile(type, pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : new BlockEntityTicker<T>() {
            @Override
            public void tick(Level level, BlockPos pos, BlockState state, T tile) {
                if (tile instanceof PortableGridTile) {
                    PortableGridTile.serverTick((PortableGridTile) tile);
                }
            }
        };
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            API.instance().getGridManager().openGrid(PortableGridBlockGridFactory.ID, (ServerPlayer) player, pos);

            ((PortableGridTile) level.getBlockEntity(pos)).onOpened();
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide) {
            ((PortableGridTile) level.getBlockEntity(pos)).applyDataFromItemToTile(stack);
            ((PortableGridTile) level.getBlockEntity(pos)).updateState();
        }
    }
}
