package com.refinedmods.refinedstorage.apiimpl.storage.tracker;

import com.refinedmods.refinedstorage.api.storage.tracker.IStorageTracker;
import com.refinedmods.refinedstorage.api.storage.tracker.StorageTrackerEntry;
import com.refinedmods.refinedstorage.util.ItemStackKey;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemStorageTracker implements IStorageTracker<ItemStack> {
    private static final String NBT_STACK = "Stack";
    private static final String NBT_NAME = "Name";
    private static final String NBT_TIME = "Time";

    private final Map<ItemStackKey, StorageTrackerEntry> changes = new HashMap<>();
    private final Runnable listener;

    public ItemStorageTracker(Runnable listener) {
        this.listener = listener;
    }

    @Override
    public void changed(Player player, ItemStack stack) {
        changes.put(new ItemStackKey(stack), new StorageTrackerEntry(System.currentTimeMillis(), player.getName().getString()));

        listener.run();
    }

    @Override
    public StorageTrackerEntry get(ItemStack stack) {
        return changes.get(new ItemStackKey(stack));
    }

    @Override
    public void readFromNbt(ListTag list) {
        for (int i = 0; i < list.size(); ++i) {
            CompoundTag tag = list.getCompound(i);

            ItemStack stack = StackUtils.deserializeStackFromNbt(tag.getCompound(NBT_STACK));

            if (!stack.isEmpty()) {
                changes.put(new ItemStackKey(stack), new StorageTrackerEntry(tag.getLong(NBT_TIME), tag.getString(NBT_NAME)));
            }
        }
    }

    @Override
    public ListTag serializeNbt() {
        ListTag list = new ListTag();

        for (Map.Entry<ItemStackKey, StorageTrackerEntry> entry : changes.entrySet()) {
            CompoundTag tag = new CompoundTag();

            tag.putLong(NBT_TIME, entry.getValue().getTime());
            tag.putString(NBT_NAME, entry.getValue().getName());
            tag.put(NBT_STACK, StackUtils.serializeStackToNbt(entry.getKey().getStack()));

            list.add(tag);
        }

        return list;
    }
}
