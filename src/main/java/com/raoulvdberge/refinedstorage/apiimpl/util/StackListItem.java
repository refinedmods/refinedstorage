package com.raoulvdberge.refinedstorage.apiimpl.util;

import com.google.common.collect.ArrayListMultimap;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public class StackListItem implements IStackList<ItemStack> {
    private ArrayListMultimap<Item, ItemStack> stacks = ArrayListMultimap.create();

    @Override
    public void add(@Nonnull ItemStack stack, int size) {
        if (stack == null || stack.isEmpty() || size <= 0) {
            throw new IllegalArgumentException("Cannot accept empty stack");
        }

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

        stacks.put(stack.getItem(), ItemHandlerHelper.copyStackWithSize(stack, size));
    }

    @Override
    public void add(@Nonnull ItemStack stack) {
        add(stack, stack.getCount());
    }

    @Override
    public boolean remove(@Nonnull ItemStack stack, int size) {
        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (API.instance().getComparer().isEqualNoQuantity(otherStack, stack)) {
                boolean success = otherStack.getCount() - size >= 0;

                if (otherStack.getCount() - size <= 0) {
                    stacks.remove(otherStack.getItem(), otherStack);
                } else {
                    otherStack.shrink(size);
                }

                return success;
            }
        }

        return false;
    }

    @Override
    public boolean remove(@Nonnull ItemStack stack) {
        return remove(stack, stack.getCount());
    }

    @Override
    @Nullable
    public ItemStack get(@Nonnull ItemStack stack, int flags) {
        for (ItemStack otherStack : stacks.get(stack.getItem())) {
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
    public IStackList<ItemStack> copy() {
        StackListItem list = new StackListItem();

        for (ItemStack stack : stacks.values()) {
            list.stacks.put(stack.getItem(), stack.copy());
        }

        return list;
    }
}
