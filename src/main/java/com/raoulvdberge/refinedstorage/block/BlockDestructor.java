package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.render.collision.CollisionGroup;
import com.raoulvdberge.refinedstorage.tile.TileDestructor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BlockDestructor extends BlockCable {
    public BlockDestructor() {
        super("destructor");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileDestructor();
    }

    @Override
    public List<CollisionGroup> getCollisions(TileEntity tile, IBlockState state) {
        return RSBlocks.CONSTRUCTOR.getCollisions(tile, state);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!canAccessGui(state, world, pos, hitX, hitY, hitZ)) {
            return false;
        }

        if (!world.isRemote) {
            tryOpenNetworkGui(RSGui.DESTRUCTOR, player, world, pos, side);
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
