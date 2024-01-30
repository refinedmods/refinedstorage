package com.refinedmods.refinedstorage.apiimpl.storage.cache.listener;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCacheListener;
import com.refinedmods.refinedstorage.api.util.StackListResult;
import com.refinedmods.refinedstorage.network.grid.GridFluidDeltaMessage;
import com.refinedmods.refinedstorage.network.grid.GridFluidUpdateMessage;
import com.refinedmods.refinedstorage.network.grid.GridItemDeltaMessage;
import com.refinedmods.refinedstorage.network.grid.GridStackDelta;
import com.refinedmods.refinedstorage.screen.grid.stack.FluidGridStack;
import com.refinedmods.refinedstorage.screen.grid.stack.ItemGridStack;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidGridStorageCacheListener implements IStorageCacheListener<FluidStack> {
    private final ServerPlayer player;
    private final INetwork network;

    public FluidGridStorageCacheListener(ServerPlayer player, INetwork network) {
        this.player = player;
        this.network = network;
    }

    @Override
    public void onAttached() {
        RS.NETWORK_HANDLER.sendTo(player, new GridFluidUpdateMessage(
            network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player),
            (List<FluidGridStack>) network.getFluidStorageCache().getGridStacks()
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
                    network.getFluidStorageCache(),
                    network.getFluidStorageCache().getCraftablesList(),
                    network.getFluidStorageTracker(),
                    delta
                )
            )).toList();
        RS.NETWORK_HANDLER.sendTo(player, new GridFluidDeltaMessage(syncDeltas));
    }
}
