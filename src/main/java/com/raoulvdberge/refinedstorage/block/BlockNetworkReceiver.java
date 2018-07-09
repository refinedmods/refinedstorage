package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.tile.TileNetworkReceiver;

public class BlockNetworkReceiver extends BlockNode {
    public BlockNetworkReceiver() {
        super(BlockInfoBuilder.forId("network_receiver").tileEntity(TileNetworkReceiver::new).create());
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }
}
