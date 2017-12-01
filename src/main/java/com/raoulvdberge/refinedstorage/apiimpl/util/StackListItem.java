package com.raoulvdberge.refinedstorage.apiimpl.util;

import com.google.common.collect.ArrayListMultimap;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class StackListItem implements IStackList<ItemStack> {
    private ArrayListMultimap<Item, ItemStack> stacks = ArrayListMultimap.create();
    private List<ItemStack> removeTracker = new LinkedList<>();
    protected boolean needsCleanup = false;
    private Set<Item> touchedItems = new HashSet<>();

    @Override
    public void add(@Nonnull ItemStack stack, int size) {
        if (stack.isEmpty() || size <= 0) {
            return;
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
    public boolean remove(@Nonnull ItemStack stack, int size) {
        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (API.instance().getComparer().isEqualNoQuantity(otherStack, stack)) {
                boolean success = otherStack.getCount() - size >= 0;
                otherStack.shrink(size);

                if (otherStack.isEmpty()) {
                    touchedItems.add(stack.getItem());
                    needsCleanup = true;
                }

                return success;
            }
        }

        return false;
    }

    @Override
    public boolean trackedRemove(@Nonnull ItemStack stack, int size) {
        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (API.instance().getComparer().isEqualNoQuantity(otherStack, stack)) {
                ItemStack removed = ItemHandlerHelper.copyStackWithSize(otherStack, Math.min(size, otherStack.getCount()));
                this.removeTracker.add(removed);

                boolean success = otherStack.getCount() - size >= 0;
                otherStack.shrink(size);

                if (otherStack.isEmpty()) {
                    touchedItems.add(stack.getItem());
                    needsCleanup = true;
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
        removeTracker.forEach(s -> add(s, s.getCount()));
        removeTracker.clear();
    }

    @Override
    @Nullable
    public ItemStack get(@Nonnull ItemStack stack, int flags) {
        // When the oredict flag is set all stacks need to be checked not just the ones matching the item
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
        if (needsCleanup) {
            clean();
        }

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
        List<Pair<Item, ItemStack>> toRemove = touchedItems.stream()
            .flatMap(item -> stacks.get(item).stream().map(stack -> Pair.of(item, stack)))
            .filter(pair -> pair.getValue().isEmpty())
            .collect(Collectors.toList());

        toRemove.forEach(pair -> stacks.remove(pair.getLeft(), pair.getRight()));

        touchedItems.clear();
        needsCleanup = false;
    }

    @Override
    public boolean isEmpty() {
        return stacks.isEmpty();
    }

    @Override
    public int getSizeFromStack(ItemStack stack) {
        return stack.getCount();
    }

    @Nonnull
    @Override
    public Collection<ItemStack> getStacks() {
        if (needsCleanup) {
            clean();
        }
        return stacks.values();
    }

    @Override
    @Nonnull
    public IStackList<ItemStack> copy() {
        StackListItem list = new StackListItem();

        if (needsCleanup) {
            clean();
        }

        for (ItemStack stack : stacks.values()) {
            list.stacks.put(stack.getItem(), stack.copy());
        }

        return list;
    }

    @Nonnull
    public StackListItemOredicted getOredicted() {
        if (needsCleanup) {
            clean();
        }

        return new StackListItemOredicted(this);
    }

    @Override
    public String toString() {
        return stacks.toString();
    }

    public static ItemStack[] toCraftingGrid(IStackList<ItemStack> list, List<ItemStack> grid, int compare) {
        ItemStack[] took = new ItemStack[Math.max(9, grid.size())];

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

                if (actualInput != null) {
                    ItemStack taken = ItemHandlerHelper.copyStackWithSize(actualInput, input.getCount());

                    took[i] = taken;

                    list.remove(taken, taken.getCount());
                }
            }
        }

        return took;
    }
}
