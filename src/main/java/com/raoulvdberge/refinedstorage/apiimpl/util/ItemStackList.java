package com.raoulvdberge.refinedstorage.apiimpl.util;

import com.google.common.collect.ArrayListMultimap;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IItemStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemStackList implements IItemStackList {
    private ArrayListMultimap<Item, ItemStack> stacks = ArrayListMultimap.create();
    private List<ItemStack> removeTracker = new LinkedList<>();

    @Override
    public void add(@Nonnull ItemStack stack, int size) {
        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (API.instance().getComparer().isEqualNoQuantity(otherStack, stack)) {
                if ((long) otherStack.getCount() + (long) size > Integer.MAX_VALUE) {
                    otherStack.setCount(Integer.MAX_VALUE);
                } else {
                    otherStack.grow(size);
                }

                return;
            }
        }

        stacks.put(stack.getItem(), size == 0 ? stack.copy() : ItemHandlerHelper.copyStackWithSize(stack, size));
    }

    @Override
    public boolean remove(@Nonnull ItemStack stack, int size, boolean removeIfReachedZero) {
        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (otherStack.getCount() > 0 && API.instance().getComparer().isEqualNoQuantity(otherStack, stack)) {
                if (otherStack.getCount() - size <= 0 && removeIfReachedZero) {
                    stacks.remove(otherStack.getItem(), otherStack);
                } else {
                    otherStack.shrink(size);
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean trackedRemove(@Nonnull ItemStack stack, int size, boolean removeIfReachedZero) {
        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (otherStack.getCount() > 0 && API.instance().getComparer().isEqualNoQuantity(otherStack, stack)) {
                ItemStack removed = ItemHandlerHelper.copyStackWithSize(otherStack, Math.min(size, otherStack.getCount()));
                this.removeTracker.add(removed);

                otherStack.shrink(size);

                boolean success = otherStack.getCount() >= 0;

                if (otherStack.getCount() <= 0 && removeIfReachedZero) {
                    stacks.remove(otherStack.getItem(), otherStack);
                }

                return success;
            }
        }

        return false;
    }

    @Override
    public List<ItemStack> getRemoveTracker() {
        return removeTracker;
    }

    @Override
    public void undo() {
        removeTracker.forEach(this::add);
        removeTracker.clear();
    }

    @Override
    @Nullable
    public ItemStack get(@Nonnull ItemStack stack, int flags) {
        // When the oredict flag is set all stacks need to be checked not just the ones matching the item
        for (ItemStack otherStack : (flags & IComparer.COMPARE_OREDICT) == IComparer.COMPARE_OREDICT ? stacks.values() : stacks.get(stack.getItem())) {
            if (otherStack.getCount() > 0 && API.instance().getComparer().isEqual(otherStack, stack, flags)) {
                return otherStack;
            }
        }

        return null;
    }

    @Override
    @Nullable
    public ItemStack get(int hash) {
        for (ItemStack stack : this.stacks.values()) {
            if (API.instance().getItemStackHashCode(stack) == hash) {
                return stack;
            }
        }

        return null;
    }

    @Override
    public void clear() {
        stacks.clear();
    }

    @Override
    public void clean() {
        List<ItemStack> toRemove = stacks.values().stream()
            .filter(stack -> stack.getCount() <= 0)
            .collect(Collectors.toList());

        toRemove.forEach(stack -> stacks.remove(stack.getItem(), stack));
    }

    @Override
    public boolean isEmpty() {
        return stacks.isEmpty();
    }

    @Nonnull
    @Override
    public Collection<ItemStack> getStacks() {
        return stacks.values();
    }

    @Override
    @Nonnull
    public IItemStackList copy() {
        ItemStackList list = new ItemStackList();

        for (ItemStack stack : stacks.values()) {
            list.stacks.put(stack.getItem(), stack.copy());
        }

        return list;
    }

    @Nonnull
    @Override
    public IItemStackList getOredicted() {
        return new ItemStackListOredicted(this);
    }

    @Override
    public String toString() {
        return stacks.toString();
    }

    public static ItemStack[] toCraftingGrid(IItemStackList list, List<ItemStack> grid, int compare) {
        ItemStack[] took = new ItemStack[9];
        for (int i = 0; i < grid.size(); i++) {
            ItemStack input = grid.get(i);
            if (input != null) {
                // This will be a tool, like a hammer
                if (input.isItemStackDamageable()) {
                    compare &= ~IComparer.COMPARE_DAMAGE;
                } else {
                    compare |= IComparer.COMPARE_DAMAGE;
                }
                ItemStack actualInput = list.get(input, compare);
                ItemStack taken = ItemHandlerHelper.copyStackWithSize(actualInput, input.getCount());
                took[i] = taken;
                list.remove(taken, true);
            }
        }
        return took;
    }
}
