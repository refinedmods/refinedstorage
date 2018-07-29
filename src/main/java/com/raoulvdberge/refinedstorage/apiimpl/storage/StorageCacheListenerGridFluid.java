package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCacheListener;
import com.raoulvdberge.refinedstorage.network.MessageGridFluidDelta;
import com.raoulvdberge.refinedstorage.network.MessageGridFluidUpdate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class StorageCacheListenerGridFluid implements IStorageCacheListener<FluidStack> {
    private EntityPlayerMP player;
    private INetwork network;

    public StorageCacheListenerGridFluid(EntityPlayerMP player, INetwork network) {
        this.player = player;
        this.network = network;
    }

    @Override
    public void onAttached() {
        RS.INSTANCE.network.sendTo(new MessageGridFluidUpdate(network, network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player)), player);
    }

    @Override
    public void onInvalidated() {
        // NO OP
    }

    @Override
    public void onChanged(@Nonnull FluidStack stack, int size) {
        RS.INSTANCE.network.sendTo(new MessageGridFluidDelta(network, network.getFluidStorageTracker(), stack, size), player);
    }
}
