package refinedstorage.apiimpl.autocrafting.v2;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.NetworkUtils;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.api.storage.item.IGroupedItemStorage;
import refinedstorage.apiimpl.autocrafting.registry.CraftingTaskFactoryProcessing;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class CraftingTask {
    class ProcessablePattern {
        private ICraftingPattern pattern;
        private Deque<ItemStack> toInsert = new ArrayDeque<>();

        @Override
        public String toString() {
            return "ProcessablePattern{" +
                    "pattern=" + pattern +
                    ", toInsert=" + toInsert +
                    '}';
        }
    }

    private INetworkMaster network;
    private ICraftingPattern pattern;
    private int quantity;
    private Deque<ItemStack> toTake = new ArrayDeque<>();
    private List<ProcessablePattern> toProcess = new ArrayList<>();
    private Multimap<Item, ItemStack> toCraft = ArrayListMultimap.create();
    private Multimap<Item, ItemStack> missing = ArrayListMultimap.create();
    private Multimap<Item, ItemStack> extras = ArrayListMultimap.create();

    public CraftingTask(INetworkMaster network, ICraftingPattern pattern, int quantity) {
        this.network = network;
        this.pattern = pattern;
        this.quantity = quantity;
    }

    public void calculate() {
        calculate(network.getItemStorage().copy(), pattern, true);
    }

    private void calculate(IGroupedItemStorage storage, ICraftingPattern pattern, boolean basePattern) {
        for (int i = 0; i < quantity; ++i) {
            boolean isProcessing = pattern.getId().equals(CraftingTaskFactoryProcessing.ID);

            if (isProcessing) {
                ProcessablePattern processable = new ProcessablePattern();

                processable.pattern = pattern;

                for (int j = pattern.getInputs().size() - 1; j >= 0; --j) {
                    processable.toInsert.push(pattern.getInputs().get(j).copy());
                }

                toProcess.add(processable);
            }

            for (ItemStack input : pattern.getInputs()) {
                ItemStack inputInNetwork = storage.get(input, CompareUtils.COMPARE_DAMAGE | CompareUtils.COMPARE_NBT);

                if (inputInNetwork == null || inputInNetwork.stackSize == 0) {
                    if (getExtrasFor(input) != null) {
                        decrOrRemoveExtras(input);
                    } else {
                        ICraftingPattern inputPattern = NetworkUtils.getPattern(network, input);

                        if (inputPattern != null) {
                            for (ItemStack output : inputPattern.getOutputs()) {
                                addToCraft(output);
                            }

                            calculate(storage, inputPattern, false);
                        } else {
                            addMissing(input);
                        }
                    }
                } else {
                    if (!isProcessing) {
                        toTake.push(input);
                    }

                    storage.remove(input);
                }
            }

            if (!basePattern) {
                addExtras(pattern);
            }
        }
    }

    @Override
    public String toString() {
        return "\nCraftingTask{quantity=" + quantity +
                "\n, toTake=" + toTake +
                "\n, toCraft=" + toCraft +
                "\n, toProcess=" + toProcess +
                "\n, missing=" + missing +
                '}';
    }

    public boolean update() {
        for (ProcessablePattern processable : toProcess) {
            if (processable.pattern.getContainer().getFacingInventory() != null && !processable.toInsert.isEmpty()) {
                ItemStack toInsert = processable.toInsert.peek();

                if (ItemHandlerHelper.insertItem(processable.pattern.getContainer().getFacingInventory(), toInsert, true) == null) {
                    ItemHandlerHelper.insertItem(processable.pattern.getContainer().getFacingInventory(), toInsert, false);

                    processable.toInsert.pop();
                }
            }
        }

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

    private void addExtras(ICraftingPattern pattern) {
        pattern.getOutputs().stream().filter(o -> o.stackSize > 1).forEach(o -> addExtras(ItemHandlerHelper.copyStackWithSize(o, o.stackSize - 1)));
    }

    private void addExtras(ItemStack stack) {
        ItemStack extras = getExtrasFor(stack);

        if (extras != null) {
            extras.stackSize += stack.stackSize;
        } else {
            this.extras.put(stack.getItem(), stack.copy());
        }
    }

    private ItemStack getExtrasFor(ItemStack stack) {
        for (ItemStack m : extras.get(stack.getItem())) {
            if (CompareUtils.compareStackNoQuantity(m, stack)) {
                return m;
            }
        }

        return null;
    }

    private void decrOrRemoveExtras(ItemStack stack) {
        ItemStack extras = getExtrasFor(stack);

        extras.stackSize--;

        if (extras.stackSize == 0) {
            this.extras.remove(extras.getItem(), extras);
        }
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
