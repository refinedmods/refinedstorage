package com.raoulvdberge.refinedstorage.inventory;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCache;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;

public class ItemHandlerStorage implements IItemHandler, BiConsumer<ItemStack, Integer> {
    private IStorage<ItemStack> storage;
    private IStorageCache<ItemStack> storageCache;
    private ItemStack[] storageCacheData;

    public ItemHandlerStorage(IStorage<ItemStack> storage, IStorageCache<ItemStack> storageCache) {
        this.storage = storage;
        this.storageCache = storageCache;

        invalidate();
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

    @Override
    public void accept(ItemStack stack, Integer amount) {
        invalidate();
    }

    private void invalidate() {
        this.storageCacheData = storageCache.getList().getStacks().toArray(new ItemStack[0]);
    }
}
