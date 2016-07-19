package refinedstorage.apiimpl.autocrafting;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.storage.CompareUtils;

public class CraftingTaskScheduler {
    private static final String NBT_SCHEDULED = "CraftingTaskScheduled";

    private TileEntity tile;
    private ItemStack scheduledItem;

    public CraftingTaskScheduler(TileEntity tile) {
        this.tile = tile;
    }

    public boolean canSchedule(int compare, ItemStack item) {
        return scheduledItem == null || !CompareUtils.compareStack(scheduledItem, item, compare);
    }

    public void schedule(INetworkMaster network, int compare, ItemStack item) {
        ICraftingPattern pattern = network.getPattern(item, compare);

        if (pattern != null) {
            scheduledItem = item;

            network.addCraftingTaskAsLast(network.createCraftingTask(pattern));

            tile.markDirty();
        }
    }

    public void resetSchedule() {
        scheduledItem = null;

        tile.markDirty();
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
