package com.raoulvdberge.refinedstorage.inventory;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCache;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class ItemHandlerStorage implements IItemHandler {
    private IStorage<ItemStack> storage;
    private ItemStack[] storageCacheData;

    public ItemHandlerStorage(IStorage<ItemStack> storage, IStorageCache<ItemStack> storageCache) {
        this.storage = storage;

        storageCache.setListener((stack, size) -> invalidate(storageCache));

        invalidate(storageCache);
    }

    @Override
    public int getSlots() {
        // Keep 1 slot extra for new items
        return storageCacheData.length + 1;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot >= storageCacheData.length ? ItemStack.EMPTY : storageCacheData[slot];
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return RSUtils.transformNullToEmpty(storage.insert(stack, stack.getCount(), simulate));
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return slot >= storageCacheData.length ? ItemStack.EMPTY : RSUtils.transformNullToEmpty(storage.extract(storageCacheData[slot], amount, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT, simulate));
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    private void invalidate(IStorageCache<ItemStack> storageCache) {
        this.storageCacheData = storageCache.getList().getStacks().toArray(new ItemStack[0]);
    }
}
