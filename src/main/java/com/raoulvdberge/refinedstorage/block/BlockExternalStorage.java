package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeExternalStorage;
import com.raoulvdberge.refinedstorage.render.collision.CollisionGroup;
import com.raoulvdberge.refinedstorage.render.collision.constants.ConstantsCable;
import com.raoulvdberge.refinedstorage.render.collision.constants.ConstantsExternalStorage;
import com.raoulvdberge.refinedstorage.tile.TileExternalStorage;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BlockExternalStorage extends BlockCable {
    public BlockExternalStorage() {
        super("external_storage");
    }

    @Override
    public List<CollisionGroup> getCollisions(TileEntity tile, IBlockState state) {
        List<CollisionGroup> groups = super.getCollisions(tile, state);

        switch (state.getValue(getDirection().getProperty())) {
            case NORTH:
                groups.add(ConstantsCable.HOLDER_NORTH);
                groups.add(ConstantsExternalStorage.HEAD_NORTH);
                break;
            case EAST:
                groups.add(ConstantsCable.HOLDER_EAST);
                groups.add(ConstantsExternalStorage.HEAD_EAST);
                break;
            case SOUTH:
                groups.add(ConstantsCable.HOLDER_SOUTH);
                groups.add(ConstantsExternalStorage.HEAD_SOUTH);
                break;
            case WEST:
                groups.add(ConstantsCable.HOLDER_WEST);
                groups.add(ConstantsExternalStorage.HEAD_WEST);
                break;
            case UP:
                groups.add(ConstantsCable.HOLDER_UP);
                groups.add(ConstantsExternalStorage.HEAD_UP);
                break;
            case DOWN:
                groups.add(ConstantsCable.HOLDER_DOWN);
                groups.add(ConstantsExternalStorage.HEAD_DOWN);
                break;
        }

        return groups;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileExternalStorage();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!canAccessGui(state, world, pos, hitX, hitY, hitZ)) {
            return false;
        }

        if (!world.isRemote) {
            tryOpenNetworkGui(RSGui.EXTERNAL_STORAGE, player, world, pos, side);
        }

        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
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
    }

    @Override
    @Nullable
    public Direction getDirection() {
        return Direction.ANY;
    }
}
