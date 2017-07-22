package com.raoulvdberge.refinedstorage.inventory;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCache;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.util.StackUtils;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;

public class ItemHandlerInterface implements IItemHandler, BiConsumer<ItemStack, Integer> {
    private INetwork network;
    private IStorageCache<ItemStack> storageCache;
    private IItemHandler importItems;
    private ItemStack[] storageCacheData;

    public ItemHandlerInterface(INetwork network, IStorageCache<ItemStack> storageCache, IItemHandler importItems) {
        this.network = network;
        this.storageCache = storageCache;
        this.importItems = importItems;

        invalidate();
    }

    @Override
    public int getSlots() {
        return importItems.getSlots() + storageCacheData.length;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot < importItems.getSlots()) {
            return importItems.getStackInSlot(slot);
        } else if (slot < importItems.getSlots() + storageCacheData.length) {
            return storageCacheData[slot - importItems.getSlots()];
        }

        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (slot < importItems.getSlots()) {
            return importItems.insertItem(slot, stack, simulate);
        }

        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot < importItems.getSlots()) {
            return ItemStack.EMPTY;
        } else if (slot < importItems.getSlots() + storageCacheData.length) {
            return StackUtils.nullToEmpty(network.extractItem(storageCacheData[slot - importItems.getSlots()], amount, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT, simulate));
        }

        return ItemStack.EMPTY;
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
