package com.refinedmods.refinedstorage.apiimpl.storage.cache.listener;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCacheListener;
import com.refinedmods.refinedstorage.api.util.StackListResult;
import com.refinedmods.refinedstorage.network.grid.GridItemDeltaMessage;
import com.refinedmods.refinedstorage.network.grid.GridItemUpdateMessage;
import com.refinedmods.refinedstorage.network.grid.GridStackDelta;
import com.refinedmods.refinedstorage.screen.grid.stack.ItemGridStack;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ItemGridStorageCacheListener implements IStorageCacheListener<ItemStack> {
    private final ServerPlayer player;
    private final INetwork network;

    public ItemGridStorageCacheListener(ServerPlayer player, INetwork network) {
        this.player = player;
        this.network = network;
    }

    @Override
    public void onAttached() {
        RS.NETWORK_HANDLER.sendTo(player, new GridItemUpdateMessage(
            network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player),
            (List<ItemGridStack>) network.getItemStorageCache().getGridStacks()
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
                    network.getItemStorageCache(),
                    network.getItemStorageCache().getCraftablesList(),
                    network.getItemStorageTracker(),
                    delta
                )
            )).toList();
        RS.NETWORK_HANDLER.sendTo(player, new GridItemDeltaMessage(syncDeltas));
    }
}
