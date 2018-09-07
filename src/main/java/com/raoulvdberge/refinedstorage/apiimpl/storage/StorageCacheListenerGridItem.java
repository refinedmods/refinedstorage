package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCacheListener;
import com.raoulvdberge.refinedstorage.network.MessageGridItemDelta;
import com.raoulvdberge.refinedstorage.network.MessageGridItemUpdate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.List;

public class StorageCacheListenerGridItem implements IStorageCacheListener<ItemStack> {
    private EntityPlayerMP player;
    private INetwork network;

    public StorageCacheListenerGridItem(EntityPlayerMP player, INetwork network) {
        this.player = player;
        this.network = network;
    }

    @Override
    public void onAttached() {
        RS.INSTANCE.network.sendTo(new MessageGridItemUpdate(network, network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player)), player);
    }

    @Override
    public void onInvalidated() {
        // NO OP
    }

    @Override
    public void onChanged(@Nonnull ItemStack stack, int size) {
        RS.INSTANCE.network.sendTo(new MessageGridItemDelta(network, network.getItemStorageTracker(), stack, size), player);
    }

    @Override
    public void onChangedBulk(@Nonnull List<Pair<ItemStack, Integer>> stacks)
    {
        RS.INSTANCE.network.sendTo(new MessageGridItemDelta(network, network.getItemStorageTracker(), stacks), player);
    }
}
