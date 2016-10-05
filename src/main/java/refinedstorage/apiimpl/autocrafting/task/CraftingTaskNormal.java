package refinedstorage.apiimpl.autocrafting.task;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.task.ICraftingTask;
import refinedstorage.api.autocrafting.task.IProcessable;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.NetworkUtils;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.api.storage.item.IGroupedItemStorage;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class CraftingTaskNormal implements ICraftingTask {
    private INetworkMaster network;
    private ICraftingPattern pattern;
    private int quantity;
    private Deque<ItemStack> toTake = new ArrayDeque<>();
    private List<IProcessable> toProcess = new ArrayList<>();
    private Multimap<Item, ItemStack> toCraft = ArrayListMultimap.create();
    private Multimap<Item, ItemStack> missing = ArrayListMultimap.create();
    private Multimap<Item, ItemStack> extras = ArrayListMultimap.create();

    public CraftingTaskNormal(INetworkMaster network, ICraftingPattern pattern, int quantity) {
        this.network = network;
        this.pattern = pattern;
        this.quantity = quantity;
    }

    public void calculate() {
        calculate(network.getItemStorage().copy(), pattern, true);
    }

    @Override
    public void onCancelled() {

    }

    private void calculate(IGroupedItemStorage storage, ICraftingPattern pattern, boolean basePattern) {
        for (int i = 0; i < quantity; ++i) {
            if (pattern.isProcessing()) {
                IProcessable processable = new Processable(pattern);

                for (int j = pattern.getInputs().size() - 1; j >= 0; --j) {
                    processable.getToInsert().push(pattern.getInputs().get(j).copy());
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
                    if (!pattern.isProcessing()) {
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
        for (IProcessable processable : toProcess) {
            if (processable.getPattern().getContainer().getFacingInventory() != null && !processable.getToInsert().isEmpty()) {
                ItemStack toInsert = NetworkUtils.extractItem(network, processable.getToInsert().peek(), 1);

                if (ItemHandlerHelper.insertItem(processable.getPattern().getContainer().getFacingInventory(), toInsert, true) == null) {
                    ItemHandlerHelper.insertItem(processable.getPattern().getContainer().getFacingInventory(), toInsert, false);

                    processable.getToInsert().pop();
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

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        return tag;
    }

    @Override
    public ICraftingPattern getPattern() {
        return pattern;
    }

    @Override
    public Deque<ItemStack> getToTake() {
        return toTake;
    }

    @Override
    public Multimap<Item, ItemStack> getToCraft() {
        return toCraft;
    }

    @Override
    public Multimap<Item, ItemStack> getMissing() {
        return missing;
    }

    @Override
    public List<IProcessable> getToProcess() {
        return toProcess;
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
