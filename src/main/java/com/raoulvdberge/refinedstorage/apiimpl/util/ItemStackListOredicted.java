package com.raoulvdberge.refinedstorage.apiimpl.util;

import com.google.common.collect.ArrayListMultimap;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IItemStackList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemStackListOredicted implements IItemStackList {
    private IItemStackList underlyingList;
    private ArrayListMultimap<Integer, ItemStack> stacks = ArrayListMultimap.create();

    private ItemStackListOredicted() {
    }

    public ItemStackListOredicted(IItemStackList list) {
        this.underlyingList = list;
        initOreDict();
    }

    private void initOreDict() {
        for (ItemStack stack : underlyingList.getStacks()) {
            for (int id : OreDictionary.getOreIDs(stack)) {
                stacks.put(id, stack);
            }
        }
    }

    @Override
    public void add(ItemStack stack) {
        underlyingList.add(stack);
        ItemStack internalStack = underlyingList.get(stack);
        if (internalStack != null && internalStack.stackSize == stack.stackSize) {
            for (int id : OreDictionary.getOreIDs(internalStack)) {
                stacks.put(id, internalStack);
            }
        }
    }

    @Override
    public boolean remove(@Nonnull ItemStack stack, int size, boolean removeIfReachedZero) {
        boolean rvalue = underlyingList.remove(stack, size, removeIfReachedZero);
        if (removeIfReachedZero) {
            localClean();
        }
        return rvalue;
    }

    @Override
    public boolean trackedRemove(@Nonnull ItemStack stack, int size, boolean removeIfReachedZero) {
        boolean rvalue = underlyingList.trackedRemove(stack, size, removeIfReachedZero);
        if (removeIfReachedZero) {
            localClean();
        }
        return rvalue;
    }

    @Override
    public List<ItemStack> getRemoveTracker() {
        return underlyingList.getRemoveTracker();
    }

    @Override
    public void undo() {
        underlyingList.getRemoveTracker().forEach(this::add);
        underlyingList.getRemoveTracker().clear();
    }

    @Nullable
    @Override
    public ItemStack get(@Nonnull ItemStack stack, int flags) {
        // Check the underlying list but don't do oredict things for exact match
        ItemStack exact = underlyingList.get(stack, flags & ~IComparer.COMPARE_OREDICT);
        if (exact == null) {
            if ((flags & IComparer.COMPARE_OREDICT) == IComparer.COMPARE_OREDICT) {
                int[] ids = OreDictionary.getOreIDs(stack);
                for (int id : ids) {
                    List<ItemStack> stacks = this.stacks.get(id);
                    if (stacks != null && !stacks.isEmpty()) {
                        int i = 0;
                        ItemStack returnStack = stacks.get(i++);
                        while (returnStack.stackSize == 0 && i < stacks.size()) {
                            returnStack = stacks.get(i++);
                        }
                        if (returnStack.stackSize != 0) {
                            return returnStack;
                        }
                    }
                }
            }
        }
        return exact;
    }

    @Nullable
    @Override
    public ItemStack get(int hash) {
        return underlyingList.get(hash);
    }

    @Override
    public void clear() {
        underlyingList.clear();
    }

    private void localClean() {
        List<Map.Entry<Integer, ItemStack>> toRemove = stacks.entries().stream()
            .filter(entry -> entry.getValue().stackSize <= 0)
            .collect(Collectors.toList());

        toRemove.forEach(entry -> stacks.remove(entry.getKey(), entry.getValue()));
    }

    @Override
    public void clean() {
        localClean();
        underlyingList.clean();
    }

    @Override
    public boolean isEmpty() {
        return underlyingList.isEmpty();
    }

    @Nonnull
    @Override
    public Collection<ItemStack> getStacks() {
        return underlyingList.getStacks();
    }

    @Nonnull
    @Override
    public IItemStackList copy() {
        ItemStackListOredicted newList = new ItemStackListOredicted();
        newList.underlyingList = this.underlyingList.copy();
        for (Map.Entry<Integer, ItemStack> entry : this.stacks.entries()) {
            newList.stacks.put(entry.getKey(), entry.getValue());
        }
        return newList;
    }

    @Nonnull
    @Override
    public IItemStackList getOredicted() {
        return this;
    }
}
