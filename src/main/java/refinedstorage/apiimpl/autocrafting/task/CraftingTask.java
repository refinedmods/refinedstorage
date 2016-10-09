package refinedstorage.apiimpl.autocrafting.task;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.RSUtils;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import refinedstorage.api.autocrafting.task.ICraftingTask;
import refinedstorage.api.autocrafting.task.IProcessable;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.util.IComparer;
import refinedstorage.api.util.IFluidStackList;
import refinedstorage.api.util.IItemStackList;
import refinedstorage.apiimpl.API;
import refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementFluidRender;
import refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementItemRender;
import refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementText;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class CraftingTask implements ICraftingTask {
    private INetworkMaster network;
    private ItemStack requested;
    private ICraftingPattern pattern;
    private int quantity;
    private List<IProcessable> toProcess = new ArrayList<>();
    private IItemStackList toTake = API.instance().createItemStackList();
    private IFluidStackList toTakeFluids = API.instance().createFluidStackList();
    private IItemStackList toCraft = API.instance().createItemStackList();
    private IItemStackList missing = API.instance().createItemStackList();
    private IItemStackList extras = API.instance().createItemStackList();
    private Deque<ItemStack> toInsert = new ArrayDeque<>();
    private int compare = IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT;

    public CraftingTask(INetworkMaster network, ItemStack requested, ICraftingPattern pattern, int quantity) {
        this.network = network;
        this.requested = requested;
        this.pattern = pattern;
        this.quantity = quantity;

        if (pattern.isOredict()) {
            this.compare = IComparer.COMPARE_OREDICT;
        }
    }

    public void calculate() {
        IItemStackList list = network.getItemStorage().getList().copy();

        int newQuantity = quantity;

        while (newQuantity > 0) {
            calculate(list, pattern, true);

            for (ItemStack output : pattern.getOutputs()) {
                toInsert.add(output.copy());
            }

            newQuantity -= requested == null ? newQuantity : pattern.getQuantityPerRequest(requested);
        }

        for (ItemStack extra : extras.getStacks()) {
            toInsert.add(extra.copy());
        }
    }

    private void calculate(IItemStackList list, ICraftingPattern pattern, boolean basePattern) {
        if (pattern.isProcessing()) {
            toProcess.add(new Processable(pattern));
        }

        if (!basePattern) {
            addExtras(pattern);
        }

        for (ItemStack input : pattern.getInputs()) {
            ItemStack inputInNetwork = list.get(input, compare);

            if (inputInNetwork == null || inputInNetwork.stackSize == 0) {
                ItemStack extra = extras.get(input, compare);

                if (extra != null) {
                    decrOrRemoveExtras(extra);
                } else {
                    ICraftingPattern inputPattern = network.getPattern(input, compare);

                    if (inputPattern != null) {
                        for (ItemStack output : inputPattern.getOutputs()) {
                            toCraft.add(output);
                        }

                        calculate(list, inputPattern, false);
                    } else {
                        FluidStack fluidInItem = RSUtils.getFluidFromStack(input, true);

                        if (fluidInItem != null && RSUtils.hasFluidBucket(fluidInItem)) {
                            FluidStack fluidInStorage = network.getFluidStorage().getList().get(fluidInItem);

                            if (fluidInStorage == null || fluidInStorage.amount < fluidInItem.amount) {
                                missing.add(input);
                            } else {
                                boolean hasBucket = network.getItemStorage().getList().get(RSUtils.EMPTY_BUCKET) != null;
                                ICraftingPattern bucketPattern = network.getPattern(RSUtils.EMPTY_BUCKET);

                                if (!hasBucket) {
                                    if (bucketPattern == null) {
                                        missing.add(RSUtils.EMPTY_BUCKET.copy());
                                    } else {
                                        calculate(list, bucketPattern, false);
                                    }
                                }

                                if (hasBucket || bucketPattern != null) {
                                    toTakeFluids.add(fluidInItem.copy());
                                }
                            }
                        } else {
                            missing.add(input);
                        }
                    }
                }
            } else {
                if (!pattern.isProcessing()) {
                    toTake.add(input);
                }

                list.remove(inputInNetwork, 1, true);
            }
        }

        for (ItemStack byproduct : pattern.getByproducts()) {
            extras.add(byproduct.copy());
        }
    }

    @Override
    public void onCancelled() {
    }

    @Override
    public String toString() {
        return "\nCraftingTask{quantity=" + quantity +
            "\n, toTake=" + toTake +
            "\n, toTakeFluids=" + toTakeFluids +
            "\n, toCraft=" + toCraft +
            "\n, toProcess=" + toProcess +
            "\n, missing=" + missing +
            '}';
    }

    public boolean update() {
        for (IProcessable processable : toProcess) {
            if (processable.getPattern().getContainer().getFacingInventory() != null && processable.getStackToInsert() != null) {
                ItemStack toInsert = network.extractItem(processable.getStackToInsert(), 1, compare);

                if (ItemHandlerHelper.insertItem(processable.getPattern().getContainer().getFacingInventory(), toInsert, true) == null) {
                    ItemHandlerHelper.insertItem(processable.getPattern().getContainer().getFacingInventory(), toInsert, false);

                    processable.nextStack();
                }
            }
        }

        for (ItemStack toTakeStack : toTake.getStacks()) {
            ItemStack took = network.extractItem(toTakeStack, 1, compare);

            if (took != null) {
                toTake.remove(toTakeStack, 1, true);
            }

            break;
        }

        // If we took all the items, we can start taking fluids
        if (toTake.isEmpty()) {
            for (FluidStack toTakeStack : toTakeFluids.getStacks()) {
                FluidStack took = network.extractFluid(toTakeStack, toTakeStack.amount);

                if (took != null) {
                    toTakeFluids.remove(toTakeStack, toTakeStack.amount, true);
                }

                break;
            }
        }

        if (toTake.isEmpty() && toTakeFluids.isEmpty() && missing.isEmpty() && hasProcessedItems()) {
            ItemStack insert = toInsert.peek();

            if (network.insertItem(insert, insert.stackSize, true) == null) {
                network.insertItem(insert, insert.stackSize, false);

                toInsert.pop();
            }

            return toInsert.isEmpty();
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

        elements.add(new CraftingMonitorElementItemRender(
            network.getCraftingTasks().indexOf(this),
            pattern.getOutputs().get(0),
            quantity,
            0
        ));

        if (!toTake.isEmpty()) {
            elements.add(new CraftingMonitorElementText("gui.refinedstorage:crafting_monitor.items_taking", 16));

            elements.addAll(toTake.getStacks().stream()
                .map(stack -> new CraftingMonitorElementItemRender(
                    -1,
                    stack,
                    stack.stackSize,
                    32
                ))
                .collect(Collectors.toList())
            );
        }

        if (!toTakeFluids.isEmpty()) {
            elements.add(new CraftingMonitorElementText("gui.refinedstorage:crafting_monitor.fluids_taking", 16));

            elements.addAll(toTakeFluids.getStacks().stream()
                .map(stack -> new CraftingMonitorElementFluidRender(
                    -1,
                    stack,
                    32
                ))
                .collect(Collectors.toList())
            );
        }

        if (!hasProcessedItems()) {
            elements.add(new CraftingMonitorElementText("gui.refinedstorage:crafting_monitor.items_processing", 16));

            for (IProcessable processable : toProcess) {
                for (int i = 0; i < processable.getPattern().getOutputs().size(); ++i) {
                    if (!processable.hasReceivedOutput(i)) {
                        elements.add(new CraftingMonitorElementItemRender(
                            -1,
                            processable.getPattern().getOutputs().get(i),
                            processable.getPattern().getOutputs().get(i).stackSize,
                            32
                        ));
                    }
                }
            }
        }

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

    private boolean hasProcessedItems() {
        return toProcess.stream().allMatch(IProcessable::hasReceivedOutputs);
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
