package com.raoulvdberge.refinedstorage.apiimpl.util;

import com.google.common.collect.ArrayListMultimap;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class StackListItemOredicted implements IStackList<ItemStack> {
    private StackListItem underlyingList;
    private ArrayListMultimap<Integer, ItemStack> stacks = ArrayListMultimap.create();

    private StackListItemOredicted() {
    }

    public StackListItemOredicted(StackListItem list) {
        this.underlyingList = list;
        initOreDict();
    }

    private void initOreDict() {
        for (ItemStack stack : underlyingList.getStacks()) {
            if (!stack.isEmpty()) {
                for (int id : OreDictionary.getOreIDs(stack)) {
                    stacks.put(id, stack);
                }
            }
        }
    }

    @Override
    public void add(@Nonnull ItemStack stack, int size) {
        underlyingList.add(stack, size);

        ItemStack internalStack = underlyingList.get(stack);

        if (internalStack != null && internalStack.getCount() == stack.getCount()) {
            for (int id : OreDictionary.getOreIDs(internalStack)) {
                stacks.put(id, internalStack);
            }
        }
    }

    @Override
    public boolean remove(@Nonnull ItemStack stack, int size) {
        return underlyingList.remove(stack, size);
    }

    @Override
    public boolean trackedRemove(@Nonnull ItemStack stack, int size) {
        return underlyingList.trackedRemove(stack, size);
    }

    @Override
    public List<ItemStack> getRemoveTracker() {
        return underlyingList.getRemoveTracker();
    }

    @Override
    public void undo() {
        underlyingList.getRemoveTracker().forEach(s -> add(s, s.getCount()));
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

                        while (returnStack.isEmpty() && i < stacks.size()) {
                            returnStack = stacks.get(i++);
                        }

                        if (!returnStack.isEmpty()) {
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
        stacks.clear();
        underlyingList.clear();
    }

    @Override
    public boolean isEmpty() {
        return underlyingList.isEmpty();
    }

    @Override
    public int getSizeFromStack(ItemStack stack) {
        return underlyingList.getSizeFromStack(stack);
    }

    @Nonnull
    @Override
    public Collection<ItemStack> getStacks() {
        return underlyingList.getStacks();
    }

    @Nonnull
    @Override
    public IStackList<ItemStack> copy() {
        StackListItemOredicted newList = new StackListItemOredicted();
        newList.underlyingList = (StackListItem) this.underlyingList.copy();

        for (Map.Entry<Integer, ItemStack> entry : this.stacks.entries()) {
            newList.stacks.put(entry.getKey(), entry.getValue());
        }

        return newList;
    }

    @Override
    public IStackList<ItemStack> getOredicted() {
        return this;
    }
}
