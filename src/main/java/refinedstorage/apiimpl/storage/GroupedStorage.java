package refinedstorage.apiimpl.storage;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.INetworkNode;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.api.storage.IGroupedStorage;
import refinedstorage.api.storage.IStorage;
import refinedstorage.api.storage.IStorageProvider;
import refinedstorage.apiimpl.network.NetworkUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GroupedStorage implements IGroupedStorage {
    private INetworkMaster network;
    private List<IStorage> storages = new ArrayList<IStorage>();
    private Multimap<Item, ItemStack> stacks = ArrayListMultimap.create();

    public GroupedStorage(INetworkMaster network) {
        this.network = network;
    }

    @Override
    public void rebuild() {
        storages.clear();

        for (INetworkNode node : network.getNodes()) {
            if (node.canUpdate() && node instanceof IStorageProvider) {
                ((IStorageProvider) node).addStorages(storages);
            }
        }

        stacks.clear();

        for (IStorage storage : storages) {
            for (ItemStack stack : storage.getItems()) {
                add(stack, true);
            }
        }

        for (ICraftingPattern pattern : network.getPatterns()) {
            for (ItemStack output : pattern.getOutputs()) {
                ItemStack patternStack = output.copy();
                patternStack.stackSize = 0;
                add(patternStack, true);
            }
        }

        network.sendStorageToClient();
    }

    @Override
    public void add(ItemStack stack, boolean rebuilding) {
        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (CompareUtils.compareStackNoQuantity(otherStack, stack)) {
                otherStack.stackSize += stack.stackSize;

                if (!rebuilding) {
                    network.sendStorageDeltaToClient(stack, stack.stackSize);
                }

                return;
            }
        }

        stacks.put(stack.getItem(), stack.copy());

        if (!rebuilding) {
            network.sendStorageDeltaToClient(stack, stack.stackSize);
        }
    }

    @Override
    public void remove(ItemStack stack) {
        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (CompareUtils.compareStackNoQuantity(otherStack, stack)) {
                otherStack.stackSize -= stack.stackSize;

                if (otherStack.stackSize == 0) {
                    if (!NetworkUtils.hasPattern(network, stack)) {
                        stacks.remove(otherStack.getItem(), otherStack);
                    }
                }

                network.sendStorageDeltaToClient(stack, -stack.stackSize);

                return;
            }
        }
    }

    @Override
    public ItemStack get(ItemStack stack, int flags) {
        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (CompareUtils.compareStack(otherStack, stack, flags)) {
                return otherStack;
            }
        }

        return null;
    }

    @Override
    public ItemStack get(int id) {
        for (ItemStack stack : this.stacks.values()) {
            if (NetworkUtils.getItemStackHashCode(stack) == id) {
                return stack;
            }
        }

        return null;
    }

    @Override
    public Collection<ItemStack> getStacks() {
        return stacks.values();
    }

    @Override
    public List<IStorage> getStorages() {
        return storages;
    }
}
