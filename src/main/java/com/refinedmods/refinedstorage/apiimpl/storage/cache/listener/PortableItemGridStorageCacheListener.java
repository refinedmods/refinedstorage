package com.refinedmods.refinedstorage.apiimpl.storage.cache.listener;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCacheListener;
import com.refinedmods.refinedstorage.api.util.StackListResult;
import com.refinedmods.refinedstorage.network.grid.GridItemDeltaMessage;
import com.refinedmods.refinedstorage.network.grid.GridItemUpdateMessage;
import com.refinedmods.refinedstorage.blockentity.grid.portable.IPortableGrid;
import com.refinedmods.refinedstorage.network.grid.GridStackDelta;
import com.refinedmods.refinedstorage.screen.grid.stack.ItemGridStack;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PortableItemGridStorageCacheListener implements IStorageCacheListener<ItemStack> {
    private final IPortableGrid portableGrid;
    private final ServerPlayer player;

    public PortableItemGridStorageCacheListener(IPortableGrid portableGrid, ServerPlayer player) {
        this.portableGrid = portableGrid;
        this.player = player;
    }

    @Override
    public void onAttached() {
        RS.NETWORK_HANDLER.sendTo(player, new GridItemUpdateMessage(
            false,
            (List<ItemGridStack>) portableGrid.getItemCache().getGridStacks()
        ));
    }

    @Override
    public void onInvalidated() {
        // NO OP
    }

    @Override
    public void onChanged(StackListResult<ItemStack> delta) {
        List<StackListResult<ItemStack>> deltas = new ArrayList<>();
        deltas.add(delta);
        onChangedBulk(deltas);
    }

    @Override
    public void onChangedBulk(List<StackListResult<ItemStack>> deltas) {
        final List<GridStackDelta<ItemGridStack>> syncDeltas = deltas.stream()
            .map(delta -> new GridStackDelta<>(
                delta.getChange(),
                ItemGridStack.of(
                    portableGrid.getItemCache(),
                    null,
                    portableGrid.getItemStorageTracker(),
                    delta
                )
            )).toList();
        RS.NETWORK_HANDLER.sendTo(player, new GridItemDeltaMessage(syncDeltas));
    }
}
