package refinedstorage.tile.config;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RefinedStorageUtils;

public class ModeFilter {
    public static boolean respectsMode(IItemHandler filters, IModeConfig mode, int compare, ItemStack stack) {
        if (mode.getMode() == ModeConstants.WHITELIST) {
            int slots = 0;

            for (int i = 0; i < filters.getSlots(); ++i) {
                ItemStack slot = filters.getStackInSlot(i);

                if (slot != null) {
                    slots++;

                    if (RefinedStorageUtils.compareStack(slot, stack, compare)) {
                        return true;
                    }
                }
            }

            return slots == 0;
        } else if (mode.getMode() == ModeConstants.BLACKLIST) {
            for (int i = 0; i < filters.getSlots(); ++i) {
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
