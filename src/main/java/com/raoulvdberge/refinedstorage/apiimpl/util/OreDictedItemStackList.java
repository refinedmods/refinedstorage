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

public class OreDictedItemStackList implements IItemStackList {
    private IItemStackList underlyingList;
    private ArrayListMultimap<Integer, ItemStack> stacks = ArrayListMultimap.create();

    private OreDictedItemStackList() {}

    public OreDictedItemStackList(IItemStackList list) {
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
        if ((flags & IComparer.COMPARE_OREDICT) == IComparer.COMPARE_OREDICT) {
            int[] ids = OreDictionary.getOreIDs(stack);
            for (int id : ids) {
                List<ItemStack> stacks = this.stacks.get(id);
                if (stacks != null && !stacks.isEmpty()) {
                    return stacks.get(0);
                }
            }
        }
        return underlyingList.get(stack, flags);
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
        OreDictedItemStackList newList = new OreDictedItemStackList();
        newList.underlyingList = this.underlyingList.copy();
        for (Map.Entry<Integer, ItemStack> entry : this.stacks.entries()) {
            newList.stacks.put(entry.getKey(), entry.getValue());
        }
        return newList;
    }

    @Nonnull
    @Override
    public IItemStackList prepOreDict() {
        return this;
    }
}
