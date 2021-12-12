package com.refinedmods.refinedstorage.tile.config;

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;

public interface IWhitelistBlacklist {
    int WHITELIST = 0;
    int BLACKLIST = 1;

    static <T extends TileEntity & INetworkNodeProxy<?>> TileDataParameter<Integer, T> createParameter() {
        return new TileDataParameter<>(DataSerializers.INT, 0, t -> ((IWhitelistBlacklist) t.getNode()).getWhitelistBlacklistMode(), (t, v) -> {
            if (v == WHITELIST || v == BLACKLIST) {
                ((IWhitelistBlacklist) t.getNode()).setWhitelistBlacklistMode(v);
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

                if (!slot.isEmpty() && API.instance().getComparer().isEqual(slot, stack, compare)) {
                    return true;
                }
            }

            return false;
        } else if (mode == BLACKLIST) {
            for (int i = 0; i < filters.getSlots(); ++i) {
                FluidStack slot = filters.getFluid(i);

                if (!slot.isEmpty() && API.instance().getComparer().isEqual(slot, stack, compare)) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    void setWhitelistBlacklistMode(int mode);

    int getWhitelistBlacklistMode();
}
