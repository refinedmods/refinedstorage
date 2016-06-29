package refinedstorage.apiimpl.storage;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.INetworkSlave;
import refinedstorage.api.storage.IGroupedStorage;
import refinedstorage.api.storage.IStorage;
import refinedstorage.api.storage.IStorageProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GroupedStorage implements IGroupedStorage {
    private List<IStorage> storages = new ArrayList<IStorage>();
    private Multimap<Item, ItemStack> stacks = ArrayListMultimap.create();
    private INetworkMaster network;
    private boolean rebuilding;

    public GroupedStorage(INetworkMaster network) {
        this.network = network;
    }

    @Override
    public void rebuild() {
        rebuilding = true;

        storages.clear();

        for (INetworkSlave slave : network.getSlaves()) {
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

        rebuilding = false;
    }

    @Override
    public List<IStorage> getStorages() {
        return storages;
    }

    @Override
    public void add(ItemStack stack) {
        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (RefinedStorageUtils.compareStackNoQuantity(otherStack, stack)) {
                otherStack.stackSize += stack.stackSize;

                return;
            }
        }

        stacks.put(stack.getItem(), stack.copy());

        if (!rebuilding) {
            network.sendStorageDelta(stack, stack.stackSize);
        }
    }

    @Override
    public void remove(ItemStack stack) {
        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (RefinedStorageUtils.compareStackNoQuantity(otherStack, stack)) {
                otherStack.stackSize -= stack.stackSize;

                if (otherStack.stackSize == 0) {
                    stacks.remove(otherStack.getItem(), otherStack);
                }

                network.sendStorageDelta(stack, -stack.stackSize);

                return;
            }
        }
    }

    @Override
    public ItemStack get(ItemStack stack, int flags) {
        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (RefinedStorageUtils.compareStack(otherStack, stack, flags)) {
                return otherStack;
            }
        }

        return null;
    }

    @Override
    public Collection<ItemStack> getStacks() {
        return stacks.values();
    }
}
