package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.block.info.BlockDirection;
import com.raoulvdberge.refinedstorage.container.ExternalStorageContainer;
import com.raoulvdberge.refinedstorage.container.factory.PositionalTileContainerProvider;
import com.raoulvdberge.refinedstorage.tile.ExternalStorageTile;
import com.raoulvdberge.refinedstorage.util.BlockUtils;
import com.raoulvdberge.refinedstorage.util.NetworkUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

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

        Direction direction = state.get(getDirection().getProperty());

        if (direction == Direction.NORTH) {
            shape = VoxelShapes.or(shape, HEAD_NORTH);
        }

        if (direction == Direction.EAST) {
            shape = VoxelShapes.or(shape, HEAD_EAST);
        }

        if (direction == Direction.SOUTH) {
            shape = VoxelShapes.or(shape, HEAD_SOUTH);
        }

        if (direction == Direction.WEST) {
            shape = VoxelShapes.or(shape, HEAD_WEST);
        }

        if (direction == Direction.UP) {
            shape = VoxelShapes.or(shape, HEAD_UP);
        }

        if (direction == Direction.DOWN) {
            shape = VoxelShapes.or(shape, HEAD_DOWN);
        }

        return shape;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ExternalStorageTile();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            return NetworkUtils.attemptModify(world, pos, hit.getFace(), player, () -> NetworkHooks.openGui(
                (ServerPlayerEntity) player,
                new PositionalTileContainerProvider<ExternalStorageTile>(
                    new TranslationTextComponent("gui.refinedstorage.external_storage"),
                    (tile, windowId, inventory, p) -> new ExternalStorageContainer(tile, player, windowId),
                    pos
                ),
                pos
            ));
        }

        return true;
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
