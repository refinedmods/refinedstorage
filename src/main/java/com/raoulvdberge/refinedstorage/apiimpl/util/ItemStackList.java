package com.raoulvdberge.refinedstorage.apiimpl.util;

import com.google.common.collect.ArrayListMultimap;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IItemStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ItemStackList implements IItemStackList {
    private ArrayListMultimap<Item, ItemStack> stacks = ArrayListMultimap.create();

    @Override
    public void add(ItemStack stack) {
        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (API.instance().getComparer().isEqualNoQuantity(otherStack, stack)) {
                if ((long) otherStack.stackSize + (long) stack.stackSize > Integer.MAX_VALUE) {
                    otherStack.stackSize = Integer.MAX_VALUE;
                } else {
                    otherStack.stackSize += stack.stackSize;
                }
                
                return;
            }
        }

        stacks.put(stack.getItem(), stack.copy());
    }

    @Override
    public boolean remove(@Nonnull ItemStack stack, int size, boolean removeIfReachedZero) {
        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (API.instance().getComparer().isEqualNoQuantity(otherStack, stack)) {
                otherStack.stackSize -= size;
                boolean success = otherStack.stackSize >= 0;

                if (otherStack.stackSize <= 0 && removeIfReachedZero) {
                    stacks.remove(otherStack.getItem(), otherStack);
                }

                return success;
            }
        }

        return false;
    }

    @Override
    @Nullable
    public ItemStack get(@Nonnull ItemStack stack, int flags) {
        // When the oreDict flag is set all stacks need to be checked not just the ones matching the Item
        for (ItemStack otherStack : (flags & IComparer.COMPARE_OREDICT) == IComparer.COMPARE_OREDICT ? stacks.values() : stacks.get(stack.getItem())) {
            if (API.instance().getComparer().isEqual(otherStack, stack, flags)) {
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
            .filter(stack -> stack.stackSize <= 0)
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
            list.add(stack.copy());
        }

        return list;
    }

    @Override
    public String toString() {
        return stacks.toString();
    }
}
