package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCacheListener;
import com.raoulvdberge.refinedstorage.network.grid.GridItemDeltaMessage;
import com.raoulvdberge.refinedstorage.network.grid.GridItemUpdateMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
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
        RS.NETWORK_HANDLER.sendTo(player, new GridItemUpdateMessage(network, network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player)));
    }

    @Override
    public void onInvalidated() {
        // NO OP
    }

    @Override
    public void onChanged(@Nonnull ItemStack stack, int size) {
        List<Pair<ItemStack, Integer>> deltas = new ArrayList<>();

        deltas.add(Pair.of(stack, size));

        onChangedBulk(deltas);
    }

    @Override
    public void onChangedBulk(@Nonnull List<Pair<ItemStack, Integer>> stacks) {
        RS.NETWORK_HANDLER.sendTo(player, new GridItemDeltaMessage(network, stacks));
    }
}
