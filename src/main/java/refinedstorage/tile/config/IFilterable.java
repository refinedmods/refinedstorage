package refinedstorage.tile.config;

import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.api.storage.CompareUtils;
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

                    if (CompareUtils.compareStack(slot, stack, compare)) {
                        return true;
                    }
                }
            }

            return slots == 0;
        } else if (mode == BLACKLIST) {
            for (int i = 0; i < filters.getSlots(); ++i) {
                ItemStack slot = filters.getStackInSlot(i);

                if (slot != null && CompareUtils.compareStack(slot, stack, compare)) {
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
