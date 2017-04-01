package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.tile.TileExporter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BlockExporter extends BlockCable {
    private static final AxisAlignedBB LINE_NORTH_1_AABB = RSUtils.getAABB(6, 6, 0, 10, 10, 2);
    private static final AxisAlignedBB LINE_NORTH_2_AABB = RSUtils.getAABB(5, 5, 2, 11, 11, 4);
    private static final AxisAlignedBB LINE_NORTH_3_AABB = RSUtils.getAABB(3, 3, 4, 13, 13, 6);
    private static final AxisAlignedBB LINE_EAST_1_AABB = RSUtils.getAABB(14, 6, 6, 16, 10, 10);
    private static final AxisAlignedBB LINE_EAST_2_AABB = RSUtils.getAABB(12, 5, 5, 14, 11, 11);
    private static final AxisAlignedBB LINE_EAST_3_AABB = RSUtils.getAABB(10, 3, 3, 12, 13, 13);
    private static final AxisAlignedBB LINE_SOUTH_1_AABB = RSUtils.getAABB(6, 6, 14, 10, 10, 16);
    private static final AxisAlignedBB LINE_SOUTH_2_AABB = RSUtils.getAABB(5, 5, 12, 11, 11, 14);
    private static final AxisAlignedBB LINE_SOUTH_3_AABB = RSUtils.getAABB(3, 3, 10, 13, 13, 12);
    private static final AxisAlignedBB LINE_WEST_1_AABB = RSUtils.getAABB(0, 6, 6, 2, 10, 10);
    private static final AxisAlignedBB LINE_WEST_2_AABB = RSUtils.getAABB(2, 5, 5, 4, 11, 11);
    private static final AxisAlignedBB LINE_WEST_3_AABB = RSUtils.getAABB(4, 3, 3, 6, 13, 13);
    private static final AxisAlignedBB LINE_UP_1_AABB = RSUtils.getAABB(6, 14, 6, 10, 16, 10);
    private static final AxisAlignedBB LINE_UP_2_AABB = RSUtils.getAABB(5, 12, 5, 11, 14, 11);
    private static final AxisAlignedBB LINE_UP_3_AABB = RSUtils.getAABB(3, 10, 3, 13, 12, 13);
    private static final AxisAlignedBB LINE_DOWN_1_AABB = RSUtils.getAABB(6, 0, 6, 10, 2, 10);
    private static final AxisAlignedBB LINE_DOWN_2_AABB = RSUtils.getAABB(5, 2, 5, 11, 4, 11);
    private static final AxisAlignedBB LINE_DOWN_3_AABB = RSUtils.getAABB(3, 4, 3, 13, 6, 13);

    public BlockExporter() {
        super("exporter");
    }

    @Override
    public List<AxisAlignedBB> getNonUnionizedCollisionBoxes(IBlockState state) {
        List<AxisAlignedBB> boxes = new ArrayList<>();

        switch (state.getValue(getDirection().getProperty())) {
            case NORTH:
                boxes.add(LINE_NORTH_1_AABB);
                boxes.add(LINE_NORTH_2_AABB);
                boxes.add(LINE_NORTH_3_AABB);
                break;
            case EAST:
                boxes.add(LINE_EAST_1_AABB);
                boxes.add(LINE_EAST_2_AABB);
                boxes.add(LINE_EAST_3_AABB);
                break;
            case SOUTH:
                boxes.add(LINE_SOUTH_1_AABB);
                boxes.add(LINE_SOUTH_2_AABB);
                boxes.add(LINE_SOUTH_3_AABB);
                break;
            case WEST:
                boxes.add(LINE_WEST_1_AABB);
                boxes.add(LINE_WEST_2_AABB);
                boxes.add(LINE_WEST_3_AABB);
                break;
            case UP:
                boxes.add(LINE_UP_1_AABB);
                boxes.add(LINE_UP_2_AABB);
                boxes.add(LINE_UP_3_AABB);
                break;
            case DOWN:
                boxes.add(LINE_DOWN_1_AABB);
                boxes.add(LINE_DOWN_2_AABB);
                boxes.add(LINE_DOWN_3_AABB);
                break;
        }

        return boxes;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileExporter();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (hitCablePart(state, world, pos, hitX, hitY, hitZ)) {
            return false;
        }

        if (!world.isRemote) {
            tryOpenNetworkGui(RSGui.EXPORTER, player, world, pos, side);
        }

        return true;
    }

    @Override
    public Direction getDirection() {
        return Direction.ANY;
    }
}
