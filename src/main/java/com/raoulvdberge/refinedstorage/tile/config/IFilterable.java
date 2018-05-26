package com.raoulvdberge.refinedstorage.tile.config;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFluid;
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

    // @todo: Change in 1.13 to be by default blacklist, and accept all on blacklist and none on whitelist when no filter is set
    static boolean canTake(IItemHandler filters, int mode, int compare, ItemStack stack) {
        if (mode == WHITELIST) {
            int slots = 0;

            for (int i = 0; i < filters.getSlots(); ++i) {
                ItemStack slot = filters.getStackInSlot(i);

                if (!slot.isEmpty()) {
                    slots++;

                    if (API.instance().getComparer().isEqual(slot, stack, compare)) {
                        return true;
                    }
                }
            }

            return slots == 0;
        } else if (mode == BLACKLIST) {
            for (int i = 0; i < filters.getSlots(); ++i) {
                ItemStack slot = filters.getStackInSlot(i);

                if (!slot.isEmpty() && API.instance().getComparer().isEqual(slot, stack, compare)) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    static boolean canTakeFluids(ItemHandlerFluid filters, int mode, int compare, FluidStack stack) {
        if (mode == WHITELIST) {
            int slots = 0;

            for (int i = 0; i < filters.getSlots(); ++i) {
                FluidStack slot = filters.getFluidStackInSlot(i);

                if (slot != null) {
                    slots++;

                    if (API.instance().getComparer().isEqual(slot, stack, compare)) {
                        return true;
                    }
                }
            }

            return slots == 0;
        } else if (mode == BLACKLIST) {
            for (int i = 0; i < filters.getSlots(); ++i) {
                FluidStack slot = filters.getFluidStackInSlot(i);

                if (slot != null && API.instance().getComparer().isEqual(slot, stack, compare)) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    static boolean isEmpty(IItemHandler filter) {
        for (int i = 0; i < filter.getSlots(); i++) {
            if (!filter.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    void setMode(int mode);

    int getMode();
}
