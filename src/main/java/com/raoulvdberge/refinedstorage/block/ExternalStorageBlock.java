package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.block.info.BlockDirection;
import com.raoulvdberge.refinedstorage.util.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class ExternalStorageBlock extends CableBlock {
    private static final VoxelShape HEAD_NORTH = makeCuboidShape(3, 3, 0, 13, 13, 2);
    private static final VoxelShape HEAD_EAST = makeCuboidShape(14, 3, 3, 16, 13, 13);
    private static final VoxelShape HEAD_SOUTH = makeCuboidShape(3, 3, 14, 13, 13, 16);
    private static final VoxelShape HEAD_WEST = makeCuboidShape(0, 3, 3, 2, 13, 13);
    private static final VoxelShape HEAD_UP = makeCuboidShape(3, 14, 3, 13, 16, 13);
    private static final VoxelShape HEAD_DOWN = makeCuboidShape(3, 0, 3, 13, 2, 13);

    public ExternalStorageBlock() {
        super(BlockUtils.DEFAULT_GLASS_PROPERTIES);

        this.setRegistryName(RS.ID, "external_storage");
    }

    @Override
    public BlockDirection getDirection() {
        return BlockDirection.ANY;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
        VoxelShape shape = super.getShape(state, world, pos, ctx);

        if (state.get(NORTH)) {
            shape = VoxelShapes.or(shape, HEAD_NORTH);
        }

        if (state.get(EAST)) {
            shape = VoxelShapes.or(shape, HEAD_EAST);
        }

        if (state.get(SOUTH)) {
            shape = VoxelShapes.or(shape, HEAD_SOUTH);
        }

        if (state.get(WEST)) {
            shape = VoxelShapes.or(shape, HEAD_WEST);
        }

        if (state.get(UP)) {
            shape = VoxelShapes.or(shape, HEAD_UP);
        }

        if (state.get(DOWN)) {
            shape = VoxelShapes.or(shape, HEAD_DOWN);
        }

        return shape;
    }

    /* TODO
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumHand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (!canAccessGui(state, world, pos, hitX, hitY, hitZ)) {
            return false;
        }

        return openNetworkGui(RSGui.EXTERNAL_STORAGE, player, world, pos, side);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        super.neighborChanged(state, world, pos, block, fromPos);

        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);

            if (tile instanceof TileExternalStorage) {
                NetworkNodeExternalStorage externalStorage = ((TileExternalStorage) tile).getNode();

                if (externalStorage.getNetwork() != null) {
                    externalStorage.updateStorage(externalStorage.getNetwork());
                }
            }
        }
    }*/
}
