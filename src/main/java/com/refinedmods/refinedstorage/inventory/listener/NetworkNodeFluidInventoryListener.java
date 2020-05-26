package com.refinedmods.refinedstorage.inventory.listener;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;

public class NetworkNodeFluidInventoryListener implements InventoryListener<FluidInventory> {
    private INetworkNode node;

    public NetworkNodeFluidInventoryListener(INetworkNode node) {
        this.node = node;
    }

    @Override
    public void onChanged(FluidInventory handler, int slot, boolean reading) {
        if (!reading) {
            node.markDirty();
        }
    }
}
