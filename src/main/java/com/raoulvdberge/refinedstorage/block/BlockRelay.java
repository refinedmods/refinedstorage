package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.tile.TileRelay;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockRelay extends BlockNode {
    public BlockRelay() {
        super("relay");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileRelay();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            tryOpenNetworkGui(RSGui.RELAY, player, world, pos, side);
        }

        return true;
    }

    @Override
    @Nullable
    public Direction getDirection() {
        return null;
    }

    public boolean hasConnectivityState() {
        return true;
    }
}
