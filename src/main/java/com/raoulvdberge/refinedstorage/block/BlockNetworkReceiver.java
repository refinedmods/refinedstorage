package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.tile.TileNetworkReceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockNetworkReceiver extends BlockNode {
    public BlockNetworkReceiver() {
        super("network_receiver");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileNetworkReceiver();
    }

    @Override
    public EnumPlacementType getPlacementType() {
        return null;
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }
}
