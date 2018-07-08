package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.render.collision.CollisionGroup;
import com.raoulvdberge.refinedstorage.render.collision.constants.ConstantsExporter;
import com.raoulvdberge.refinedstorage.tile.TileExporter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BlockExporter extends BlockCable {
    public BlockExporter() {
        super("exporter");
    }

    @Override
    public List<CollisionGroup> getCollisions(TileEntity tile, IBlockState state) {
        List<CollisionGroup> groups = super.getCollisions(tile, state);

        switch (state.getValue(getDirection().getProperty())) {
            case NORTH:
                groups.add(ConstantsExporter.LINE_NORTH);
                break;
            case EAST:
                groups.add(ConstantsExporter.LINE_EAST);
                break;
            case SOUTH:
                groups.add(ConstantsExporter.LINE_SOUTH);
                break;
            case WEST:
                groups.add(ConstantsExporter.LINE_WEST);
                break;
            case UP:
                groups.add(ConstantsExporter.LINE_UP);
                break;
            case DOWN:
                groups.add(ConstantsExporter.LINE_DOWN);
                break;
        }

        return groups;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileExporter();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!canAccessGui(state, world, pos, hitX, hitY, hitZ)) {
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
