package refinedstorage.tile.config;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.api.storage.CompareUtils;

// @TODO: Move into IModeConfig
public final class ModeFilter {
    public static boolean respectsMode(IItemHandler filters, int mode, int compare, ItemStack stack) {
        if (mode == IModeConfig.WHITELIST) {
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
        } else if (mode == IModeConfig.BLACKLIST) {
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
}
