package refinedstorage.apiimpl.autocrafting.v2;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.NetworkUtils;
import refinedstorage.api.storage.CompareUtils;

import java.util.ArrayDeque;
import java.util.Deque;

public class CraftingTask {
    private INetworkMaster network;
    private ICraftingPattern pattern;
    private int quantity;
    private Deque<ItemStack> toTake = new ArrayDeque<>();
    private Multimap<Item, ItemStack> toCraft = ArrayListMultimap.create();
    private Multimap<Item, ItemStack> missing = ArrayListMultimap.create();

    public CraftingTask(INetworkMaster network, ICraftingPattern pattern, int quantity) {
        this.network = network;
        this.pattern = pattern;
        this.quantity = quantity;
    }

    public void calculate() {
        calculate(pattern, true);
    }

    private void calculate(ICraftingPattern pattern, boolean basePattern) {
        for (int i = 0; i < quantity; ++i) {
            for (ItemStack input : pattern.getInputs()) {
                ItemStack inputInNetwork = network.getItemStorage().get(input, CompareUtils.COMPARE_DAMAGE | CompareUtils.COMPARE_NBT);

                if (inputInNetwork == null || inputInNetwork.stackSize == 0) {
                    ICraftingPattern inputPattern = NetworkUtils.getPattern(network, input);

                    if (inputPattern != null) {
                        addToCraft(input);

                        calculate(inputPattern, false);
                    } else {
                        addMissing(input);
                    }
                } else {
                    toTake.push(input);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "quantity=" + quantity + ",toTake=" + toTake.toString() + ",toCraft=" + toCraft.toString() + ",missing=" + missing.toString();
    }

    public boolean update() {
        if (!toTake.isEmpty()) {
            ItemStack took = NetworkUtils.extractItem(network, toTake.peek(), 1);

            if (took != null) {
                toTake.pop();
            }
        }

        if (toTake.isEmpty() && missing.isEmpty()) {
            for (ItemStack output : pattern.getOutputs()) {
                // @TODO: Handle remainder
                network.insertItem(output, output.stackSize, false);
            }

            for (ItemStack byproduct : pattern.getByproducts()) {
                // @TODO: Handle remainder
                network.insertItem(byproduct, byproduct.stackSize, false);
            }

            return true;
        }

        return false;
    }

    private void addMissing(ItemStack stack) {
        for (ItemStack m : missing.get(stack.getItem())) {
            if (CompareUtils.compareStackNoQuantity(m, stack)) {
                m.stackSize += stack.stackSize;

                return;
            }
        }

        missing.put(stack.getItem(), stack.copy());
    }

    private void addToCraft(ItemStack stack) {
        for (ItemStack m : toCraft.get(stack.getItem())) {
            if (CompareUtils.compareStackNoQuantity(m, stack)) {
                m.stackSize += stack.stackSize;

                return;
            }
        }

        toCraft.put(stack.getItem(), stack.copy());
    }
}
