package refinedstorage.apiimpl.autocrafting;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.network.INetworkMaster;

public class CraftingTaskScheduler {
    private static final String NBT_SCHEDULED = "CraftingTaskScheduled";

    private ItemStack scheduledItem;

    public boolean canSchedule(int compare, ItemStack item) {
        return scheduledItem == null || !RefinedStorageUtils.compareStack(scheduledItem, item, compare);
    }

    public void schedule(INetworkMaster network, int compare, ItemStack item) {
        ICraftingPattern pattern = network.getPattern(item, compare);

        if (pattern != null) {
            scheduledItem = item;

            network.addCraftingTask(network.createCraftingTask(pattern));
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
