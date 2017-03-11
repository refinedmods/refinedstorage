package com.raoulvdberge.refinedstorage.tile.config;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFluid;
import com.raoulvdberge.refinedstorage.item.filter.ItemFilter;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataConsumer;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;

public interface IFilterable {
    int WHITELIST = 0;
    int BLACKLIST = 1;

    static <T extends TileEntity & INetworkNodeProxy> TileDataParameter<Integer> createParameter() {
        return new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, T>() {
            @Override
            public Integer getValue(T tile) {
                return ((IFilterable) tile.getNode()).getMode();
            }
        }, new ITileDataConsumer<Integer, T>() {
            @Override
            public void setValue(T tile, Integer value) {
                if (value == WHITELIST || value == BLACKLIST) {
                    ((IFilterable) tile.getNode()).setMode(value);
                }
            }
        });
    }

    static boolean canTake(IItemHandler filters, int mode, int compare, ItemStack stack) {
        if (mode == WHITELIST) {
            int slots = 0;

            for (int i = 0; i < filters.getSlots(); ++i) {
                ItemStack slot = filters.getStackInSlot(i);

                if (!slot.isEmpty()) {
                    slots++;

                    if (slot.getItem() == RSItems.FILTER) {
                        for (ItemStack slotInFilter : ItemFilter.getFilterItemsFromCache(slot)) {
                            if (!slotInFilter.isEmpty() && API.instance().getComparer().isEqual(slotInFilter, stack, compare)) {
                                return true;
                            }
                        }
                    } else if (API.instance().getComparer().isEqual(slot, stack, compare)) {
                        return true;
                    }
                }
            }

            return slots == 0;
        } else if (mode == BLACKLIST) {
            for (int i = 0; i < filters.getSlots(); ++i) {
                ItemStack slot = filters.getStackInSlot(i);

                if (!slot.isEmpty()) {
                    if (slot.getItem() == RSItems.FILTER) {
                        for (ItemStack slotInFilter : ItemFilter.getFilterItemsFromCache(slot)) {
                            if (!slotInFilter.isEmpty() && API.instance().getComparer().isEqual(slotInFilter, stack, compare)) {
                                return false;
                            }
                        }
                    } else if (API.instance().getComparer().isEqual(slot, stack, compare)) {
                        return false;
                    }
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
