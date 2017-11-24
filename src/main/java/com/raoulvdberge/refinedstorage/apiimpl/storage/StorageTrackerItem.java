package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.api.storage.IStorageTracker;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import gnu.trove.map.hash.TCustomHashMap;
import gnu.trove.strategy.HashingStrategy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;

import java.util.Map;

public class StorageTrackerItem implements IStorageTracker<ItemStack> {
    private static final String NBT_STACK = "Stack";
    private static final String NBT_NAME = "Name";
    private static final String NBT_TIME = "Time";

    private Map<ItemStack, IStorageTrackerEntry> changes = new TCustomHashMap<>(new HashingStrategy<ItemStack>() {
        @Override
        public int computeHashCode(ItemStack stack) {
            return API.instance().getItemStackHashCode(stack);
        }

        @Override
        public boolean equals(ItemStack left, ItemStack right) {
            return API.instance().getComparer().isEqualNoQuantity(left, right);
        }
    });

    private Runnable listener;

    public StorageTrackerItem(Runnable listener) {
        this.listener = listener;
    }

    @Override
    public void changed(EntityPlayer player, ItemStack stack) {
        changes.put(stack, new StorageTrackerEntry(MinecraftServer.getCurrentTimeMillis(), player.getName()));

        listener.run();
    }

    @Override
    public IStorageTrackerEntry get(ItemStack stack) {
        return changes.get(stack);
    }

    public void readFromNBT(NBTTagList list) {
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound tag = list.getCompoundTagAt(i);

            ItemStack stack = new ItemStack(tag.getCompoundTag(NBT_STACK));

            if (!stack.isEmpty()) {
                changes.put(
                    stack,
                    new StorageTrackerEntry(tag.getLong(NBT_TIME), tag.getString(NBT_NAME))
                );
            }
        }
    }

    public NBTTagList serializeNBT() {
        NBTTagList list = new NBTTagList();

        for (Map.Entry<ItemStack, IStorageTrackerEntry> entry : changes.entrySet()) {
            NBTTagCompound tag = new NBTTagCompound();

            tag.setLong(NBT_TIME, entry.getValue().getTime());
            tag.setString(NBT_NAME, entry.getValue().getName());
            tag.setTag(NBT_STACK, entry.getKey().serializeNBT());

            list.appendTag(tag);
        }

        return list;
    }
}
