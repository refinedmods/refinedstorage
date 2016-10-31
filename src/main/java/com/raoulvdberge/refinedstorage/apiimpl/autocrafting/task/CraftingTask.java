package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementList;
import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingStep;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IFluidStackList;
import com.raoulvdberge.refinedstorage.api.util.IItemStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.*;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementFluidStack;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementItemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class CraftingTask implements ICraftingTask {
    protected static final int DEFAULT_COMPARE = IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT;

    public static final String NBT_STEPS = "Steps";
    public static final String NBT_TO_TAKE_FLUIDS = "ToTakeFluids";
    public static final String NBT_TO_INSERT_ITEMS = "ToInsertItems";
    public static final String NBT_TO_INSERT_FLUIDS = "ToInsertFluids";
    public static final String NBT_TOOK_FLUIDS = "TookFluids";

    private INetworkMaster network;
    @Nullable
    private ItemStack requested;
    private ICraftingPattern pattern;
    private int quantity;
    private List<ICraftingStep> steps = new ArrayList<>();
    private IItemStackList toTake = API.instance().createItemStackList();
    private IItemStackList toCraft = API.instance().createItemStackList();
    private IItemStackList missing = API.instance().createItemStackList();
    private Set<ICraftingPattern> usedPatterns = new HashSet<>();
    private boolean recurseFound = false;
    private Deque<ItemStack> toInsertItems = new ArrayDeque<>();
    private Deque<FluidStack> toInsertFluids = new ArrayDeque<>();
    private IFluidStackList toTakeFluids = API.instance().createFluidStackList();
    private IFluidStackList tookFluids = API.instance().createFluidStackList();

    public CraftingTask(INetworkMaster network, @Nullable ItemStack requested, ICraftingPattern pattern, int quantity) {
        this.network = network;
        this.requested = requested;
        this.pattern = pattern;
        this.quantity = quantity;
    }

    public CraftingTask(INetworkMaster network, @Nullable ItemStack requested, ICraftingPattern pattern, int quantity, List<ICraftingStep> steps, Deque<ItemStack> toInsertItems, IFluidStackList toTakeFluids, IFluidStackList tookFluids, Deque<FluidStack> toInsertFluids) {
        this(network, requested, pattern, quantity);
        this.steps = steps;
        this.toInsertItems = toInsertItems;
        this.toTakeFluids = toTakeFluids;
        this.tookFluids = tookFluids;
        this.toInsertFluids = toInsertFluids;
    }

    @Override
    public void calculate() {
        // Copy here might be expensive but since it is only executed once it isn't a big impact
        IItemStackList networkList = network.getItemStorageCache().getList().copy();
        networkList.clean(); // Remove the zero stacks
        networkList = networkList.getOredicted();
        IFluidStackList networkFluidList = network.getFluidStorageCache().getList().copy();
        IItemStackList toInsert = API.instance().createItemStackList();

        ItemStack requested = this.requested != null ? this.requested : pattern.getOutputs().get(0);
        toCraft.add(ItemHandlerHelper.copyStackWithSize(requested, quantity));

        int quantity = this.quantity;

        while (quantity > 0 && !recurseFound) {
            calculate(networkList, networkFluidList, pattern, toInsert);
            quantity -= pattern.getQuantityPerRequest(requested);
        }

        usedPatterns.clear();
    }

    private void calculate(IItemStackList networkList, IFluidStackList networkFluidList, ICraftingPattern pattern, IItemStackList toInsert) {
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
                    if (!inputStack.isItemStackDamageable() || !inputStack.isItemDamaged()) {
                        toCraft.add(inputStack);
                    }
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
                        calculate(networkList, networkFluidList, inputPattern, toInsert);
                        input.stackSize -= craftQuantity;
                        // Calculate added all the crafted outputs toInsertItems
                        // So we remove the ones we use from toInsertItems
                        toInsert.remove(inputCrafted, true);
                    } else {
                        // Fluid checks are with a stack size of one
                        ItemStack fluidCheck = ItemHandlerHelper.copyStackWithSize(input, 1);
                        while (input.stackSize > 0 && doFluidCalculation(networkList, networkFluidList, fluidCheck, toInsert)) {
                            actualInputs.add(fluidCheck);
                            input.stackSize -= 1;
                        }

                        // When it isn't a fluid or just doesn't have the needed fluids
                        if (input.stackSize > 0) {
                            missing.add(input.copy());
                            input.stackSize = 0;
                        }
                    }
                }
            }
        }

        if (pattern.isProcessing()) {
            steps.add(new CraftingStepProcess(network, pattern));
        } else {
            steps.add(new CraftingStepCraft(network, pattern));
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

    private boolean doFluidCalculation(IItemStackList networkList, IFluidStackList networkFluidList, ItemStack input, IItemStackList toInsert) {
        FluidStack fluidInItem = RSUtils.getFluidFromStack(input, true);

        if (fluidInItem != null && RSUtils.hasFluidBucket(fluidInItem)) {
            FluidStack fluidInStorage = networkFluidList.get(fluidInItem);

            if (fluidInStorage == null || fluidInStorage.amount < fluidInItem.amount) {
                missing.add(input);
            } else {
                ItemStack bucket = toInsert.get(RSUtils.EMPTY_BUCKET);
                boolean hasBucket = false;
                if (bucket != null && bucket.stackSize > 0) {
                    hasBucket = toInsert.remove(RSUtils.EMPTY_BUCKET, 1, false);
                }
                if (!hasBucket) {
                    bucket = networkList.get(RSUtils.EMPTY_BUCKET);
                    if (bucket != null && bucket.stackSize > 0) {
                        hasBucket = networkList.remove(RSUtils.EMPTY_BUCKET, 1, false);
                    }
                }

                ICraftingPattern bucketPattern = network.getPattern(RSUtils.EMPTY_BUCKET);

                if (!hasBucket) {
                    if (bucketPattern == null) {
                        missing.add(RSUtils.EMPTY_BUCKET.copy());
                    } else {
                        toCraft.add(RSUtils.EMPTY_BUCKET.copy());
                        calculate(networkList, networkFluidList, bucketPattern, toInsert);
                        toInsert.remove(RSUtils.EMPTY_BUCKET, 1, false);
                    }
                }

                if (hasBucket || bucketPattern != null) {
                    toTakeFluids.add(fluidInItem.copy());
                    networkFluidList.remove(fluidInItem, false);
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public void onCancelled() {
        for (ItemStack stack : toInsertItems) {
            network.insertItem(stack, stack.stackSize, false);
        }

        for (FluidStack stack : tookFluids.getStacks()) {
            network.insertFluid(stack, stack.amount, false);
        }

        network.sendCraftingMonitorUpdate();
    }

    @Override
    public String toString() {
        return "\nCraftingTask{quantity=" + quantity +
            "\n, toTake=" + toTake +
            "\n, toTakeFluids=" + toTakeFluids +
            "\n, toCraft=" + toCraft +
            "\n, toInsertItems=" + toInsertItems +
            "\n, toInsertFluids=" + toInsertFluids +
            "\n, steps=" + steps +
            '}';
    }

    @Override
    public boolean update(Map<ICraftingPatternContainer, Integer> usedContainers) {
        IItemStackList oreDictPrepped = network.getItemStorageCache().getList().getOredicted();

        if (!missing.isEmpty()) {
            for (ItemStack missing : this.missing.getStacks()) {
                if (!oreDictPrepped.trackedRemove(missing, true)) {
                    oreDictPrepped.undo();
                    return false;
                }
            }
            oreDictPrepped.undo();
            reschedule();
            return false;
        }


        for (FluidStack stack : toTakeFluids.getStacks()) {
            FluidStack stackExtracted = network.extractFluid(stack, stack.amount);
            if (stackExtracted != null) {
                toTakeFluids.remove(stack, stack.amount, false);
                tookFluids.add(stackExtracted);
                network.sendCraftingMonitorUpdate();
            }
        }

        toTakeFluids.clean();

        for (ICraftingStep step : steps) {
            ICraftingPatternContainer container = step.getPattern().getContainer();
            Integer timesUsed = usedContainers.get(container);

            if (timesUsed == null) {
                timesUsed = 0;
            }

            if (timesUsed++ <= container.getSpeedUpdateCount()) {
                if (!step.hasStartedProcessing() && step.canStartProcessing(oreDictPrepped, tookFluids)) {
                    step.setStartedProcessing();
                    step.execute(toInsertItems, toInsertFluids);
                    usedContainers.put(container, timesUsed);
                    network.sendCraftingMonitorUpdate();
                }
            }
        }

        // We need to copy the size cause we'll re-add unadded stacks to the queue
        int times = toInsertItems.size();
        for (int i = 0; i < times; i++) {
            ItemStack insert = toInsertItems.poll();
            if (insert != null) {
                ItemStack remainder = network.insertItem(insert, insert.stackSize, false);

                if (remainder != null) {
                    toInsertItems.add(remainder);
                }
            }
        }

        if (steps.stream().filter(ICraftingStep::hasStartedProcessing).count() == 0) {
            // When there is no started processes, restart the task.
            reschedule();
        }

        // Remove finished tasks
        steps.removeIf(ICraftingStep::hasReceivedOutputs);

        return isFinished();
    }

    @Override
    public void reschedule() {
        List<ICraftingStep> mainSteps = steps.stream().filter(s -> s.getPattern() == pattern).collect(Collectors.toList());
        missing.clear();
        steps.clear();
        // if the list of main steps is empty there is no point in rescheduling
        if (!mainSteps.isEmpty()) {
            quantity = 0;
            int quantityPerRequest = pattern.getQuantityPerRequest(requested);
            for (ICraftingStep step : mainSteps) {
                quantity += quantityPerRequest - step.getReceivedOutput(requested);
            }
            calculate();
            network.sendCraftingMonitorUpdate();
        }
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

        NBTTagList stepsList = new NBTTagList();

        for (ICraftingStep step : steps) {
            stepsList.appendTag(step.writeToNBT(new NBTTagCompound()));
        }

        tag.setTag(NBT_STEPS, stepsList);

        NBTTagList toInsertItemsList = new NBTTagList();

        for (ItemStack insert : toInsertItems) {
            toInsertItemsList.appendTag(insert.serializeNBT());
        }

        tag.setTag(NBT_TO_INSERT_ITEMS, toInsertItemsList);

        tag.setTag(NBT_TO_TAKE_FLUIDS, RSUtils.serializeFluidStackList(toTakeFluids));

        NBTTagList toInsertFluidsList = new NBTTagList();

        for (FluidStack insert : toInsertFluids) {
            toInsertFluidsList.appendTag(insert.writeToNBT(new NBTTagCompound()));
        }

        tag.setTag(NBT_TO_INSERT_FLUIDS, toInsertFluidsList);

        tag.setTag(NBT_TOOK_FLUIDS, RSUtils.serializeFluidStackList(tookFluids));

        return tag;
    }

    @Override
    public List<ICraftingMonitorElement> getCraftingMonitorElements() {
        ICraftingMonitorElementList elements = API.instance().createCraftingMonitorElementList();

        elements.directAdd(new CraftingMonitorElementItemRender(
            network.getCraftingTasks().indexOf(this),
            requested != null ? requested : pattern.getOutputs().get(0),
            quantity,
            0
        ));

        if (!missing.isEmpty()) {
            elements.directAdd(new CraftingMonitorElementText("gui.refinedstorage:crafting_monitor.items_missing", 16));

            missing.getStacks().stream()
                .map(stack -> new CraftingMonitorElementError(new CraftingMonitorElementItemRender(
                    -1,
                    stack,
                    stack.stackSize,
                    32
                ), ""))
                .forEach(elements::add);

            elements.commit();
        }

        if (!toInsertItems.isEmpty()) {
            elements.directAdd(new CraftingMonitorElementText("gui.refinedstorage:crafting_monitor.items_inserting", 16));

            toInsertItems.stream()
                .map(stack -> new CraftingMonitorElementItemRender(
                    -1,
                    stack,
                    stack.stackSize,
                    32
                ))
                .forEach(elements::add);

            elements.commit();
        }

        if (!isFinished()) {
            if (steps.stream().filter(s -> !s.getPattern().isProcessing()).count() > 0) {
                elements.directAdd(new CraftingMonitorElementText("gui.refinedstorage:crafting_monitor.items_crafting", 16));

                IItemStackList oreDictPrepped = network.getItemStorageCache().getList().getOredicted();

                for (ICraftingStep step : steps.stream().filter(s -> !s.getPattern().isProcessing()).collect(Collectors.toList())) {
                    for (int i = 0; i < step.getPattern().getOutputs().size(); ++i) {
                        ICraftingMonitorElement element = new CraftingMonitorElementItemRender(
                            -1,
                            step.getPattern().getOutputs().get(i),
                            step.getPattern().getOutputs().get(i).stackSize,
                            32
                        );

                        if (!step.hasStartedProcessing() && !step.canStartProcessing(oreDictPrepped, tookFluids)) {
                            element = new CraftingMonitorElementInfo(element, "gui.refinedstorage:crafting_monitor.waiting_for_items");
                        }

                        elements.add(element);
                    }
                }

                elements.commit();
            }

            if (steps.stream().filter(s -> s.getPattern().isProcessing()).count() > 0) {
                elements.directAdd(new CraftingMonitorElementText("gui.refinedstorage:crafting_monitor.items_processing", 16));

                for (ICraftingStep step : steps.stream().filter(s -> s.getPattern().isProcessing()).collect(Collectors.toList())) {
                    for (int i = 0; i < step.getPattern().getOutputs().size(); ++i) {
                        ICraftingMonitorElement element = new CraftingMonitorElementItemRender(
                            -1,
                            step.getPattern().getOutputs().get(i),
                            step.getPattern().getOutputs().get(i).stackSize,
                            32
                        );

                        if (step.getPattern().getContainer().getFacingTile() == null) {
                            element = new CraftingMonitorElementError(element, "gui.refinedstorage:crafting_monitor.machine_none");
                        } else if (!step.hasStartedProcessing() && !step.canStartProcessing()) {
                            element = new CraftingMonitorElementError(element, "gui.refinedstorage:crafting_monitor.machine_in_use");
                        }

                        elements.add(element);
                    }
                }

                elements.commit();
            }

            if (!toTakeFluids.isEmpty()) {
                elements.directAdd(new CraftingMonitorElementText("gui.refinedstorage:crafting_monitor.fluids_taking", 16));

                toTakeFluids.getStacks().stream()
                    .map(stack -> new CraftingMonitorElementFluidRender(
                        -1,
                        stack,
                        32
                    )).forEach(elements::add);

                elements.commit();
            }
        }

        return elements.getElements();
    }

    @Override
    public ICraftingPattern getPattern() {
        return pattern;
    }

    public List<ICraftingStep> getSteps() {
        return steps;
    }

    @Override
    public boolean isValid() {
        return !recurseFound;
    }

    @Override
    public IItemStackList getMissing() {
        return missing;
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
        return steps.stream().allMatch(ICraftingStep::hasReceivedOutputs);
    }
}
