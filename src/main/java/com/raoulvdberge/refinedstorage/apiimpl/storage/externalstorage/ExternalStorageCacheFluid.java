package com.raoulvdberge.refinedstorage.apiimpl.storage.externalstorage;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ExternalStorageCacheFluid {
    private List<FluidStack> cache;

    public void update(INetwork network, @Nullable IFluidHandler handler) {
        if (handler == null) {
            return;
        }

        if (cache == null) {
            cache = new ArrayList<>();

            for (IFluidTankProperties properties : handler.getTankProperties()) {
                cache.add(properties.getContents() == null ? null : properties.getContents().copy());
            }

            return;
        }

        for (int i = 0; i < handler.getTankProperties().length; ++i) {
            FluidStack actual = handler.getTankProperties()[i].getContents();

            if (i >= cache.size()) { // ENLARGED
                if (actual != null) {
                    network.getFluidStorageCache().add(actual, actual.amount, false, true);

                    cache.add(actual.copy());
                }

                continue;
            }

            FluidStack cached = cache.get(i);

            if (actual == null && cached == null) { // NONE
                continue;
            }

            if (actual == null && cached != null) { // REMOVED
                network.getFluidStorageCache().remove(cached, cached.amount, true);

                cache.set(i, null);
            } else if (actual != null && cached == null) { // ADDED
                network.getFluidStorageCache().add(actual, actual.amount, false, true);

                cache.set(i, actual.copy());
            } else if (!API.instance().getComparer().isEqual(actual, cached, IComparer.COMPARE_NBT)) { // CHANGED
                network.getFluidStorageCache().remove(cached, cached.amount, true);
                network.getFluidStorageCache().add(actual, actual.amount, false, true);

                cache.set(i, actual.copy());
            } else if (actual.amount > cached.amount) { // COUNT_CHANGED
                network.getFluidStorageCache().add(actual, actual.amount - cached.amount, false, true);

                cached.amount = actual.amount;
            } else if (actual.amount < cached.amount) { // COUNT_CHANGED
                network.getFluidStorageCache().remove(actual, cached.amount - actual.amount, true);

                cached.amount = actual.amount;
            }
        }

        if (cache.size() > handler.getTankProperties().length) { // SHRUNK
            for (int i = cache.size() - 1; i >= handler.getTankProperties().length; --i) { // Reverse order for the remove call.
                FluidStack cached = cache.get(i);

                if (cached != null) {
                    network.getFluidStorageCache().remove(cached, cached.amount, true);
                }

                cache.remove(i);
            }
        }

        network.getFluidStorageCache().flush();
    }
}
