package com.raoulvdberge.refinedstorage.tile.externalstorage;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.fluid.IFluidStorage;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class FluidStorageExternal implements IFluidStorage {
    private FluidStack cache;

    private TileExternalStorage externalStorage;
    private IFluidHandler handler;

    public FluidStorageExternal(TileExternalStorage externalStorage, IFluidHandler handler, IFluidTankProperties properties) {
        this.externalStorage = externalStorage;
        this.handler = handler;
    }

    private IFluidTankProperties getProperties() {
        return handler.getTankProperties().length != 0 ? handler.getTankProperties()[0] : null;
    }

    private FluidStack getContents() {
        return getProperties() == null ? null : getProperties().getContents();
    }

    @Override
    public List<FluidStack> getStacks() {
        return getContents() == null ? Collections.emptyList() : Collections.singletonList(getContents().copy());
    }

    @Nullable
    @Override
    public FluidStack insertFluid(@Nonnull FluidStack stack, int size, boolean simulate) {
        if (getProperties() != null && IFilterable.canTakeFluids(externalStorage.getFluidFilters(), externalStorage.getMode(), externalStorage.getCompare(), stack) && getProperties().canFillFluidType(stack)) {
            int filled = handler.fill(RSUtils.copyStackWithSize(stack, size), !simulate);

            if (filled == size) {
                return null;
            }

            return RSUtils.copyStackWithSize(stack, size - filled);
        }

        return RSUtils.copyStackWithSize(stack, size);
    }

    @Nullable
    @Override
    public FluidStack extractFluid(@Nonnull FluidStack stack, int size, int flags, boolean simulate) {
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
