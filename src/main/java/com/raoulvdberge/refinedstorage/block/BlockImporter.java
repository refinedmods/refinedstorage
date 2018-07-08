package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.render.collision.CollisionGroup;
import com.raoulvdberge.refinedstorage.render.collision.constants.ConstantsImporter;
import com.raoulvdberge.refinedstorage.tile.TileImporter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BlockImporter extends BlockCable {
    public BlockImporter() {
        super("importer");
    }

    @Override
    public List<CollisionGroup> getCollisions(TileEntity tile, IBlockState state) {
        List<CollisionGroup> groups = super.getCollisions(tile, state);

        switch (state.getValue(getDirection().getProperty())) {
            case NORTH:
                groups.add(ConstantsImporter.LINE_NORTH);
                break;
            case EAST:
                groups.add(ConstantsImporter.LINE_EAST);
                break;
            case SOUTH:
                groups.add(ConstantsImporter.LINE_SOUTH);
                break;
            case WEST:
                groups.add(ConstantsImporter.LINE_WEST);
                break;
            case UP:
                groups.add(ConstantsImporter.LINE_UP);
                break;
            case DOWN:
                groups.add(ConstantsImporter.LINE_DOWN);
                break;
        }

        return groups;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileImporter();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!canAccessGui(state, world, pos, hitX, hitY, hitZ)) {
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
