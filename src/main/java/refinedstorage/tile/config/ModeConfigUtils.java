package refinedstorage.tile.config;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageUtils;

public class ModeConfigUtils {
    public static boolean doesNotViolateMode(IInventory inventory, IModeConfig mode, int compare, ItemStack stack) {
        if (mode.isWhitelist()) {
            int slots = 0;

            for (int i = 0; i < inventory.getSizeInventory(); ++i) {
                ItemStack slot = inventory.getStackInSlot(i);

                if (slot != null) {
                    slots++;

                    if (RefinedStorageUtils.compareStack(slot, stack, compare)) {
                        return true;
                    }
                }
            }

            return slots == 0;
        } else if (mode.isBlacklist()) {
            for (int i = 0; i < inventory.getSizeInventory(); ++i) {
                ItemStack slot = inventory.getStackInSlot(i);

                if (slot != null && RefinedStorageUtils.compareStack(slot, stack, compare)) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }
}
