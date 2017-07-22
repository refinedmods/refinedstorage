package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternChain;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementList;
import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingStep;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementError;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementInfo;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementItemRender;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementText;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementFluidStack;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementItemStack;
import com.raoulvdberge.refinedstorage.apiimpl.util.StackListItem;
import com.raoulvdberge.refinedstorage.util.StackUtils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class CraftingTask implements ICraftingTask {
    protected static final int DEFAULT_COMPARE = IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT | IComparer.COMPARE_STRIP_NBT;

    public static final String NBT_STEPS = "Steps";
    public static final String NBT_TO_TAKE_FLUIDS = "ToTakeFluids";
    public static final String NBT_TO_INSERT_ITEMS = "ToInsertItems";
    public static final String NBT_TO_INSERT_FLUIDS = "ToInsertFluids";

    private INetwork network;
    @Nullable
    private ItemStack requested;
    private ICraftingPattern pattern;
    private ICraftingPatternChain chain;
    private int quantity;
    private boolean automated;
    private List<ICraftingStep> mainSteps = new LinkedList<>();
    private IStackList<ItemStack> toTake = API.instance().createItemStackList();
    private IStackList<ItemStack> toCraft = API.instance().createItemStackList();
    private IStackList<ItemStack> missing = API.instance().createItemStackList();
    private Set<ICraftingPattern> usedPatterns = new HashSet<>();
    private boolean recurseFound = false;
    private Deque<ItemStack> toInsertItems = new ArrayDeque<>();
    private Deque<FluidStack> toInsertFluids = new ArrayDeque<>();
    private IStackList<FluidStack> toTakeFluids = API.instance().createFluidStackList();

    public CraftingTask(INetwork network, @Nullable ItemStack requested, ICraftingPattern pattern, int quantity, boolean automated) {
        this.network = network;
        this.requested = requested;
        this.pattern = pattern;
        this.quantity = quantity;
        this.automated = automated;
    }

    public CraftingTask(INetwork network, @Nullable ItemStack requested, ICraftingPatternChain chain, int quantity, boolean automated) {
        this(network, requested, chain.getPrototype(), quantity, automated);

        this.chain = chain;
    }

    public CraftingTask(INetwork network, @Nullable ItemStack requested, ICraftingPattern pattern, int quantity, List<ICraftingStep> mainSteps, Deque<ItemStack> toInsertItems, IStackList<FluidStack> toTakeFluids, Deque<FluidStack> toInsertFluids, boolean automated) {
        this(network, requested, pattern, quantity, automated);

        this.mainSteps = mainSteps;
        this.toInsertItems = toInsertItems;
        this.toTakeFluids = toTakeFluids;
        this.toInsertFluids = toInsertFluids;
    }

    @Override
    public void calculate() {
        // Copy here might be expensive but since it is only executed once it isn't a big impact
        IStackList<ItemStack> networkList = network.getItemStorageCache().getList().copy();
        networkList.clean(); // Remove the zero stacks
        networkList = networkList.getOredicted();
        IStackList<FluidStack> networkFluidList = network.getFluidStorageCache().getList().copy();
        IStackList<ItemStack> toInsert = API.instance().createItemStackList();

        ItemStack requested = this.requested != null ? this.requested : pattern.getOutputs().get(0);
        toCraft.add(ItemHandlerHelper.copyStackWithSize(requested, quantity));

        int quantity = this.quantity;

        ICraftingPattern currentPattern;
        while (quantity > 0 && !recurseFound) {
            currentPattern = this.chain == null ? this.pattern : this.chain.cycle();
            mainSteps.add(calculate(networkList, networkFluidList, currentPattern, toInsert));
            quantity -= currentPattern.getQuantityPerRequest(requested);
        }

        usedPatterns.clear();
    }

    private ICraftingStep calculate(IStackList<ItemStack> networkList, IStackList<FluidStack> networkFluidList, ICraftingPattern pattern, IStackList<ItemStack> toInsert) {
        recurseFound |= !usedPatterns.add(pattern);
        if (recurseFound) {
            return null;
        }

        int compare = DEFAULT_COMPARE;

        IStackList<ItemStack> actualInputs = API.instance().createItemStackList();
        List<ItemStack> usedStacks = new LinkedList<>();
        List<ICraftingStep> previousSteps = new LinkedList<>();

        IStackList<ItemStack> byproductList = API.instance().createItemStackList();
        pattern.getByproducts().stream().filter(Objects::nonNull).forEach(byproductList::add);

        for (List<ItemStack> inputs : pattern.getOreInputs()) {
            if (inputs == null || inputs.isEmpty()) {
                usedStacks.add(null);
                continue;
            }

            int i = 0;
            ItemStack input, extraStack, networkStack;
            do {
                input = inputs.get(i).copy();
                // This will be a tool, like a hammer
                if (input.isItemStackDamageable()) {
                    compare &= ~IComparer.COMPARE_DAMAGE;
                } else {
                    compare |= IComparer.COMPARE_DAMAGE;
                }

                extraStack = toInsert.get(input, compare);
                networkStack = networkList.get(input, compare);
            }
            while (extraStack == null && networkStack == null && ++i < inputs.size() && !network.getCraftingManager().hasPattern(input, compare));
            if (i == inputs.size()) {
                input = inputs.get(0).copy();
            }
            usedStacks.add(input.copy());

            // This will be a tool, like a hammer
            if (input.isItemStackDamageable()) {
                compare &= ~IComparer.COMPARE_DAMAGE;
            } else {
                compare |= IComparer.COMPARE_DAMAGE;
            }

            // This handles recipes that use the output as input for the sub recipe
            final int lambdaCompare = compare;
            final ItemStack lambdaInput = input;
            ICraftingPattern inputPattern = null;
            int available = (extraStack == null ? 0 : extraStack.getCount()) + (networkStack == null ? 0 : networkStack.getCount());
            if (available < input.getCount()) {
                inputPattern = network.getCraftingManager().getPattern(input, compare, networkList);
                if (inputPattern != null) {
                    if (inputPattern.getInputs().stream().anyMatch(s -> API.instance().getComparer().isEqual(s, lambdaInput, lambdaCompare))) {
                        int craftQuantity = inputPattern.getQuantityPerRequest(input, compare);
                        // The needed amount is the actual needed amount of extraStacks + the needed input (twice so you can keep repeating it)
                        long needed = (networkStack == null ? 0 : -networkStack.getCount()) + input.getCount() + inputPattern.getInputs().stream().filter(s -> API.instance().getComparer().isEqual(s, lambdaInput, lambdaCompare)).count() * 2;
                        do {
                            previousSteps.add(calculate(networkList, networkFluidList, inputPattern, toInsert));
                            toCraft.add(ItemHandlerHelper.copyStackWithSize(input, craftQuantity));
                            extraStack = toInsert.get(input, compare);
                        } while (extraStack != null && extraStack.getCount() < needed);
                    }
                }
            }

            while (input.getCount() > 0) {
                if (extraStack != null && extraStack.getCount() > 0) {
                    int takeQuantity = Math.min(extraStack.getCount(), input.getCount());
                    ItemStack inputStack = ItemHandlerHelper.copyStackWithSize(extraStack, takeQuantity);
                    actualInputs.add(inputStack.copy());
                    input.shrink(takeQuantity);
                    if (byproductList.get(inputStack, compare) == null) {
                        toCraft.add(inputStack);
                    }
                    toInsert.remove(inputStack);
                    if (input.getCount() > 0) {
                        i = 0;
                        do {
                            extraStack = toInsert.get(inputs.get(i), compare);
                        } while ((extraStack == null || extraStack.getCount() == 0) && ++i < inputs.size());
                    }
                } else if (networkStack != null && networkStack.getCount() > 0) {
                    int takeQuantity = Math.min(networkStack.getCount(), input.getCount());
                    ItemStack inputStack = ItemHandlerHelper.copyStackWithSize(networkStack, takeQuantity);
                    toTake.add(inputStack.copy());
                    actualInputs.add(inputStack.copy());
                    input.shrink(takeQuantity);
                    networkList.remove(inputStack);
                    if (input.getCount() > 0) {
                        i = 0;
                        do {
                            networkStack = networkList.get(inputs.get(i), compare);
                        } while ((extraStack == null || extraStack.getCount() == 0) && ++i < inputs.size());
                    }
                } else {
                    int oreDictedCompare = compare | (pattern.isOredict() ? IComparer.COMPARE_OREDICT : 0);
                    if (inputPattern == null) {
                        inputPattern = network.getCraftingManager().getPattern(input, oreDictedCompare, networkList);
                    }

                    if (inputPattern != null) {
                        ItemStack actualCraft = inputPattern.getActualOutput(input, oreDictedCompare);
                        int craftQuantity = Math.min(inputPattern.getQuantityPerRequest(input, oreDictedCompare), input.getCount());
                        ItemStack inputCrafted = ItemHandlerHelper.copyStackWithSize(actualCraft, craftQuantity);
                        toCraft.add(inputCrafted.copy());
                        actualInputs.add(inputCrafted.copy());
                        previousSteps.add(calculate(networkList, networkFluidList, inputPattern, toInsert));
                        input.shrink(craftQuantity);
                        if (!recurseFound) {
                            // Calculate added all the crafted outputs toInsert
                            // So we remove the ones we use from toInsert
                            ItemStack inserted = toInsert.get(inputCrafted, compare);
                            toInsert.remove(inserted, craftQuantity);
                        }
                    } else {
                        // Fluid checks are with a stack size of one
                        ItemStack fluidCheck = ItemHandlerHelper.copyStackWithSize(input, 1);
                        while (input.getCount() > 0 && doFluidCalculation(networkList, networkFluidList, fluidCheck, toInsert, previousSteps)) {
                            actualInputs.add(fluidCheck);
                            input.shrink(1);
                        }

                        // When it isn't a fluid or just doesn't have the needed fluids
                        if (input.getCount() > 0) {
                            ItemStack copy = input.copy();
                            if (copy.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                                copy.setItemDamage(0);
                            }
                            missing.add(copy);
                            input.setCount(0);
                        }
                    }
                }
            }
        }

        ItemStack[] took = null;
        if (missing.isEmpty()) {
            if (!pattern.isProcessing()) {
                took = StackListItem.toCraftingGrid(actualInputs, usedStacks, compare | (pattern.isOredict() ? IComparer.COMPARE_OREDICT : 0));
            }
        }

        List<ItemStack> outputs = !pattern.isProcessing() && pattern.isOredict() && missing.isEmpty() ? pattern.getOutputs(took) : pattern.getOutputs();
        if (outputs == null) { // Bla Bla what evs
            outputs = pattern.getOutputs();
        }

        for (ItemStack output : outputs) {
            if (output != null && !output.isEmpty()) {
                toInsert.add(output.copy());
            }
        }

        for (ItemStack byproduct : (!pattern.isProcessing() && pattern.isOredict() && missing.isEmpty() ? pattern.getByproducts(took) : pattern.getByproducts())) {
            if (byproduct != null && !byproduct.isEmpty()) {
                toInsert.add(byproduct.copy());
            }
        }

        usedPatterns.remove(pattern);

        return pattern.isProcessing() ? new CraftingStepProcess(network, pattern, previousSteps) : new CraftingStepCraft(network, pattern, usedStacks, previousSteps);
    }

    private boolean doFluidCalculation(IStackList<ItemStack> networkList, IStackList<FluidStack> networkFluidList, ItemStack input, IStackList<ItemStack> toInsert, List<ICraftingStep> previousSteps) {
        FluidStack fluidInItem;

        if (API.instance().getComparer().isEqual(input, StackUtils.WATER_BOTTLE)) {
            FluidStack fluidInStorage = networkFluidList.get(new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME));

            if (fluidInStorage == null || fluidInStorage.amount < Fluid.BUCKET_VOLUME) {
                missing.add(input);
            } else {
                ItemStack emptyBottle = toInsert.get(StackUtils.EMPTY_BOTTLE);
                boolean hasBottle = false;
                if (emptyBottle != null && emptyBottle.getCount() > 0) {
                    hasBottle = toInsert.remove(StackUtils.EMPTY_BOTTLE, 1);
                }
                if (!hasBottle) {
                    emptyBottle = networkList.get(StackUtils.EMPTY_BOTTLE);
                    if (emptyBottle != null && emptyBottle.getCount() > 0) {
                        hasBottle = networkList.remove(StackUtils.EMPTY_BOTTLE);
                    }
                }

                ICraftingPattern emptyBottlePattern = network.getCraftingManager().getPattern(StackUtils.EMPTY_BOTTLE);

                if (!hasBottle) {
                    if (emptyBottlePattern == null) {
                        missing.add(StackUtils.EMPTY_BOTTLE.copy());
                    } else {
                        toCraft.add(StackUtils.EMPTY_BOTTLE.copy());
                        previousSteps.add(calculate(networkList, networkFluidList, emptyBottlePattern, toInsert));
                        toInsert.remove(StackUtils.EMPTY_BOTTLE, 1);
                    }
                }

                if (hasBottle || emptyBottlePattern != null) {
                    toTake.add(StackUtils.EMPTY_BOTTLE.copy());
                    networkList.remove(StackUtils.EMPTY_BOTTLE);
                }
            }

            return true;
        } else if ((fluidInItem = StackUtils.getFluid(input, true).getValue()) != null && StackUtils.hasFluidBucket(fluidInItem)) {
            FluidStack fluidInStorage = networkFluidList.get(fluidInItem);

            if (fluidInStorage == null || fluidInStorage.amount < fluidInItem.amount) {
                missing.add(input);
            } else {
                ItemStack bucket = toInsert.get(StackUtils.EMPTY_BUCKET);
                boolean hasBucket = false;
                if (bucket != null && bucket.getCount() > 0) {
                    hasBucket = toInsert.remove(StackUtils.EMPTY_BUCKET, 1);
                }
                if (!hasBucket) {
                    bucket = networkList.get(StackUtils.EMPTY_BUCKET);
                    if (bucket != null && bucket.getCount() > 0) {
                        hasBucket = networkList.remove(StackUtils.EMPTY_BUCKET, 1);
                    }
                }

                ICraftingPattern bucketPattern = network.getCraftingManager().getPattern(StackUtils.EMPTY_BUCKET);

                if (!hasBucket) {
                    if (bucketPattern == null) {
                        missing.add(StackUtils.EMPTY_BUCKET.copy());
                    } else {
                        toCraft.add(StackUtils.EMPTY_BUCKET.copy());
                        previousSteps.add(calculate(networkList, networkFluidList, bucketPattern, toInsert));
                        toInsert.remove(StackUtils.EMPTY_BUCKET, 1);
                    }
                }

                if (hasBucket || bucketPattern != null) {
                    toTakeFluids.add(fluidInItem.copy());
                    networkFluidList.remove(fluidInItem);
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public void onCancelled() {
        for (ItemStack stack : toInsertItems) {
            network.insertItemTracked(stack, stack.getCount());
        }

        for (ICraftingStep step : getSteps()) {
            if (step.getPattern().isBlocking()) {
                step.getPattern().getContainer().setBlocked(false);
            }
        }

        network.markCraftingMonitorForUpdate();
    }

    @Override
    public String toString() {
        return "\nCraftingTask{quantity=" + quantity +
            "\n, automated=" + automated +
            "\n, toTake=" + toTake +
            "\n, toTakeFluids=" + toTakeFluids +
            "\n, toCraft=" + toCraft +
            "\n, toInsertItems=" + toInsertItems +
            "\n, toInsertFluids=" + toInsertFluids +
            "\n, mainSteps=" + mainSteps +
            '}';
    }

    @Override
    public boolean update(Map<ICraftingPatternContainer, Integer> usedContainers) {
        IStackList<ItemStack> oreDictPrepped = network.getItemStorageCache().getList().getOredicted();
        IStackList<FluidStack> networkFluids = network.getFluidStorageCache().getList();

        if (!missing.isEmpty()) {
            for (ItemStack missing : this.missing.getStacks()) {
                if (!oreDictPrepped.trackedRemove(missing)) {
                    oreDictPrepped.undo();
                    return false;
                }
            }
            oreDictPrepped.undo();
            reschedule();
            return false;
        }

        // We need to copy the size cause we'll re-add unadded stacks to the queue
        // Do inserting on the next tick, reliefs CPU time during insertion
        // See TileController#runningSteps
        int times = toInsertItems.size();
        for (int i = 0; i < times; i++) {
            ItemStack insert = toInsertItems.poll();
            if (insert != null) {
                ItemStack remainder = network.insertItemTracked(insert, insert.getCount());

                if (remainder != null) {
                    toInsertItems.add(remainder);
                }
            }
        }

        // Collect all leaf steps
        List<ICraftingStep> leafSteps = new LinkedList<>();
        Queue<ICraftingStep> steps = new LinkedList<>();
        steps.addAll(mainSteps);
        while (steps.size() > 0) {
            ICraftingStep step = steps.poll();
            if (step.getPreliminarySteps().size() > 0) {
                steps.addAll(step.getPreliminarySteps());
            } else {
                leafSteps.add(step);
            }
        }

        for (ICraftingStep step : leafSteps) {
            if (!step.hasStartedProcessing()) {
                ICraftingPatternContainer container = step.getPattern().getContainer();
                Integer timesUsed = usedContainers.get(container);

                if (timesUsed == null) {
                    timesUsed = 0;
                }

                if (timesUsed++ <= container.getSpeedUpdateCount()) {
                    if (!step.getPattern().isProcessing() || !container.isBlocked()) {
                        if (step.canStartProcessing(oreDictPrepped, networkFluids)) {
                            step.setStartedProcessing();
                            step.execute(toInsertItems, toInsertFluids);
                            usedContainers.put(container, timesUsed);
                            network.markCraftingMonitorForUpdate();
                        }
                    }
                }
            }
        }


        if (getSteps().stream().filter(ICraftingStep::hasStartedProcessing).count() == 0) {
            // When there is no started processes, restart the task.
            reschedule();
        }

        // Remove finished tasks
        steps.clear(); // Re use Queue from earlier
        mainSteps.removeIf(ICraftingStep::hasReceivedOutputs);
        steps.addAll(mainSteps);
        while (steps.size() > 0) {
            ICraftingStep step = steps.poll();
            step.getPreliminarySteps().removeIf(ICraftingStep::hasReceivedOutputs);
            steps.addAll(step.getPreliminarySteps());
        }

        return isFinished();
    }

    @Override
    public void reschedule() {
        List<ICraftingStep> mainSteps = this.mainSteps.stream().filter(s -> s.getPattern().alike(pattern)).collect(Collectors.toList());
        missing.clear();
        this.mainSteps.clear();
        // if the list of main mainSteps is empty there is no point in rescheduling
        if (!mainSteps.isEmpty()) {
            quantity = 0;
            int quantityPerRequest = pattern.getQuantityPerRequest(requested);
            for (ICraftingStep step : mainSteps) {
                quantity += quantityPerRequest - step.getReceivedOutput(requested);
            }
            if (quantity > 0) {
                calculate();
            }
            network.markCraftingMonitorForUpdate();
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

        for (ICraftingStep step : mainSteps) {
            stepsList.appendTag(step.writeToNBT(new NBTTagCompound()));
        }

        tag.setTag(NBT_STEPS, stepsList);

        NBTTagList toInsertItemsList = new NBTTagList();

        for (ItemStack insert : toInsertItems) {
            toInsertItemsList.appendTag(insert.serializeNBT());
        }

        tag.setTag(NBT_TO_INSERT_ITEMS, toInsertItemsList);

        tag.setTag(NBT_TO_TAKE_FLUIDS, StackUtils.serializeFluidStackList(toTakeFluids));

        NBTTagList toInsertFluidsList = new NBTTagList();

        for (FluidStack insert : toInsertFluids) {
            toInsertFluidsList.appendTag(insert.writeToNBT(new NBTTagCompound()));
        }

        tag.setTag(NBT_TO_INSERT_FLUIDS, toInsertFluidsList);

        return tag;
    }

    @Override
    public List<ICraftingMonitorElement> getCraftingMonitorElements() {
        ICraftingMonitorElementList elements = API.instance().createCraftingMonitorElementList();

        elements.directAdd(new CraftingMonitorElementItemRender(
            network.getCraftingManager().getTasks().indexOf(this),
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
                    stack.getCount(),
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
                    stack.getCount(),
                    32
                ))
                .forEach(elements::add);

            elements.commit();
        }

        if (!isFinished()) {
            if (getSteps().stream().filter(s -> !s.getPattern().isProcessing()).count() > 0) {
                elements.directAdd(new CraftingMonitorElementText("gui.refinedstorage:crafting_monitor.items_crafting", 16));

                IStackList<ItemStack> oreDictPrepped = network.getItemStorageCache().getList().getOredicted();
                IStackList<FluidStack> networkFluids = network.getFluidStorageCache().getList();

                for (ICraftingStep step : getSteps().stream().filter(s -> !s.getPattern().isProcessing()).collect(Collectors.toList())) {
                    for (int i = 0; i < step.getPattern().getOutputs().size(); ++i) {
                        ICraftingMonitorElement element = new CraftingMonitorElementItemRender(
                            -1,
                            step.getPattern().getOutputs().get(i),
                            step.getPattern().getOutputs().get(i).getCount(),
                            32
                        );

                        if (!step.hasStartedProcessing() && !step.canStartProcessing(oreDictPrepped, networkFluids)) {
                            element = new CraftingMonitorElementInfo(element, "gui.refinedstorage:crafting_monitor.waiting_for_items");
                        }

                        elements.add(element);
                    }
                }

                elements.commit();
            }

            if (getSteps().stream().filter(s -> s.getPattern().isProcessing()).count() > 0) {
                elements.directAdd(new CraftingMonitorElementText("gui.refinedstorage:crafting_monitor.items_processing", 16));

                for (ICraftingStep step : getSteps().stream().filter(s -> s.getPattern().isProcessing()).collect(Collectors.toList())) {
                    for (int i = 0; i < step.getPattern().getOutputs().size(); ++i) {
                        ICraftingMonitorElement element = new CraftingMonitorElementItemRender(
                            -1,
                            step.getPattern().getOutputs().get(i),
                            step.getPattern().getOutputs().get(i).getCount(),
                            32
                        );

                        if (step.getPattern().getContainer().getFacingTile() == null) {
                            element = new CraftingMonitorElementError(element, "gui.refinedstorage:crafting_monitor.machine_none");
                        } else if (!step.hasStartedProcessing() && !step.canStartProcessing()) {
                            element = new CraftingMonitorElementError(element, "gui.refinedstorage:crafting_monitor.machine_in_use");
                        } else if (!step.hasStartedProcessing() && step.getPattern().getContainer().isBlocked()) {
                            element = new CraftingMonitorElementError(element, "gui.refinedstorage:crafting_monitor.blocked");
                        }

                        elements.add(element);
                    }
                }

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
        List<ICraftingStep> allSteps = new LinkedList<>();
        Queue<ICraftingStep> steps = new LinkedList<>();
        steps.addAll(mainSteps);
        while (steps.size() > 0) {
            ICraftingStep step = steps.poll();
            allSteps.add(step);
            steps.addAll(step.getPreliminarySteps());
        }
        return allSteps;
    }

    @Override
    public boolean isValid() {
        return !recurseFound;
    }

    @Override
    public IStackList<ItemStack> getMissing() {
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
            previewStack.addToCraft(stack.getCount());
            map.put(hash, previewStack);
        }

        for (ItemStack stack : missing.getStacks()) {
            int hash = API.instance().getItemStackHashCode(stack);
            CraftingPreviewElementItemStack previewStack = map.get(hash);
            if (previewStack == null) {
                previewStack = new CraftingPreviewElementItemStack(stack);
            }
            previewStack.setMissing(true);
            previewStack.addToCraft(stack.getCount());
            map.put(hash, previewStack);
        }

        for (ItemStack stack : toTake.getStacks()) {
            int hash = API.instance().getItemStackHashCode(stack);
            CraftingPreviewElementItemStack previewStack = map.get(hash);
            if (previewStack == null) {
                previewStack = new CraftingPreviewElementItemStack(stack);
            }
            previewStack.addAvailable(stack.getCount());
            map.put(hash, previewStack);
        }

        List<ICraftingPreviewElement> elements = new ArrayList<>(map.values());

        toTakeFluids.getStacks().stream().map(CraftingPreviewElementFluidStack::new).forEach(elements::add);

        return elements;
    }

    @Override
    public boolean isAutomated() {
        return automated;
    }

    @Override
    public boolean isFinished() {
        return mainSteps.stream().allMatch(ICraftingStep::hasReceivedOutputs);
    }
}
