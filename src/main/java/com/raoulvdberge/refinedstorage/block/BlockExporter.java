package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.render.constants.ConstantsExporter;
import com.raoulvdberge.refinedstorage.tile.TileExporter;
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

public class BlockExporter extends BlockCable {
    public BlockExporter() {
        super("exporter");
    }

    @Override
    public List<AxisAlignedBB> getCollisionBoxes(TileEntity tile, IBlockState state) {
        List<AxisAlignedBB> boxes = super.getCollisionBoxes(tile, state);

        switch (state.getValue(getDirection().getProperty())) {
            case NORTH:
                boxes.add(ConstantsExporter.LINE_NORTH_1_AABB);
                boxes.add(ConstantsExporter.LINE_NORTH_2_AABB);
                boxes.add(ConstantsExporter.LINE_NORTH_3_AABB);
                break;
            case EAST:
                boxes.add(ConstantsExporter.LINE_EAST_1_AABB);
                boxes.add(ConstantsExporter.LINE_EAST_2_AABB);
                boxes.add(ConstantsExporter.LINE_EAST_3_AABB);
                break;
            case SOUTH:
                boxes.add(ConstantsExporter.LINE_SOUTH_1_AABB);
                boxes.add(ConstantsExporter.LINE_SOUTH_2_AABB);
                boxes.add(ConstantsExporter.LINE_SOUTH_3_AABB);
                break;
            case WEST:
                boxes.add(ConstantsExporter.LINE_WEST_1_AABB);
                boxes.add(ConstantsExporter.LINE_WEST_2_AABB);
                boxes.add(ConstantsExporter.LINE_WEST_3_AABB);
                break;
            case UP:
                boxes.add(ConstantsExporter.LINE_UP_1_AABB);
                boxes.add(ConstantsExporter.LINE_UP_2_AABB);
                boxes.add(ConstantsExporter.LINE_UP_3_AABB);
                break;
            case DOWN:
                boxes.add(ConstantsExporter.LINE_DOWN_1_AABB);
                boxes.add(ConstantsExporter.LINE_DOWN_2_AABB);
                boxes.add(ConstantsExporter.LINE_DOWN_3_AABB);
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
    @Nullable
    public Direction getDirection() {
        return Direction.ANY;
    }
}
