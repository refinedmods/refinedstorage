package com.refinedmods.refinedstorage.apiimpl.storage.cache.listener;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCacheListener;
import com.refinedmods.refinedstorage.api.util.StackListResult;
import com.refinedmods.refinedstorage.network.grid.GridItemDeltaMessage;
import com.refinedmods.refinedstorage.network.grid.GridItemUpdateMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemGridStorageCacheListener implements IStorageCacheListener<ItemStack> {
    private ServerPlayerEntity player;
    private INetwork network;

    public ItemGridStorageCacheListener(ServerPlayerEntity player, INetwork network) {
        this.player = player;
        this.network = network;
    }

    @Override
    public void onAttached() {
        RS.NETWORK_HANDLER.sendTo(player, new GridItemUpdateMessage(network, network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player)));
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
        RS.NETWORK_HANDLER.sendTo(player, new GridItemDeltaMessage(network, deltas));
    }
}
