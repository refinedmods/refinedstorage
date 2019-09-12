package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCacheListener;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.List;

public class StorageCacheListenerGridItem implements IStorageCacheListener<ItemStack> {
    private ServerPlayerEntity player;
    private INetwork network;

    public StorageCacheListenerGridItem(ServerPlayerEntity player, INetwork network) {
        this.player = player;
        this.network = network;
    }

    @Override
    public void onAttached() {
        // TODO RS.INSTANCE.network.sendTo(new MessageGridItemUpdate(network, network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player)), player);
    }

    @Override
    public void onInvalidated() {
        // NO OP
    }

    @Override
    public void onChanged(@Nonnull ItemStack stack, int size) {
        // TODO RS.INSTANCE.network.sendTo(new MessageGridItemDelta(network, network.getItemStorageTracker(), stack, size), player);
    }

    @Override
    public void onChangedBulk(@Nonnull List<Pair<ItemStack, Integer>> stacks) {
        // TODO RS.INSTANCE.network.sendTo(new MessageGridItemDelta(network, network.getItemStorageTracker(), stacks), player);
    }
}
