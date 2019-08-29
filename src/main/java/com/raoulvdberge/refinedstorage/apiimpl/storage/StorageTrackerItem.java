package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.api.storage.IStorageTracker;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

import java.util.HashMap;
import java.util.Map;

public class StorageTrackerItem implements IStorageTracker<ItemStack> {
    private static final String NBT_STACK = "Stack";
    private static final String NBT_NAME = "Name";
    private static final String NBT_TIME = "Time";


    private Map<ItemStack, IStorageTrackerEntry> changes = new HashMap<>(); /* TODO BROKEN new TCustomHashMap<>(new HashingStrategy<ItemStack>() {
        @Override
        public int computeHashCode(ItemStack stack) {
            return API.instance().getItemStackHashCode(stack);
        }

        @Override
        public boolean equals(ItemStack left, ItemStack right) {
            return API.instance().getComparer().isEqualNoQuantity(left, right);
        }
    });*/

    private Runnable listener;

    public StorageTrackerItem(Runnable listener) {
        this.listener = listener;
    }

    @Override
    public void changed(PlayerEntity player, ItemStack stack) {
        changes.put(stack, new StorageTrackerEntry(System.currentTimeMillis(), player.getName().getString())); // TODO correct?

        listener.run();
    }

    @Override
    public IStorageTrackerEntry get(ItemStack stack) {
        return changes.get(stack);
    }

    public void readFromNbt(ListNBT list) {
        for (int i = 0; i < list.size(); ++i) {
            CompoundNBT tag = list.getCompound(i);

            ItemStack stack = StackUtils.deserializeStackFromNbt(tag.getCompound(NBT_STACK));

            if (!stack.isEmpty()) {
                changes.put(
                    stack,
                    new StorageTrackerEntry(tag.getLong(NBT_TIME), tag.getString(NBT_NAME))
                );
            }
        }
    }

    public ListNBT serializeNbt() {
        ListNBT list = new ListNBT();

        for (Map.Entry<ItemStack, IStorageTrackerEntry> entry : changes.entrySet()) {
            CompoundNBT tag = new CompoundNBT();

            tag.putLong(NBT_TIME, entry.getValue().getTime());
            tag.putString(NBT_NAME, entry.getValue().getName());
            tag.put(NBT_STACK, StackUtils.serializeStackToNbt(entry.getKey()));

            list.add(tag);
        }

        return list;
    }
}
