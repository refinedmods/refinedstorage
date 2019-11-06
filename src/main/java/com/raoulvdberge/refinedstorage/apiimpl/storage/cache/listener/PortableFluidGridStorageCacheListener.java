package com.raoulvdberge.refinedstorage.apiimpl.storage.cache.listener;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.storage.cache.IStorageCacheListener;
import com.raoulvdberge.refinedstorage.api.util.StackListResult;
import com.raoulvdberge.refinedstorage.network.grid.PortableGridFluidDeltaMessage;
import com.raoulvdberge.refinedstorage.network.grid.PortableGridFluidUpdateMessage;
import com.raoulvdberge.refinedstorage.tile.grid.portable.IPortableGrid;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class PortableFluidGridStorageCacheListener implements IStorageCacheListener<FluidStack> {
    private IPortableGrid portableGrid;
    private ServerPlayerEntity player;

    public PortableFluidGridStorageCacheListener(IPortableGrid portableGrid, ServerPlayerEntity player) {
        this.portableGrid = portableGrid;
        this.player = player;
    }

    @Override
    public void onAttached() {
        RS.NETWORK_HANDLER.sendTo(player, new PortableGridFluidUpdateMessage(portableGrid));
    }

    @Override
    public void onInvalidated() {
        // NO OP
    }

    @Override
    public void onChanged(StackListResult<FluidStack> delta) {
        List<StackListResult<FluidStack>> deltas = new ArrayList<>();

        deltas.add(delta);

        onChangedBulk(deltas);
    }

    @Override
    public void onChangedBulk(List<StackListResult<FluidStack>> storageCacheDeltas) {
        RS.NETWORK_HANDLER.sendTo(player, new PortableGridFluidDeltaMessage(portableGrid, storageCacheDeltas));
    }
}
