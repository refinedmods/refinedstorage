package refinedstorage.apiimpl.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import refinedstorage.api.RSAPI;
import refinedstorage.api.network.NetworkUtils;
import refinedstorage.api.util.IItemStackList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public class ItemStackList implements IItemStackList {
    private Multimap<Item, ItemStack> stacks = ArrayListMultimap.create();

    public void add(ItemStack stack) {
        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (RSAPI.instance().getComparer().isEqualNoQuantity(otherStack, stack)) {
                otherStack.stackSize += stack.stackSize;

                return;
            }
        }

        stacks.put(stack.getItem(), stack.copy());
    }

    public boolean remove(@Nonnull ItemStack stack, boolean removeIfReachedZero) {
        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (RSAPI.instance().getComparer().isEqualNoQuantity(otherStack, stack)) {
                otherStack.stackSize -= stack.stackSize;

                if (otherStack.stackSize == 0 && removeIfReachedZero) {
                    stacks.remove(otherStack.getItem(), otherStack);
                }

                return true;
            }
        }

        return false;
    }

    @Nullable
    public ItemStack get(@Nonnull ItemStack stack, int flags) {
        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (RSAPI.instance().getComparer().isEqual(otherStack, stack, flags)) {
                return otherStack;
            }
        }

        return null;
    }

    @Nullable
    public ItemStack get(int hash) {
        for (ItemStack stack : this.stacks.values()) {
            if (NetworkUtils.getItemStackHashCode(stack) == hash) {
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
