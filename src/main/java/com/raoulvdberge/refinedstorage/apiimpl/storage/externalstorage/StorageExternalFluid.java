package com.raoulvdberge.refinedstorage.apiimpl.storage.externalstorage;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IExternalStorageContext;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IStorageExternal;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class StorageExternalFluid implements IStorageExternal<FluidStack> {
    private IExternalStorageContext context;
    private Supplier<IFluidHandler> handlerSupplier;
    private boolean connectedToInterface;
    private ExternalStorageCacheFluid cache = new ExternalStorageCacheFluid();

    public StorageExternalFluid(IExternalStorageContext context, Supplier<IFluidHandler> handlerSupplier, boolean connectedToInterface) {
        this.context = context;
        this.handlerSupplier = handlerSupplier;
        this.connectedToInterface = connectedToInterface;
    }

    public boolean isConnectedToInterface() {
        return connectedToInterface;
    }

    @Override
    public void update(INetwork network) {
        if (getAccessType() == AccessType.INSERT) {
            return;
        }

        cache.update(network, handlerSupplier.get());
    }

    @Override
    public int getCapacity() {
        IFluidHandler fluidHandler = handlerSupplier.get();

        if (fluidHandler != null) {
            int cap = 0;

            for (int i = 0; i < fluidHandler.getTanks(); ++i) {
                cap += fluidHandler.getTankCapacity(i);
            }

            return cap;
        }

        return 0;
    }

    @Override
    public Collection<FluidStack> getStacks() {
        IFluidHandler fluidHandler = handlerSupplier.get();

        if (fluidHandler != null) {
            List<FluidStack> fluids = new ArrayList<>();

            for (int i = 0; i < fluidHandler.getTanks(); ++i) {
                fluids.add(fluidHandler.getFluidInTank(i));
            }

            return fluids;
        }

        return Collections.emptyList();
    }

    @Nullable
    @Override
    public FluidStack insert(@Nonnull FluidStack stack, int size, Action action) {
        if (context.acceptsFluid(stack)) {
            int filled = handlerSupplier.get().fill(StackUtils.copy(stack, size), action == Action.PERFORM ? IFluidHandler.FluidAction.EXECUTE : IFluidHandler.FluidAction.SIMULATE);

            if (filled == size) {
                return null;
            }

            return StackUtils.copy(stack, size - filled);
        }

        return StackUtils.copy(stack, size);
    }

    @Nullable
    @Override
    public FluidStack extract(@Nonnull FluidStack stack, int size, int flags, Action action) {
        IFluidHandler handler = handlerSupplier.get();

        if (handler == null) {
            return null;
        }

        return handler.drain(StackUtils.copy(stack, size), action == Action.PERFORM ? IFluidHandler.FluidAction.EXECUTE : IFluidHandler.FluidAction.SIMULATE);
    }

    @Override
    public int getStored() {
        IFluidHandler fluidHandler = handlerSupplier.get();

        if (fluidHandler != null) {
            int stored = 0;

            for (int i = 0; i < fluidHandler.getTanks(); ++i) {
                stored += fluidHandler.getFluidInTank(i).getAmount();
            }

            return stored;
        }

        return 0;
    }

    @Override
    public int getPriority() {
        return context.getPriority();
    }

    @Override
    public AccessType getAccessType() {
        return context.getAccessType();
    }

    @Override
    public int getCacheDelta(int storedPreInsertion, int size, @Nullable FluidStack remainder) {
        if (getAccessType() == AccessType.INSERT) {
            return 0;
        }

        return remainder == null ? size : (size - remainder.getAmount());
    }
}
