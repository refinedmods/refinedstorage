package com.refinedmods.refinedstorage.apiimpl.storage.tracker;

import com.refinedmods.refinedstorage.api.storage.tracker.IStorageTracker;
import com.refinedmods.refinedstorage.api.storage.tracker.StorageTrackerEntry;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.fluids.FluidStack;
import java.util.HashMap;
import java.util.Map;

public class FluidStorageTracker implements IStorageTracker<FluidStack> {
    private static final String NBT_STACK = "Stack";
    private static final String NBT_NAME = "Name";
    private static final String NBT_TIME = "Time";

    private final Map<Key, StorageTrackerEntry> changes = new HashMap<>();
    private final Runnable listener;

    public FluidStorageTracker(Runnable listener) {
        this.listener = listener;
    }

    @Override
    public void changed(Player player, FluidStack stack) {
        changes.put(new Key(stack), new StorageTrackerEntry(System.currentTimeMillis(), player.getName().getString()));

        listener.run();
    }

    @Override
    public StorageTrackerEntry get(FluidStack stack) {
        return changes.get(new Key(stack));
    }

    @Override
    public void readFromNbt(ListTag list) {
        for (int i = 0; i < list.size(); ++i) {
            CompoundTag tag = list.getCompound(i);

            FluidStack stack = FluidStack.loadFluidStackFromNBT(tag.getCompound(NBT_STACK));

            if (!stack.isEmpty()) {
                changes.put(new Key(stack), new StorageTrackerEntry(tag.getLong(NBT_TIME), tag.getString(NBT_NAME)));
            }
        }
    }

    @Override
    public ListTag serializeNbt() {
        ListTag list = new ListTag();

        for (Map.Entry<Key, StorageTrackerEntry> entry : changes.entrySet()) {
            CompoundTag tag = new CompoundTag();

            tag.putLong(NBT_TIME, entry.getValue().getTime());
            tag.putString(NBT_NAME, entry.getValue().getName());
            tag.put(NBT_STACK, entry.getKey().stack.writeToNBT(new CompoundTag()));

            list.add(tag);
        }

        return list;
    }

    private static class Key {
        private final FluidStack stack;

        public Key(FluidStack stack) {
            this.stack = stack;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Key && API.instance().getComparer().isEqual(stack, ((Key) other).stack, IComparer.COMPARE_NBT);
        }

        @Override
        public int hashCode() {
            return API.instance().getFluidStackHashCode(stack);
        }
    }
}
