package refinedstorage.apiimpl.storage;

import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.INetworkSlave;
import refinedstorage.api.storage.IItemList;
import refinedstorage.api.storage.IStorage;
import refinedstorage.api.storage.IStorageProvider;

import java.util.ArrayList;
import java.util.List;

public class ItemList implements IItemList {
    private List<IStorage> storages = new ArrayList<IStorage>();
    private List<ItemStack> stacks = new ArrayList<ItemStack>();

    @Override
    public void rebuild(INetworkMaster master) {
        storages.clear();

        for (INetworkSlave slave : master.getSlaves()) {
            if (slave.canUpdate() && slave instanceof IStorageProvider) {
                ((IStorageProvider) slave).addStorages(storages);
            }
        }

        stacks.clear();

        for (IStorage storage : storages) {
            for (ItemStack stack : storage.getItems()) {
                add(stack);
            }
        }
    }

    @Override
    public List<IStorage> getStorages() {
        return storages;
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
