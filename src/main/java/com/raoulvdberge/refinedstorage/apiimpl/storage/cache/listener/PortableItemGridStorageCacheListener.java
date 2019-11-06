package com.raoulvdberge.refinedstorage.apiimpl.storage.cache.listener;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.storage.cache.IStorageCacheListener;
import com.raoulvdberge.refinedstorage.api.util.StackListResult;
import com.raoulvdberge.refinedstorage.network.grid.PortableGridItemDeltaMessage;
import com.raoulvdberge.refinedstorage.network.grid.PortableGridItemUpdateMessage;
import com.raoulvdberge.refinedstorage.tile.grid.portable.IPortableGrid;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PortableItemGridStorageCacheListener implements IStorageCacheListener<ItemStack> {
    private IPortableGrid portableGrid;
    private ServerPlayerEntity player;

    public PortableItemGridStorageCacheListener(IPortableGrid portableGrid, ServerPlayerEntity player) {
        this.portableGrid = portableGrid;
        this.player = player;
    }

    @Override
    public void onAttached() {
        RS.NETWORK_HANDLER.sendTo(player, new PortableGridItemUpdateMessage(portableGrid));
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
    public void onChangedBulk(List<StackListResult<ItemStack>> storageCacheDeltas) {
        RS.NETWORK_HANDLER.sendTo(player, new PortableGridItemDeltaMessage(portableGrid, storageCacheDeltas));
    }
}
