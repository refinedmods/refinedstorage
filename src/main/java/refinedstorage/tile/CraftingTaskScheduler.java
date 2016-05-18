package refinedstorage.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.tile.autocrafting.CraftingPattern;
import refinedstorage.tile.controller.TileController;

public class CraftingTaskScheduler {
    public static String NBT_SCHEDULED = "CraftingTaskScheduled";

    private ItemStack scheduledFor;

    public boolean canSchedule(int compare, ItemStack item) {
        // We can only reschedule if:
        // - we didn't schedule anything before
        // - the item we can't to schedule is another item
        return scheduledFor == null || !RefinedStorageUtils.compareStack(scheduledFor, item, compare);
    }

    public void schedule(TileController controller, int compare, ItemStack item) {
        CraftingPattern pattern = controller.getPattern(item, compare);

        if (pattern != null) {
            scheduledFor = item;

            controller.addCraftingTask(pattern);
        }
    }

    public void resetSchedule() {
        this.scheduledFor = null;
    }

    public void writeToNBT(NBTTagCompound tag) {
        if (scheduledFor != null) {
            tag.setTag(NBT_SCHEDULED, scheduledFor.serializeNBT());
        } else {
            tag.removeTag(NBT_SCHEDULED);
        }
    }

    public void readFromNBT(NBTTagCompound tag) {
        if (tag.hasKey(NBT_SCHEDULED)) {
            scheduledFor = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(NBT_SCHEDULED));
        }
    }
}
