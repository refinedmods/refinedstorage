package refinedstorage.apiimpl.storage;

import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.storage.IItemList;
import refinedstorage.api.storage.IStorageProvider;

import java.util.List;

public class ItemList implements IItemList {
    private List<ItemStack> stacks;

    @Override
    public void rebuild(List<IStorageProvider> providers) {
        stacks.clear();
    }

    @Override
    public void add(ItemStack stack) {
        for (ItemStack otherStack : stacks) {
            if (RefinedStorageUtils.compareStackNoQuantity(otherStack, stack)) {
                otherStack.stackSize += stack.stackSize;

                return;
            }
        }

        stacks.add(stack.copy());
    }

    @Override
    public void remove(ItemStack stack) {
        for (ItemStack otherStack : stacks) {
            if (RefinedStorageUtils.compareStackNoQuantity(otherStack, stack)) {
                otherStack.stackSize -= stack.stackSize;

                if (otherStack.stackSize == 0) {
                    stacks.remove(otherStack);
                }

                return;
            }
        }
    }

    @Override
    public ItemStack get(ItemStack stack, int flags) {
        for (ItemStack otherStack : stacks) {
            if (RefinedStorageUtils.compareStack(otherStack, stack, flags)) {
                return otherStack;
            }
        }

        return null;
    }

    @Override
    public List<ItemStack> getStacks() {
        return stacks;
    }
}
