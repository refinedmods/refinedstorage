package refinedstorage.tile.settings;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import refinedstorage.util.InventoryUtils;

public class ModeSettingUtils {
    public static boolean doesNotViolateMode(IInventory inventory, IModeSetting mode, int compare, ItemStack stack) {
        if (mode.isWhitelist()) {
            int slots = 0;

            for (int i = 0; i < inventory.getSizeInventory(); ++i) {
                ItemStack slot = inventory.getStackInSlot(i);

                if (slot != null) {
                    slots++;

                    if (InventoryUtils.compareStack(slot, stack, compare)) {
                        return true;
                    }
                }
            }

            return slots == 0;
        } else if (mode.isBlacklist()) {
            for (int i = 0; i < inventory.getSizeInventory(); ++i) {
                ItemStack slot = inventory.getStackInSlot(i);

                if (slot != null && InventoryUtils.compareStack(slot, stack, compare)) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }
}
