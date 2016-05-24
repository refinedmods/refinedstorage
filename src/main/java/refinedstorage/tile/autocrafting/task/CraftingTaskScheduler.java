package refinedstorage.tile.autocrafting.task;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.tile.autocrafting.CraftingPattern;
import refinedstorage.tile.controller.TileController;

public class CraftingTaskScheduler {
    public static String NBT_SCHEDULED = "CraftingTaskScheduled";

    private ItemStack scheduledItem;

    public boolean canSchedule(int compare, ItemStack item) {
        return scheduledItem == null || !RefinedStorageUtils.compareStack(scheduledItem, item, compare);
    }

    public void schedule(TileController controller, int compare, ItemStack item) {
        CraftingPattern pattern = controller.getPattern(item, compare);

        if (pattern != null) {
            scheduledItem = item;

            controller.addCraftingTask(pattern);
        }
    }

    public void resetSchedule() {
        this.scheduledItem = null;
    }

    public void writeToNBT(NBTTagCompound tag) {
        if (scheduledItem != null) {
            tag.setTag(NBT_SCHEDULED, scheduledItem.serializeNBT());
        } else {
            tag.removeTag(NBT_SCHEDULED);
        }
    }

    public void read(NBTTagCompound tag) {
        if (tag.hasKey(NBT_SCHEDULED)) {
            scheduledItem = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(NBT_SCHEDULED));
        }
    }
}
