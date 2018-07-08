package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.render.constants.ConstantsImporter;
import com.raoulvdberge.refinedstorage.tile.TileImporter;
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

public class BlockImporter extends BlockCable {
    public BlockImporter() {
        super("importer");
    }

    @Override
    public List<AxisAlignedBB> getCollisionBoxes(TileEntity tile, IBlockState state) {
        List<AxisAlignedBB> boxes = super.getCollisionBoxes(tile, state);

        switch (state.getValue(getDirection().getProperty())) {
            case NORTH:
                boxes.add(ConstantsImporter.LINE_NORTH_1_AABB);
                boxes.add(ConstantsImporter.LINE_NORTH_2_AABB);
                boxes.add(ConstantsImporter.LINE_NORTH_3_AABB);
                break;
            case EAST:
                boxes.add(ConstantsImporter.LINE_EAST_1_AABB);
                boxes.add(ConstantsImporter.LINE_EAST_2_AABB);
                boxes.add(ConstantsImporter.LINE_EAST_3_AABB);
                break;
            case SOUTH:
                boxes.add(ConstantsImporter.LINE_SOUTH_1_AABB);
                boxes.add(ConstantsImporter.LINE_SOUTH_2_AABB);
                boxes.add(ConstantsImporter.LINE_SOUTH_3_AABB);
                break;
            case WEST:
                boxes.add(ConstantsImporter.LINE_WEST_1_AABB);
                boxes.add(ConstantsImporter.LINE_WEST_2_AABB);
                boxes.add(ConstantsImporter.LINE_WEST_3_AABB);
                break;
            case UP:
                boxes.add(ConstantsImporter.LINE_UP_1_AABB);
                boxes.add(ConstantsImporter.LINE_UP_2_AABB);
                boxes.add(ConstantsImporter.LINE_UP_3_AABB);
                break;
            case DOWN:
                boxes.add(ConstantsImporter.LINE_DOWN_1_AABB);
                boxes.add(ConstantsImporter.LINE_DOWN_2_AABB);
                boxes.add(ConstantsImporter.LINE_DOWN_3_AABB);
                break;
        }

        return boxes;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileImporter();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (hitCablePart(state, world, pos, hitX, hitY, hitZ)) {
            return false;
        }

        if (!world.isRemote) {
            tryOpenNetworkGui(RSGui.IMPORTER, player, world, pos, side);
        }

        return true;
    }

    @Override
    @Nullable
    public Direction getDirection() {
        return Direction.ANY;
    }
}
