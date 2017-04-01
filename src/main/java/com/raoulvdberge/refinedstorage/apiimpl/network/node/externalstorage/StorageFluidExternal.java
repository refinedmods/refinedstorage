package com.raoulvdberge.refinedstorage.apiimpl.network.node.externalstorage;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

public class StorageFluidExternal implements IStorage<FluidStack> {
    private FluidStack cache;

    private NetworkNodeExternalStorage externalStorage;
    private Supplier<IFluidHandler> handlerSupplier;

    public StorageFluidExternal(NetworkNodeExternalStorage externalStorage, Supplier<IFluidHandler> handlerSupplier) {
        this.externalStorage = externalStorage;
        this.handlerSupplier = handlerSupplier;
    }

    private IFluidTankProperties getProperties() {
        IFluidHandler handler = handlerSupplier.get();

        return (handler != null && handler.getTankProperties() != null && handler.getTankProperties().length != 0) ? handler.getTankProperties()[0] : null;
    }

    private FluidStack getContents() {
        return getProperties() == null ? null : getProperties().getContents();
    }

    @Override
    public Collection<FluidStack> getStacks() {
        return getContents() == null ? Collections.emptyList() : Collections.singletonList(getContents().copy());
    }

    @Override
    @Nullable
    public FluidStack insert(@Nonnull FluidStack stack, int size, boolean simulate) {
        if (getProperties() != null && IFilterable.canTakeFluids(externalStorage.getFluidFilters(), externalStorage.getMode(), externalStorage.getCompare(), stack) && getProperties().canFillFluidType(stack)) {
            int filled = handlerSupplier.get().fill(RSUtils.copyStackWithSize(stack, size), !simulate);

            if (filled == size) {
                return null;
            }

            return RSUtils.copyStackWithSize(stack, size - filled);
        }

        return RSUtils.copyStackWithSize(stack, size);
    }

    @Override
    @Nullable
    public FluidStack extract(@Nonnull FluidStack stack, int size, int flags, boolean simulate) {
        IFluidHandler handler = handlerSupplier.get();

        if (handler == null) {
            return null;
        }

        FluidStack toDrain = RSUtils.copyStackWithSize(stack, size);

        if (API.instance().getComparer().isEqual(getContents(), toDrain, flags)) {
            return handler.drain(toDrain, !simulate);
        }

        return null;
    }

    @Override
    public int getStored() {
        return getContents() != null ? getContents().amount : 0;
    }

    public int getCapacity() {
        return getProperties() != null ? getProperties().getCapacity() : 0;
    }

    @Override
    public int getPriority() {
        return externalStorage.getPriority();
    }

    public AccessType getAccessType() {
        return externalStorage.getAccessType();
    }

    @Override
    public int getCacheDelta(int storedPreInsertion, int size, @Nullable FluidStack remainder) {
        if (getAccessType() == AccessType.INSERT) {
            return 0;
        }

        return remainder == null ? size : (size - remainder.amount);
    }

    public boolean updateCache() {
        FluidStack stack = getContents();

        if (cache == null) {
            cache = RSUtils.copyStack(stack);
        } else if (!API.instance().getComparer().isEqual(stack, cache, IComparer.COMPARE_NBT | IComparer.COMPARE_QUANTITY)) {
            cache = RSUtils.copyStack(stack);

            return true;
        }

        return false;
    }

    public void updateCacheForcefully() {
        cache = RSUtils.copyStack(getContents());
    }
}
