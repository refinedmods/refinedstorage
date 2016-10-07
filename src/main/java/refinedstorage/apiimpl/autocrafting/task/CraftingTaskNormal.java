package refinedstorage.apiimpl.autocrafting.task;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.api.RSAPI;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import refinedstorage.api.autocrafting.task.ICraftingTask;
import refinedstorage.api.autocrafting.task.IProcessable;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.util.IItemStackList;
import refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementRoot;
import refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementToTake;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CraftingTaskNormal implements ICraftingTask {
    private INetworkMaster network;
    private ItemStack requested;
    private ICraftingPattern pattern;
    private int quantity;
    private List<IProcessable> toProcess = new ArrayList<>();
    private IItemStackList toTake = RSAPI.instance().createItemStackList();
    private IItemStackList toCraft = RSAPI.instance().createItemStackList();
    private IItemStackList missing = RSAPI.instance().createItemStackList();
    private IItemStackList extras = RSAPI.instance().createItemStackList();

    public CraftingTaskNormal(INetworkMaster network, ItemStack requested, ICraftingPattern pattern, int quantity) {
        this.network = network;
        this.requested = requested;
        this.pattern = pattern;
        this.quantity = quantity;
    }

    public void calculate() {
        IItemStackList list = network.getItemStorage().getList().copy();

        int newQuantity = quantity;

        while (newQuantity > 0) {
            calculate(list, pattern, true);

            newQuantity -= requested == null ? newQuantity : pattern.getQuantityPerRequest(requested);
        }
    }

    private void calculate(IItemStackList list, ICraftingPattern pattern, boolean basePattern) {
        if (pattern.isProcessing()) {
            toProcess.add(new Processable(pattern));
        }

        for (ItemStack input : pattern.getInputs()) {
            ItemStack inputInNetwork = list.get(input);

            if (inputInNetwork == null || inputInNetwork.stackSize == 0) {
                if (extras.get(input) != null) {
                    decrOrRemoveExtras(input);
                } else {
                    ICraftingPattern inputPattern = network.getPattern(input);

                    if (inputPattern != null) {
                        for (ItemStack output : inputPattern.getOutputs()) {
                            toCraft.add(output);
                        }

                        calculate(list, inputPattern, false);
                    } else {
                        missing.add(input);
                    }
                }
            } else {
                if (!pattern.isProcessing()) {
                    toTake.add(input);
                }

                list.remove(input, true);
            }
        }

        if (!basePattern) {
            addExtras(pattern);
        }
    }

    @Override
    public void onCancelled() {
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
            if (processable.getPattern().getContainer().getFacingInventory() != null && processable.getStackToInsert() != null) {
                ItemStack toInsert = network.extractItem(processable.getStackToInsert(), 1);

                if (ItemHandlerHelper.insertItem(processable.getPattern().getContainer().getFacingInventory(), toInsert, true) == null) {
                    ItemHandlerHelper.insertItem(processable.getPattern().getContainer().getFacingInventory(), toInsert, false);

                    processable.nextStack();
                }
            }
        }

        for (ItemStack toTakeStack : toTake.getStacks()) {
            ItemStack took = network.extractItem(toTakeStack, 1);

            if (took != null) {
                toTake.remove(toTakeStack, 1, true);
            }

            break;
        }

        if (toTake.isEmpty() && missing.isEmpty() && toProcess.stream().allMatch(IProcessable::hasReceivedOutputs)) {
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
    public List<ICraftingMonitorElement> getCraftingMonitorElements() {
        List<ICraftingMonitorElement> elements = new ArrayList<>();

        elements.add(new CraftingMonitorElementRoot(
            network.getCraftingTasks().indexOf(this),
            pattern.getOutputs().get(0),
            quantity
        ));

        elements.addAll(toTake.getStacks().stream()
            .map(stack -> new CraftingMonitorElementToTake(stack, stack.stackSize))
            .collect(Collectors.toList())
        );

        return elements;
    }

    @Override
    public ICraftingPattern getPattern() {
        return pattern;
    }


    @Override
    public List<IProcessable> getToProcess() {
        return toProcess;
    }

    private void addExtras(ICraftingPattern pattern) {
        pattern.getOutputs().stream()
            .filter(o -> o.stackSize > 1)
            .forEach(o -> extras.add(ItemHandlerHelper.copyStackWithSize(o, o.stackSize - 1)));
    }

    private void decrOrRemoveExtras(ItemStack stack) {
        extras.remove(ItemHandlerHelper.copyStackWithSize(stack, 1), true);
    }
}
