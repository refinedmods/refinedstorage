package com.raoulvdberge.refinedstorage.tile.config;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;

public interface IFilterable {
    int WHITELIST = 0;
    int BLACKLIST = 1;

    static <T extends TileEntity & INetworkNodeProxy> TileDataParameter<Integer, T> createParameter() {
        return new TileDataParameter<>(DataSerializers.VARINT, 0, t -> ((IFilterable) t.getNode()).getMode(), (t, v) -> {
            if (v == WHITELIST || v == BLACKLIST) {
                ((IFilterable) t.getNode()).setMode(v);
            }
        });
    }

    static boolean acceptsItem(IItemHandler filters, int mode, int compare, ItemStack stack) {
        if (mode == WHITELIST) {
            for (int i = 0; i < filters.getSlots(); ++i) {
                ItemStack slot = filters.getStackInSlot(i);

                if (API.instance().getComparer().isEqual(slot, stack, compare)) {
                    return true;
                }
            }

            return false;
        } else if (mode == BLACKLIST) {
            for (int i = 0; i < filters.getSlots(); ++i) {
                ItemStack slot = filters.getStackInSlot(i);

                if (API.instance().getComparer().isEqual(slot, stack, compare)) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    static boolean acceptsFluid(FluidInventory filters, int mode, int compare, FluidStack stack) {
        if (mode == WHITELIST) {
            for (int i = 0; i < filters.getSlots(); ++i) {
                FluidStack slot = filters.getFluid(i);

                if (slot != null && API.instance().getComparer().isEqual(slot, stack, compare)) {
                    return true;
                }
            }

            return false;
        } else if (mode == BLACKLIST) {
            for (int i = 0; i < filters.getSlots(); ++i) {
                FluidStack slot = filters.getFluid(i);

                if (slot != null && API.instance().getComparer().isEqual(slot, stack, compare)) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    void setMode(int mode);

    int getMode();
}
