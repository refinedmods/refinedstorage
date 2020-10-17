package com.refinedmods.refinedstorage.apiimpl.util;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.api.util.IStackList;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.api.util.StackListResult;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ItemStackList implements IStackList<ItemStack> {
    private final Map<ItemMeta, StackListEntry<ItemStack>> stacks = new HashMap<>();
    private final Multimap<Item, ItemMeta> itemsByType = LinkedHashMultimap.create();
    private final Map<UUID, ItemStack> index = new HashMap<>();

    @Override
    public StackListResult<ItemStack> add(@Nonnull ItemStack stack, int size) {
        if (stack.isEmpty() || size <= 0) {
            throw new IllegalArgumentException("Cannot accept empty stack");
        }

        ItemMeta itemMeta = new ItemMeta(stack);
        StackListEntry<ItemStack> entry = stacks.get(itemMeta);
        if (entry != null) {
            ItemStack otherStack = entry.getStack();

            if ((long) otherStack.getCount() + (long) size > Integer.MAX_VALUE) {
                otherStack.setCount(Integer.MAX_VALUE);
            } else {
                otherStack.grow(size);
            }

            return new StackListResult<>(otherStack, entry.getId(), size);
        }

        StackListEntry<ItemStack> newEntry = new StackListEntry<>(ItemHandlerHelper.copyStackWithSize(stack, size));

        stacks.put(itemMeta, newEntry);
        index.put(newEntry.getId(), newEntry.getStack());
        itemsByType.put(itemMeta.getItem(), itemMeta);

        return new StackListResult<>(newEntry.getStack(), newEntry.getId(), size);
    }

    @Override
    public StackListResult<ItemStack> add(@Nonnull ItemStack stack) {
        return add(stack, stack.getCount());
    }

    @Override
    public StackListResult<ItemStack> remove(@Nonnull ItemStack stack, int size) {
        final ItemMeta itemMeta = new ItemMeta(stack);
        StackListEntry<ItemStack> entry = stacks.get(itemMeta);
        if (entry != null) {
            ItemStack otherStack = entry.getStack();

            if (otherStack.getCount() - size <= 0) {
                stacks.remove(itemMeta);
                index.remove(entry.getId());
                itemsByType.remove(itemMeta.getItem(), itemMeta);

                return new StackListResult<>(otherStack, entry.getId(), -otherStack.getCount());
            } else {
                otherStack.shrink(size);

                return new StackListResult<>(otherStack, entry.getId(), -size);
            }
        }

        return null;
    }

    @Override
    public StackListResult<ItemStack> remove(@Nonnull ItemStack stack) {
        return remove(stack, stack.getCount());
    }

    @Override
    public int getCount(@Nonnull ItemStack stack, int flags) {
        ItemStack found = get(stack, flags);
        if (found == null) {
            return 0;
        }

        return found.getCount();
    }

    @Nullable
    @Override
    public StackListEntry<ItemStack> getEntry(@Nonnull ItemStack stack, int flags) {
        IComparer comparer = API.instance().getComparer();
        if ((flags & IComparer.COMPARE_NBT) == IComparer.COMPARE_NBT) {
            // Fast path for NBT-aware comparison
            StackListEntry<ItemStack> entry = stacks.get(new ItemMeta(stack));
            if (entry != null && comparer.isEqual(entry.getStack(), stack, flags)) {
                return entry;
            }
        } else {
            for (ItemMeta meta : itemsByType.get(stack.getItem())) {
                StackListEntry<ItemStack> entry = stacks.get(meta);
                if (comparer.isEqual(entry.getStack(), stack, flags)) {
                    return entry;
                }
            }
        }

        return null;
    }

    @Override
    @Nullable
    public ItemStack get(UUID id) {
        return index.get(id);
    }

    @Override
    public void clear() {
        stacks.clear();
        index.clear();
        itemsByType.clear();
    }

    @Override
    public boolean isEmpty() {
        return stacks.isEmpty();
    }

    @Nonnull
    @Override
    public Collection<StackListEntry<ItemStack>> getStacks() {
        return stacks.values();
    }

    @Override
    @Nonnull
    public IStackList<ItemStack> copy() {
        ItemStackList list = new ItemStackList();

        for (Map.Entry<ItemMeta, StackListEntry<ItemStack>> pair : stacks.entrySet()) {
            StackListEntry<ItemStack> entry = pair.getValue();
            ItemStack newStack = entry.getStack().copy();

            // Can reuse key because it is immutable
            ItemMeta itemMeta = pair.getKey();
            list.stacks.put(itemMeta, new StackListEntry<>(entry.getId(), newStack));
            list.index.put(entry.getId(), newStack);
            list.itemsByType.put(itemMeta.getItem(), itemMeta);
        }

        return list;
    }

    @Override
    public int size() {
        return stacks.size();
    }

    private static final class ItemMeta {
        private final boolean isEmpty;
        private final Item item;
        private final CompoundNBT nbt;

        private ItemMeta(ItemStack template) {
            isEmpty = template.isEmpty();
            item = template.getItem();
            CompoundNBT originalNbt = template.getTag();
            nbt = originalNbt == null ? null : originalNbt.copy();
        }

        public Item getItem() {
            return item;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ItemMeta itemMeta = (ItemMeta) o;
            return isEmpty == itemMeta.isEmpty &&
                    item.equals(itemMeta.item) &&
                    Objects.equals(nbt, itemMeta.nbt);
        }

        @Override
        public int hashCode() {
            return Objects.hash(isEmpty, item, nbt);
        }
    }
}
