package refinedstorage.apiimpl.util;

import com.google.common.collect.ArrayListMultimap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import refinedstorage.api.util.IItemStackList;
import refinedstorage.apiimpl.API;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public class ItemStackList implements IItemStackList {
    private ArrayListMultimap<Item, ItemStack> stacks = ArrayListMultimap.create();

    @Override
    public void add(ItemStack stack) {
        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (API.instance().getComparer().isEqualNoQuantity(otherStack, stack)) {
                otherStack.stackSize += stack.stackSize;

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

                if (otherStack.stackSize <= 0 && removeIfReachedZero) {
                    stacks.remove(otherStack.getItem(), otherStack);
                }

                return true;
            }
        }

        return false;
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
    public IItemStackList copy() {
        ItemStackList list = new ItemStackList();

        for (ItemStack stack : stacks.values()) {
            list.add(stack.copy());
        }

        return list;
    }
}
