package com.refinedmods.refinedstorage.apiimpl.storage.cache.listener;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCacheListener;
import com.refinedmods.refinedstorage.api.util.StackListResult;
import com.refinedmods.refinedstorage.blockentity.grid.portable.IPortableGrid;
import com.refinedmods.refinedstorage.network.grid.GridFluidDeltaMessage;
import com.refinedmods.refinedstorage.network.grid.GridFluidUpdateMessage;
import com.refinedmods.refinedstorage.network.grid.GridItemDeltaMessage;
import com.refinedmods.refinedstorage.network.grid.GridItemUpdateMessage;
import com.refinedmods.refinedstorage.network.grid.GridStackDelta;
import com.refinedmods.refinedstorage.screen.grid.stack.FluidGridStack;
import com.refinedmods.refinedstorage.screen.grid.stack.ItemGridStack;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.fluids.FluidStack;
import java.util.ArrayList;
import java.util.List;

public class PortableFluidGridStorageCacheListener implements IStorageCacheListener<FluidStack> {
    private final IPortableGrid portableGrid;
    private final ServerPlayer player;

    public PortableFluidGridStorageCacheListener(IPortableGrid portableGrid, ServerPlayer player) {
        this.portableGrid = portableGrid;
        this.player = player;
    }

    @Override
    public void onAttached() {
        RS.NETWORK_HANDLER.sendTo(player, new GridFluidUpdateMessage(
            false,
            (List<FluidGridStack>) portableGrid.getFluidCache().getGridStacks()
        ));
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
    public void onChangedBulk(List<StackListResult<FluidStack>> deltas) {
        final List<GridStackDelta<FluidGridStack>> syncDeltas = deltas.stream()
            .map(delta -> new GridStackDelta<>(
                delta.getChange(),
                FluidGridStack.of(
                    portableGrid.getFluidCache(),
                    null,
                    portableGrid.getFluidStorageTracker(),
                    delta
                )
            )).toList();
        RS.NETWORK_HANDLER.sendTo(player, new GridFluidDeltaMessage(syncDeltas));
    }
}
