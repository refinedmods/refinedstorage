package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.render.constants.ConstantsCable;
import com.raoulvdberge.refinedstorage.render.constants.ConstantsConstructor;
import com.raoulvdberge.refinedstorage.tile.TileConstructor;
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

public class BlockConstructor extends BlockCable {
    public BlockConstructor() {
        super("constructor");
    }

    @Override
    public List<AxisAlignedBB> getCollisionBoxes(TileEntity tile, IBlockState state) {
        List<AxisAlignedBB> boxes = super.getCollisionBoxes(tile, state);

        switch (state.getValue(getDirection().getProperty())) {
            case NORTH:
                boxes.add(ConstantsCable.HOLDER_NORTH_AABB);
                boxes.add(ConstantsConstructor.HEAD_NORTH_AABB);
                break;
            case EAST:
                boxes.add(ConstantsCable.HOLDER_EAST_AABB);
                boxes.add(ConstantsConstructor.HEAD_EAST_AABB);
                break;
            case SOUTH:
                boxes.add(ConstantsCable.HOLDER_SOUTH_AABB);
                boxes.add(ConstantsConstructor.HEAD_SOUTH_AABB);
                break;
            case WEST:
                boxes.add(ConstantsCable.HOLDER_WEST_AABB);
                boxes.add(ConstantsConstructor.HEAD_WEST_AABB);
                break;
            case UP:
                boxes.add(ConstantsCable.HOLDER_UP_AABB);
                boxes.add(ConstantsConstructor.HEAD_UP_AABB);
                break;
            case DOWN:
                boxes.add(ConstantsCable.HOLDER_DOWN_AABB);
                boxes.add(ConstantsConstructor.HEAD_DOWN_AABB);
                break;
        }

        return boxes;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileConstructor();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (hitCablePart(state, world, pos, hitX, hitY, hitZ)) {
            return false;
        }

        if (!world.isRemote) {
            tryOpenNetworkGui(RSGui.CONSTRUCTOR, player, world, pos, side);
        }

        return true;
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    @Nullable
    public Direction getDirection() {
        return Direction.ANY;
    }
}
