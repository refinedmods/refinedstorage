package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeExternalStorage;
import com.raoulvdberge.refinedstorage.render.constants.ConstantsCable;
import com.raoulvdberge.refinedstorage.render.constants.ConstantsExternalStorage;
import com.raoulvdberge.refinedstorage.tile.TileExternalStorage;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BlockExternalStorage extends BlockCable {
    public BlockExternalStorage() {
        super("external_storage");
    }

    @Override
    public List<AxisAlignedBB> getCollisionBoxes(TileEntity tile, IBlockState state) {
        List<AxisAlignedBB> boxes = super.getCollisionBoxes(tile, state);

        switch (state.getValue(getDirection().getProperty())) {
            case NORTH:
                boxes.add(ConstantsCable.HOLDER_NORTH_AABB);
                boxes.add(ConstantsExternalStorage.HEAD_NORTH_AABB);
                break;
            case EAST:
                boxes.add(ConstantsCable.HOLDER_EAST_AABB);
                boxes.add(ConstantsExternalStorage.HEAD_EAST_AABB);
                break;
            case SOUTH:
                boxes.add(ConstantsCable.HOLDER_SOUTH_AABB);
                boxes.add(ConstantsExternalStorage.HEAD_SOUTH_AABB);
                break;
            case WEST:
                boxes.add(ConstantsCable.HOLDER_WEST_AABB);
                boxes.add(ConstantsExternalStorage.HEAD_WEST_AABB);
                break;
            case UP:
                boxes.add(ConstantsCable.HOLDER_UP_AABB);
                boxes.add(ConstantsExternalStorage.HEAD_UP_AABB);
                break;
            case DOWN:
                boxes.add(ConstantsCable.HOLDER_DOWN_AABB);
                boxes.add(ConstantsExternalStorage.HEAD_DOWN_AABB);
                break;
        }

        return boxes;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileExternalStorage();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (hitCablePart(state, world, pos, hitX, hitY, hitZ)) {
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
