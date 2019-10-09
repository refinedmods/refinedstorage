package com.raoulvdberge.refinedstorage.apiimpl.storage.cache.listener;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.cache.IStorageCacheListener;
import com.raoulvdberge.refinedstorage.api.util.StackListResult;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class FluidGridStorageCacheListener implements IStorageCacheListener<FluidStack> {
    private ServerPlayerEntity player;
    private INetwork network;

    public FluidGridStorageCacheListener(ServerPlayerEntity player, INetwork network) {
        this.player = player;
        this.network = network;
    }

    @Override
    public void onAttached() {
        // TODO: RS.INSTANCE.network.sendTo(new MessageGridFluidUpdate(network, network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player)), player);
    }

    @Override
    public void onInvalidated() {
        // NO OP
    }

    @Override
    public void onChanged(StackListResult<FluidStack> delta) {
        // TODO: RS.INSTANCE.network.sendTo(new MessageGridFluidUpdate(network, network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player)), player);

    }

    @Override
    public void onChangedBulk(List<StackListResult<FluidStack>> storageCacheDeltas) {
        /* TODO
        for (Pair<FluidStack, Integer> stack : stacks) {
                    onChanged(stack.getLeft(), stack.getRight());
                }
         */
    }
}
