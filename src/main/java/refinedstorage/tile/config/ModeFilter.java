package refinedstorage.tile.config;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageUtils;

public class ModeFilter {
    public static boolean violatesMode(IInventory filters, IModeConfig mode, int compare, ItemStack stack) {
        if (mode.isWhitelist()) {
            int slots = 0;

            for (int i = 0; i < filters.getSizeInventory(); ++i) {
                ItemStack slot = filters.getStackInSlot(i);

                if (slot != null) {
                    slots++;

                    if (RefinedStorageUtils.compareStack(slot, stack, compare)) {
                        return true;
                    }
                }
            }

            return slots == 0;
        } else if (mode.isBlacklist()) {
            for (int i = 0; i < filters.getSizeInventory(); ++i) {
                ItemStack slot = filters.getStackInSlot(i);

                if (slot != null && RefinedStorageUtils.compareStack(slot, stack, compare)) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }
}
