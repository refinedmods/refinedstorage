package com.raoulvdberge.refinedstorage.apiimpl.storage.externalstorage;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IExternalStorageContext;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IStorageExternal;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class StorageExternalItem implements IStorageExternal<ItemStack> {
    private IExternalStorageContext context;
    private Supplier<IItemHandler> handlerSupplier;
    private List<ItemStack> cache;
    private boolean connectedToInterface;

    public StorageExternalItem(IExternalStorageContext context, Supplier<IItemHandler> handlerSupplier, boolean connectedToInterface) {
        this.context = context;
        this.handlerSupplier = handlerSupplier;
        this.connectedToInterface = connectedToInterface;
    }

    public boolean isConnectedToInterface() {
        return connectedToInterface;
    }

    @Override
    public void update(INetwork network) {
        // If we are insert only, we don't care about sending changes
        if (getAccessType() == AccessType.INSERT) {
            return;
        }

        if (cache == null) {
            cache = new ArrayList<>(getStacks());

            return;
        }

        List<ItemStack> newStacks = new ArrayList<>(getStacks());

        for (int i = 0; i < newStacks.size(); ++i) {
            ItemStack actual = newStacks.get(i);

            // If we exceed the cache size, than that means this item is added
            if (i >= cache.size()) {
                if (!actual.isEmpty()) {
                    network.getItemStorageCache().add(actual, actual.getCount(), false, true);
                }

                continue;
            }

            ItemStack cached = cache.get(i);

            if (!cached.isEmpty() && actual.isEmpty()) {
                // If the cached is not empty but the actual is, we remove this item
                network.getItemStorageCache().remove(cached, cached.getCount(), true);
            } else if (cached.isEmpty() && !actual.isEmpty()) {
                // If the cached is empty and the actual isn't, we added this item
                network.getItemStorageCache().add(actual, actual.getCount(), false, true);

                if (!isConnectedToInterface()) {
                    network.getCraftingManager().track(actual, actual.getCount());
                }
            } else if (cached.isEmpty() && actual.isEmpty()) {
                // If they're both empty, nothing happens
            } else if (!API.instance().getComparer().isEqualNoQuantity(cached, actual)) {
                // If both items mismatch, remove the old and add the new
                network.getItemStorageCache().remove(cached, cached.getCount(), true);
                network.getItemStorageCache().add(actual, actual.getCount(), false, true);

                if (!isConnectedToInterface()) {
                    network.getCraftingManager().track(actual, actual.getCount());
                }
            } else if (cached.getCount() != actual.getCount()) {
                int delta = actual.getCount() - cached.getCount();

                if (delta > 0) {
                    network.getItemStorageCache().add(actual, delta, false, true);

                    if (!isConnectedToInterface()) {
                        network.getCraftingManager().track(actual, delta);
                    }
                } else {
                    network.getItemStorageCache().remove(actual, Math.abs(delta), true);
                }
            }
        }

        // If the cache size is somehow bigger than the actual stacks, that means the inventory shrunk
        // In that case, we remove the items that have been removed due to the shrinkage
        if (cache.size() > newStacks.size()) {
            for (int i = newStacks.size(); i < cache.size(); ++i) {
                if (cache.get(i) != ItemStack.EMPTY) {
                    network.getItemStorageCache().remove(cache.get(i), cache.get(i).getCount(), true);
                }
            }
        }

        this.cache = newStacks;

        network.getItemStorageCache().flush();
    }

    @Override
    public int getCapacity() {
        IItemHandler handler = handlerSupplier.get();

        if (handler == null) {
            return 0;
        }

        int capacity = 0;

        for (int i = 0; i < handler.getSlots(); ++i) {
            capacity += Math.min(handler.getSlotLimit(i), handler.getStackInSlot(i).getMaxStackSize());
        }

        return capacity;
    }

    @Override
    public Collection<ItemStack> getStacks() {
        IItemHandler handler = handlerSupplier.get();

        if (handler == null) {
            return Collections.emptyList();
        }

        List<ItemStack> stacks = new ArrayList<>();

        for (int i = 0; i < handler.getSlots(); ++i) {
            stacks.add(handler.getStackInSlot(i).copy());
        }

        return stacks;
    }

    @Nullable
    @Override
    public ItemStack insert(@Nonnull ItemStack stack, int size, Action action) {
        IItemHandler handler = handlerSupplier.get();

        if (handler != null && context.acceptsItem(stack)) {
            return StackUtils.emptyToNull(ItemHandlerHelper.insertItem(handler, ItemHandlerHelper.copyStackWithSize(stack, size), action == Action.SIMULATE));
        }

        return ItemHandlerHelper.copyStackWithSize(stack, size);
    }

    @Nullable
    @Override
    public ItemStack extract(@Nonnull ItemStack stack, int size, int flags, Action action) {
        int remaining = size;

        ItemStack received = null;

        IItemHandler handler = handlerSupplier.get();

        if (handler == null) {
            return null;
        }

        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack slot = handler.getStackInSlot(i);

            if (!slot.isEmpty() && API.instance().getComparer().isEqual(slot, stack, flags)) {
                ItemStack got = handler.extractItem(i, remaining, action == Action.SIMULATE);

                if (!got.isEmpty()) {
                    if (received == null) {
                        received = got.copy();
                    } else {
                        received.grow(got.getCount());
                    }

                    remaining -= got.getCount();

                    if (remaining == 0) {
                        break;
                    }
                }
            }
        }

        return received;
    }

    @Override
    public int getStored() {
        IItemHandler handler = handlerSupplier.get();

        if (handler == null) {
            return 0;
        }

        int size = 0;

        for (int i = 0; i < handler.getSlots(); ++i) {
            size += handler.getStackInSlot(i).getCount();
        }

        return size;
    }

    @Override
    public int getPriority() {
        return context.getPriority();
    }

    @Override
    public AccessType getAccessType() {
        return context.getAccessType();
    }

    @Override
    public int getCacheDelta(int storedPreInsertion, int size, @Nullable ItemStack remainder) {
        if (getAccessType() == AccessType.INSERT) {
            return 0;
        }

        return remainder == null ? size : (size - remainder.getCount());
    }
}
