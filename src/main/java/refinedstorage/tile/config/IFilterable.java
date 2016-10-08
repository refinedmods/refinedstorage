package refinedstorage.tile.config;

import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.apiimpl.API;
import refinedstorage.inventory.ItemHandlerFluid;
import refinedstorage.tile.data.ITileDataConsumer;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.TileDataParameter;

public interface IFilterable {
    int WHITELIST = 0;
    int BLACKLIST = 1;

    static <T extends TileEntity> TileDataParameter<Integer> createParameter() {
        return new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, T>() {
            @Override
            public Integer getValue(T tile) {
                return ((IFilterable) tile).getMode();
            }
        }, new ITileDataConsumer<Integer, T>() {
            @Override
            public void setValue(T tile, Integer value) {
                if (value == WHITELIST || value == BLACKLIST) {
                    ((IFilterable) tile).setMode(value);
                }
            }
        });
    }

    static boolean canTake(IItemHandler filters, int mode, int compare, ItemStack stack) {
        if (mode == WHITELIST) {
            int slots = 0;

            for (int i = 0; i < filters.getSlots(); ++i) {
                ItemStack slot = filters.getStackInSlot(i);

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
                ItemStack slot = filters.getStackInSlot(i);

                if (slot != null && API.instance().getComparer().isEqual(slot, stack, compare)) {
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
            if (filter.getStackInSlot(i) != null) {
                return false;
            }
        }
        return true;
    }

    void setMode(int mode);

    int getMode();
}
