package com.raoulvdberge.refinedstorage.apiimpl.storage.externalstorage;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

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

            for (int i = 0; i < handler.getTanks(); ++i) {
                cache.add(handler.getFluidInTank(i));
            }

            return;
        }

        for (int i = 0; i < handler.getTanks(); ++i) {
            FluidStack actual = handler.getFluidInTank(i);

            if (i >= cache.size()) { // ENLARGED
                if (actual != null) {
                    network.getFluidStorageCache().add(actual, actual.getAmount(), false, true);

                    cache.add(actual.copy());
                }

                continue;
            }

            FluidStack cached = cache.get(i);

            if (actual == null && cached == null) { // NONE
                continue;
            }

            if (actual == null && cached != null) { // REMOVED
                network.getFluidStorageCache().remove(cached, cached.getAmount(), true);

                cache.set(i, null);
            } else if (actual != null && cached == null) { // ADDED
                network.getFluidStorageCache().add(actual, actual.getAmount(), false, true);

                cache.set(i, actual.copy());
            } else if (!API.instance().getComparer().isEqual(actual, cached, IComparer.COMPARE_NBT)) { // CHANGED
                network.getFluidStorageCache().remove(cached, cached.getAmount(), true);
                network.getFluidStorageCache().add(actual, actual.getAmount(), false, true);

                cache.set(i, actual.copy());
            } else if (actual.getAmount() > cached.getAmount()) { // COUNT_CHANGED
                network.getFluidStorageCache().add(actual, actual.getAmount() - cached.getAmount(), false, true);

                cached.setAmount(actual.getAmount());
            } else if (actual.getAmount() < cached.getAmount()) { // COUNT_CHANGED
                network.getFluidStorageCache().remove(actual, cached.getAmount() - actual.getAmount(), true);

                cached.setAmount(actual.getAmount());
            }
        }

        if (cache.size() > handler.getTanks()) { // SHRUNK
            for (int i = cache.size() - 1; i >= handler.getTanks(); --i) { // Reverse order for the remove call.
                FluidStack cached = cache.get(i);

                if (cached != null) {
                    network.getFluidStorageCache().remove(cached, cached.getAmount(), true);
                }

                cache.remove(i);
            }
        }

        network.getFluidStorageCache().flush();
    }
}
