package refinedstorage.apiimpl.storage;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.storage.IGroupedStorage;
import refinedstorage.api.storage.IStorage;

import java.util.Collection;

public class GroupedStorage implements IGroupedStorage {
    private INetworkMaster network;
    private Multimap<Item, ItemStack> stacks = ArrayListMultimap.create();
    private boolean rebuilding;

    public GroupedStorage(INetworkMaster network) {
        this.network = network;
    }

    @Override
    public void rebuild() {
        this.rebuilding = true;

        stacks.clear();

        for (IStorage storage : network.getStorages()) {
            for (ItemStack stack : storage.getItems()) {
                add(stack);
            }
        }

        for (ICraftingPattern pattern : network.getPatterns()) {
            for (ItemStack output : pattern.getOutputs()) {
                ItemStack patternStack = output.copy();
                patternStack.stackSize = 0;
                add(patternStack);
            }
        }

        this.rebuilding = false;

        network.sendStorageToClient();
    }

    @Override
    public boolean isRebuilding() {
        return rebuilding;
    }

    @Override
    public void add(ItemStack stack) {
        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (RefinedStorageUtils.compareStackNoQuantity(otherStack, stack)) {
                otherStack.stackSize += stack.stackSize;

                network.sendStorageToClient();

                return;
            }
        }

        stacks.put(stack.getItem(), stack.copy());

        network.sendStorageToClient();
    }

    @Override
    public void remove(ItemStack stack) {
        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (RefinedStorageUtils.compareStackNoQuantity(otherStack, stack)) {
                otherStack.stackSize -= stack.stackSize;

                if (otherStack.stackSize == 0) {
                    if (RefinedStorageUtils.getPatternFromNetwork(network, otherStack) == null) {
                        stacks.remove(otherStack.getItem(), otherStack);
                    }
                }

                network.sendStorageToClient();

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
