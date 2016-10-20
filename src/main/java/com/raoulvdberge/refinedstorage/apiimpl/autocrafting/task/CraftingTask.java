package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.IProcessable;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IFluidStackList;
import com.raoulvdberge.refinedstorage.api.util.IItemStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementError;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementFluidRender;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementItemRender;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementText;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementFluidStack;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementItemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class CraftingTask implements ICraftingTask {
    private static final int DEFAULT_COMPARE = IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT;

    public static final String NBT_TO_PROCESS = "ToProcess";
    public static final String NBT_TO_TAKE = "ToTake";
    public static final String NBT_INTERNAL_TO_TAKE = "InternalToTake";
    public static final String NBT_TO_TAKE_FLUIDS = "ToTakeFluids";
    public static final String NBT_TO_INSERT = "ToInsert";
    public static final String NBT_TOOK = "Took";
    public static final String NBT_TOOK_FLUIDS = "TookFluids";

    private INetworkMaster network;
    @Nullable
    private ItemStack requested;
    private ICraftingPattern pattern;
    private int quantity;
    private List<IProcessable> toProcess = new ArrayList<>();
    private IItemStackList toTake = API.instance().createItemStackList();
    private IItemStackList internalToTake = API.instance().createItemStackList();
    private IItemStackList toCraft = API.instance().createItemStackList();
    private IFluidStackList toTakeFluids = API.instance().createFluidStackList();
    private IItemStackList missing = API.instance().createItemStackList();
    private Set<ICraftingPattern> usedPatterns = new HashSet<>();
    private boolean recurseFound = false;
    private Deque<ItemStack> toInsert = new ArrayDeque<>();
    private IItemStackList took = API.instance().createItemStackList();
    private List<FluidStack> tookFluids = new ArrayList<>();

    public CraftingTask(INetworkMaster network, @Nullable ItemStack requested, ICraftingPattern pattern, int quantity) {
        this.network = network;
        this.requested = requested;
        this.pattern = pattern;
        this.quantity = quantity;
    }

    public CraftingTask(INetworkMaster network, @Nullable ItemStack requested, ICraftingPattern pattern, int quantity, List<IProcessable> toProcess, IItemStackList toTake, IItemStackList internalToTake, IFluidStackList toTakeFluids, Deque<ItemStack> toInsert, IItemStackList took, List<FluidStack> tookFluids) {
        this(network, requested, pattern, quantity);

        this.toProcess = toProcess;
        this.toTake = toTake;
        this.internalToTake = internalToTake;
        this.toTakeFluids = toTakeFluids;
        this.toInsert = toInsert;
        this.took = took;
        this.tookFluids = tookFluids;
    }

    public INetworkMaster getNetwork() {
        return network;
    }

    @Override
    public void calculate() {
        IItemStackList networkList = network.getItemStorageCache().getList().copy();
        IItemStackList toInsert = API.instance().createItemStackList();

        toCraft.add(ItemHandlerHelper.copyStackWithSize(requested, quantity));
        int quantity = this.quantity;
        while (quantity > 0 && !recurseFound) {
            calculate(networkList, pattern, toInsert);
            quantity -= pattern.getQuantityPerRequest(requested);
        }

        if (!recurseFound) {
            this.toInsert.addAll(toInsert.getStacks());
        }

        usedPatterns.clear();
    }

    private void calculate(IItemStackList networkList, ICraftingPattern pattern, IItemStackList toInsert) {
        recurseFound = !usedPatterns.add(pattern);
        if (recurseFound) {
            return;
        }

        int compare = DEFAULT_COMPARE | (pattern.isOredict() ? IComparer.COMPARE_OREDICT : 0);
        ItemStack[] took = new ItemStack[9];

        IItemStackList inputs = API.instance().createItemStackList();
        IItemStackList actualInputs = API.instance().createItemStackList();

        for (ItemStack input : pattern.getInputs()) {
            if (input != null) {
                inputs.add(input.copy());
            }
        }

        for (ItemStack input : inputs.getStacks()) {
            ItemStack extraStack = toInsert.get(input, compare);
            ItemStack networkStack = networkList.get(input, compare);

            while (input.stackSize > 0) {
                if (extraStack != null && extraStack.stackSize > 0) {
                    int takeQuantity = Math.min(extraStack.stackSize, input.stackSize);
                    ItemStack inputStack = ItemHandlerHelper.copyStackWithSize(extraStack, takeQuantity);
                    actualInputs.add(inputStack.copy());
                    input.stackSize -= takeQuantity;
                    toInsert.remove(inputStack, true);
                } else if (networkStack != null && networkStack.stackSize > 0) {
                    int takeQuantity = Math.min(networkStack.stackSize, input.stackSize);
                    ItemStack inputStack = ItemHandlerHelper.copyStackWithSize(networkStack, takeQuantity);
                    toTake.add(inputStack.copy());
                    actualInputs.add(inputStack.copy());
                    input.stackSize -= takeQuantity;
                    networkList.remove(inputStack, true);
                } else {
                    ICraftingPattern inputPattern = network.getPattern(input, compare);

                    if (inputPattern != null) {
                        int craftQuantity = Math.min(inputPattern.getQuantityPerRequest(input, compare), input.stackSize);
                        ItemStack inputCrafted = ItemHandlerHelper.copyStackWithSize(input, craftQuantity);
                        toCraft.add(inputCrafted.copy());
                        actualInputs.add(inputCrafted.copy());
                        calculate(networkList, inputPattern, toInsert);
                        input.stackSize -= craftQuantity;
                        // Calculate added all the crafted outputs toInsert
                        // So we remove the ones we use from toInsert
                        toInsert.remove(inputCrafted, true);
                        // If the pattern is processing the have to be taken.
                        if (pattern.isProcessing()) {
                            internalToTake.add(inputCrafted.copy());
                        }
                    } else if (doFluidCalculation(networkList, input, toInsert)) {
                        actualInputs.add(ItemHandlerHelper.copyStackWithSize(input, 1));
                        input.stackSize -= 1;
                    } else {
                        missing.add(input.copy());
                        input.stackSize = 0;
                    }
                }
            }
        }

        if (pattern.isProcessing()) {
            toProcess.add(new Processable(this));
        }

        if (missing.isEmpty()) {
            for (int i = 0; i < pattern.getInputs().size(); i++) {
                ItemStack input = pattern.getInputs().get(i);
                if (input != null) {
                    ItemStack actualInput = actualInputs.get(input, compare);
                    ItemStack taken = ItemHandlerHelper.copyStackWithSize(actualInput, input.stackSize);
                    took[i] = taken;
                    actualInputs.remove(taken, true);
                }
            }
        }

        if (!pattern.isProcessing()) {
            for (ItemStack byproduct : (pattern.isOredict() && missing.isEmpty() ? pattern.getByproducts(took) : pattern.getByproducts())) {
                toInsert.add(byproduct.copy());
            }

            for (ItemStack output : (pattern.isOredict() && missing.isEmpty() ? pattern.getOutputs(took) : pattern.getOutputs())) {
                toInsert.add(output.copy());
            }
        }

        usedPatterns.remove(pattern);
    }

    private boolean doFluidCalculation(IItemStackList networkList, ItemStack input, IItemStackList toInsert) {
        FluidStack fluidInItem = RSUtils.getFluidFromStack(input, true);

        if (fluidInItem != null && RSUtils.hasFluidBucket(fluidInItem)) {
            FluidStack fluidInStorage = network.getFluidStorageCache().getList().get(fluidInItem);

            if (fluidInStorage == null || fluidInStorage.amount < fluidInItem.amount) {
                missing.add(input);
            } else {
                boolean hasBucket = networkList.get(RSUtils.EMPTY_BUCKET) != null;
                ICraftingPattern bucketPattern = network.getPattern(RSUtils.EMPTY_BUCKET);

                if (!hasBucket) {
                    if (bucketPattern == null) {
                        missing.add(RSUtils.EMPTY_BUCKET.copy());
                    } else {
                        toCraft.add(RSUtils.EMPTY_BUCKET.copy());
                        calculate(networkList, bucketPattern, toInsert);
                    }
                }

                if (hasBucket || bucketPattern != null) {
                    toTakeFluids.add(fluidInItem.copy());
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public void onCancelled() {
        for (ItemStack stack : took.getStacks()) {
            network.insertItem(stack, stack.stackSize, false);
        }

        for (FluidStack stack : tookFluids) {
            network.insertFluid(stack, stack.amount, false);
        }
    }

    @Override
    public String toString() {
        return "\nCraftingTask{quantity=" + quantity +
            "\n, toTake=" + toTake +
            "\n, internalToTake=" + internalToTake +
            "\n, toTakeFluids=" + toTakeFluids +
            "\n, toProcess=" + toProcess +
            "\n, toCraft=" + toProcess +
            "\n, toInsert=" + toInsert +
            '}';
    }

    @Override
    public boolean update() {
        for (ItemStack stack : toTake.getStacks()) {
            ItemStack stackExtracted = network.extractItem(stack, Math.min(stack.stackSize, 64));

            if (stackExtracted != null) {
                toTake.remove(stack, stackExtracted.stackSize, true);

                took.add(stackExtracted);

                network.sendCraftingMonitorUpdate();

                break;
            }
        }

        // Fetches results from processing patterns
        for (ItemStack stack : internalToTake.getStacks()) {
            ItemStack stackExtracted = network.extractItem(stack, Math.min(stack.stackSize, 64));

            if (stackExtracted != null) {
                internalToTake.remove(stack, stackExtracted.stackSize, false);

                took.add(stackExtracted);

                network.sendCraftingMonitorUpdate();
            }
        }
        // Clean up zero stacks, cause we can't remove them in the loop (CME ahoy!)
        internalToTake.clean();

        for (IProcessable processable : toProcess) {
            IItemHandler inventory = processable.getPattern().getContainer().getFacingInventory();

            if (inventory != null && !processable.hasStartedProcessing() && processable.canStartProcessing(took) && canProcess(processable)) {
                processable.setStartedProcessing();

                for (ItemStack insertStack : processable.getToInsert().getStacks()) {
                    ItemStack tookStack = took.get(insertStack, DEFAULT_COMPARE | (processable.getPattern().isOredict() ? IComparer.COMPARE_OREDICT : 0));
                    ItemStack toInsert = ItemHandlerHelper.copyStackWithSize(tookStack, insertStack.stackSize);

                    if (ItemHandlerHelper.insertItem(inventory, toInsert, true) == null) {
                        ItemHandlerHelper.insertItem(inventory, toInsert, false);

                        took.remove(tookStack, toInsert.stackSize, true);

                        network.sendCraftingMonitorUpdate();
                    }
                }
            }
        }

        // If we took all the items, we can start taking fluids
        if (toTake.isEmpty()) {
            for (FluidStack stack : toTakeFluids.getStacks()) {
                FluidStack stackExtracted = network.extractFluid(stack, stack.amount);

                if (stackExtracted != null) {
                    toTakeFluids.remove(stack, stack.amount, true);

                    tookFluids.add(stackExtracted);

                    network.sendCraftingMonitorUpdate();

                    break;
                }
            }
        }

        if (isFinished()) {
            ItemStack insert = toInsert.peek();

            if (insert != null && network.insertItem(insert, insert.stackSize, true) == null) {
                network.insertItem(insert, insert.stackSize, false);

                toInsert.pop();

                network.sendCraftingMonitorUpdate();
            }

            return toInsert.isEmpty();
        }

        return false;
    }

    private boolean canProcess(IProcessable processable) {
        for (ICraftingTask otherTask : network.getCraftingTasks()) {
            for (IProcessable otherProcessable : otherTask.getToProcess()) {
                if (otherProcessable != processable && !otherProcessable.hasReceivedOutputs() && otherProcessable.hasStartedProcessing() && otherProcessable.getPattern().getContainer().getFacingTile() != null) {
                    if (!arePatternsEqual(processable.getPattern(), otherProcessable.getPattern())) {
                        if (processable.getPattern().getContainer().getFacingTile().getPos().equals(otherProcessable.getPattern().getContainer().getFacingTile().getPos())) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    private boolean arePatternsEqual(ICraftingPattern left, ICraftingPattern right) {
        for (int i = 0; i < 9; ++i) {
            ItemStack leftStack = left.getInputs().get(i);
            ItemStack rightStack = right.getInputs().get(i);

            if (!API.instance().getComparer().isEqual(leftStack, rightStack)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Nullable
    @Override
    public ItemStack getRequested() {
        return requested;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        writeDefaultsToNBT(tag);

        NBTTagList processablesList = new NBTTagList();

        for (IProcessable processable : toProcess) {
            processablesList.appendTag(processable.writeToNBT(new NBTTagCompound()));
        }

        tag.setTag(NBT_TO_PROCESS, processablesList);

        tag.setTag(NBT_TO_TAKE, RSUtils.serializeItemStackList(toTake));
        tag.setTag(NBT_INTERNAL_TO_TAKE, RSUtils.serializeItemStackList(internalToTake));
        tag.setTag(NBT_TO_TAKE_FLUIDS, RSUtils.serializeFluidStackList(toTakeFluids));

        NBTTagList toInsertList = new NBTTagList();

        for (ItemStack insert : new ArrayList<>(toInsert)) {
            toInsertList.appendTag(insert.serializeNBT());
        }

        tag.setTag(NBT_TO_INSERT, toInsertList);

        tag.setTag(NBT_TOOK, RSUtils.serializeItemStackList(took));

        NBTTagList fluidsTookList = new NBTTagList();

        for (FluidStack took : this.tookFluids) {
            fluidsTookList.appendTag(took.writeToNBT(new NBTTagCompound()));
        }

        tag.setTag(NBT_TOOK_FLUIDS, fluidsTookList);

        return tag;
    }

    @Override
    public List<ICraftingMonitorElement> getCraftingMonitorElements() {
        List<ICraftingMonitorElement> elements = new ArrayList<>();

        elements.add(new CraftingMonitorElementItemRender(
            network.getCraftingTasks().indexOf(this),
            requested != null ? requested : pattern.getOutputs().get(0),
            quantity,
            0
        ));

        if (isFinished() && !toInsert.isEmpty()) {
            elements.add(new CraftingMonitorElementText("gui.refinedstorage:crafting_monitor.items_inserting", 16));

            elements.addAll(toInsert.stream()
                .map(stack -> new CraftingMonitorElementItemRender(
                    -1,
                    stack,
                    stack.stackSize,
                    32
                ))
                .collect(Collectors.toList())
            );
        } else {
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
                            ICraftingMonitorElement element = new CraftingMonitorElementItemRender(
                                -1,
                                processable.getPattern().getOutputs().get(i),
                                processable.getPattern().getOutputs().get(i).stackSize,
                                32
                            );

                            if (processable.getPattern().getContainer().getFacingTile() == null) {
                                element = new CraftingMonitorElementError(element, "gui.refinedstorage:crafting_monitor.machine_none");
                            } else if (!canProcess(processable)) {
                                element = new CraftingMonitorElementError(element, "gui.refinedstorage:crafting_monitor.machine_in_use");
                            }

                            elements.add(element);
                        }
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

    @Override
    public boolean isValid() {
        return !recurseFound;
    }

    @Override
    public List<ICraftingPreviewElement> getPreviewStacks() {
        if (!isValid()) {
            return Collections.emptyList();
        }

        Map<Integer, CraftingPreviewElementItemStack> map = new LinkedHashMap<>();

        for (ItemStack stack : toCraft.getStacks()) {
            int hash = API.instance().getItemStackHashCode(stack);
            CraftingPreviewElementItemStack previewStack = map.get(hash);
            if (previewStack == null) {
                previewStack = new CraftingPreviewElementItemStack(stack);
            }
            previewStack.addToCraft(stack.stackSize);
            map.put(hash, previewStack);
        }

        for (ItemStack stack : missing.getStacks()) {
            int hash = API.instance().getItemStackHashCode(stack);
            CraftingPreviewElementItemStack previewStack = map.get(hash);
            if (previewStack == null) {
                previewStack = new CraftingPreviewElementItemStack(stack);
            }
            previewStack.setMissing(true);
            previewStack.addToCraft(stack.stackSize);
            map.put(hash, previewStack);
        }

        for (ItemStack stack : toTake.getStacks()) {
            int hash = API.instance().getItemStackHashCode(stack);
            CraftingPreviewElementItemStack previewStack = map.get(hash);
            if (previewStack == null) {
                previewStack = new CraftingPreviewElementItemStack(stack);
            }
            previewStack.addAvailable(stack.stackSize);
            map.put(hash, previewStack);
        }

        List<ICraftingPreviewElement> elements = new ArrayList<>(map.values());

        toTakeFluids.getStacks().stream().map(CraftingPreviewElementFluidStack::new).forEach(elements::add);

        return elements;
    }

    private boolean isFinished() {
        return toTake.isEmpty() && internalToTake.isEmpty() && toTakeFluids.isEmpty() && missing.isEmpty() && hasProcessedItems();
    }

    private boolean hasProcessedItems() {
        return toProcess.stream().allMatch(IProcessable::hasReceivedOutputs);
    }
}
