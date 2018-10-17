package com.raoulvdberge.refinedstorage.apiimpl.storage.externalstorage;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IExternalStorageContext;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IStorageExternal;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

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
    private List<FluidStack> cache;
    private boolean connectedToInterface;

    public StorageExternalFluid(IExternalStorageContext context, Supplier<IFluidHandler> handlerSupplier, boolean connectedToInterface) {
        this.context = context;
        this.handlerSupplier = handlerSupplier;
        this.connectedToInterface = connectedToInterface;
    }

    public boolean isConnectedToInterface() {
        return connectedToInterface;
    }

    @Nullable
    private IFluidTankProperties[] getProperties() {
        IFluidHandler handler = handlerSupplier.get();

        return (handler != null && handler.getTankProperties() != null && handler.getTankProperties().length != 0) ? handler.getTankProperties() : null;
    }

    @Override
    public void update(INetwork network) {
        // If we are insert only, we don't care about sending changes
        if (getAccessType() == AccessType.INSERT) {
            return;
        }

        if (cache == null) {
            cache = new ArrayList<>(getStacksWithNulls());

            return;
        }

        List<FluidStack> newStacks = new ArrayList<>(getStacksWithNulls());

        for (int i = 0; i < newStacks.size(); ++i) {
            FluidStack actual = newStacks.get(i);

            // If we exceed the cache size, than that means this item is added
            if (i >= cache.size()) {
                if (actual != null) {
                    network.getFluidStorageCache().add(actual, actual.amount, false, true);
                }

                continue;
            }

            FluidStack cached = cache.get(i);

            if (actual == null && cached == null) {
                // NO OP
            } else if (actual == null && cached != null) {
                network.getFluidStorageCache().remove(cached, cached.amount, true);
            } else if (actual != null && cached == null) {
                network.getFluidStorageCache().add(actual, actual.amount, false, true);
            } else if (!API.instance().getComparer().isEqual(actual, cached, IComparer.COMPARE_NBT)) {
                network.getFluidStorageCache().remove(cached, cached.amount, true);
                network.getFluidStorageCache().add(actual, actual.amount, false, true);
            } else if (actual.amount > cached.amount) {
                network.getFluidStorageCache().add(actual, actual.amount - cached.amount, false, true);
            } else if (actual.amount < cached.amount) {
                network.getFluidStorageCache().remove(actual, cached.amount - actual.amount, true);
            }
        }

        if (cache.size() > newStacks.size()) {
            for (int i = newStacks.size(); i < cache.size(); ++i) {
                if (cache.get(i) != null) {
                    network.getFluidStorageCache().remove(cache.get(i), cache.get(i).amount, true);
                }
            }
        }

        this.cache = newStacks;

        network.getFluidStorageCache().flush();
    }

    @Override
    public int getCapacity() {
        IFluidTankProperties[] props = getProperties();

        if (props != null) {
            int cap = 0;

            for (IFluidTankProperties properties : props) {
                cap += properties.getCapacity();
            }

            return cap;
        }

        return 0;
    }

    @Override
    public Collection<FluidStack> getStacks() {
        IFluidTankProperties[] props = getProperties();

        if (props != null) {
            List<FluidStack> fluids = new ArrayList<>();

            for (IFluidTankProperties properties : props) {
                FluidStack stack = properties.getContents();

                if (stack != null) {
                    fluids.add(stack.copy());
                }
            }

            return fluids;
        }

        return Collections.emptyList();
    }

    private Collection<FluidStack> getStacksWithNulls() {
        IFluidTankProperties[] props = getProperties();

        if (props != null) {
            List<FluidStack> fluids = new ArrayList<>();

            for (IFluidTankProperties properties : props) {
                FluidStack stack = properties.getContents();

                if (stack != null) {
                    fluids.add(stack.copy());
                } else {
                    fluids.add(null);
                }
            }

            return fluids;
        }

        return Collections.emptyList();
    }

    @Nullable
    @Override
    public FluidStack insert(@Nonnull FluidStack stack, int size, Action action) {
        if (context.acceptsFluid(stack)) {
            int filled = handlerSupplier.get().fill(StackUtils.copy(stack, size), action == Action.PERFORM);

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

        return handler.drain(StackUtils.copy(stack, size), action == Action.PERFORM);
    }

    @Override
    public int getStored() {
        IFluidTankProperties[] props = getProperties();

        if (props != null) {
            int stored = 0;

            for (IFluidTankProperties properties : props) {
                FluidStack contents = properties.getContents();

                if (contents != null) {
                    stored += contents.amount;
                }
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

        return remainder == null ? size : (size - remainder.amount);
    }
}
