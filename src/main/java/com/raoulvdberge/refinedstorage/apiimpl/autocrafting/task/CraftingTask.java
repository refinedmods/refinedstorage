package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.*;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementList;
import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskErrorType;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTaskError;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementColor;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementItemRender;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementText;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementItemStack;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.extractor.CraftingExtractor;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.extractor.CraftingExtractorItemStatus;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.inserter.CraftingInserter;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.inserter.CraftingInserterItem;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.inserter.CraftingInserterItemStatus;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.step.CraftingStep;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.step.CraftingStepCraft;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.step.CraftingStepProcess;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.*;

public class CraftingTask implements ICraftingTask {
    private static final long CALCULATION_TIMEOUT_MS = 5000;

    private static final String NBT_REQUESTED = "Requested";
    private static final String NBT_QUANTITY = "Quantity";
    private static final String NBT_PATTERN = "Pattern";
    private static final String NBT_STEPS = "Steps";
    private static final String NBT_INSERTER = "Inserter";
    private static final String NBT_TICKS = "Ticks";
    private static final String NBT_ID = "Id";
    private static final String NBT_MISSING = "Missing";
    private static final String NBT_EXECUTION_STARTED = "ExecutionStarted";

    private INetwork network;
    private ItemStack requested;
    private int quantity;
    private ICraftingPattern pattern;
    private List<CraftingStep> steps = new LinkedList<>();
    private CraftingInserter inserter;
    private Set<ICraftingPattern> patternsUsed = new HashSet<>();
    private int ticks = 0;
    private long calculationStarted;
    private long executionStarted = -1;
    private UUID id = UUID.randomUUID();

    private IStackList<ItemStack> toTake = API.instance().createItemStackList();
    private IStackList<ItemStack> missing = API.instance().createItemStackList();
    private IStackList<ItemStack> toCraft = API.instance().createItemStackList();

    public CraftingTask(INetwork network, ItemStack requested, int quantity, ICraftingPattern pattern) {
        this.network = network;
        this.inserter = new CraftingInserter(network);
        this.requested = requested;
        this.quantity = quantity;
        this.pattern = pattern;
    }

    public CraftingTask(INetwork network, NBTTagCompound tag) throws CraftingTaskReadException {
        this.network = network;

        this.requested = new ItemStack(tag.getCompoundTag(NBT_REQUESTED));
        if (requested.isEmpty()) {
            throw new CraftingTaskReadException("Requested item doesn't exist anymore");
        }

        this.quantity = tag.getInteger(NBT_QUANTITY);
        this.pattern = readPatternFromNbt(tag.getCompoundTag(NBT_PATTERN), network.world());
        this.inserter = new CraftingInserter(network, tag.getTagList(NBT_INSERTER, Constants.NBT.TAG_COMPOUND));
        this.ticks = tag.getInteger(NBT_TICKS);
        this.id = tag.getUniqueId(NBT_ID);

        NBTTagList steps = tag.getTagList(NBT_STEPS, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < steps.tagCount(); ++i) {
            NBTTagCompound stepTag = steps.getCompoundTagAt(i);

            this.steps.add(CraftingStep.readFromNbt(network, inserter, stepTag));
        }

        NBTTagList missing = tag.getTagList(NBT_MISSING, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < missing.tagCount(); ++i) {
            ItemStack missingItem = new ItemStack(missing.getCompoundTagAt(i));

            if (missingItem.isEmpty()) {
                throw new CraftingTaskReadException("Missing item is empty");
            }

            this.missing.add(missingItem);
        }

        if (tag.hasKey(NBT_EXECUTION_STARTED)) {
            this.executionStarted = tag.getLong(NBT_EXECUTION_STARTED);
        }
    }

    @Override
    @Nullable
    public ICraftingTaskError calculate() {
        this.calculationStarted = System.currentTimeMillis();

        int qty = this.quantity;
        int qtyPerCraft = getQuantityPerCraft(pattern, requested);
        int crafted = 0;

        IStackList<ItemStack> results = API.instance().createItemStackList();
        IStackList<ItemStack> storage = network.getItemStorageCache().getList().copy();

        ICraftingPatternChainList patternChainList = network.getCraftingManager().createPatternChainList();

        ICraftingPatternChain patternChain = patternChainList.getChain(pattern);

        while (qty > 0) {
            Pair<CraftingStep, ICraftingTaskError> result = calculateInternal(storage, results, patternChainList, patternChain.current());

            if (result.getRight() != null) {
                return result.getRight();
            }

            this.steps.add(result.getLeft());

            qty -= qtyPerCraft;

            crafted += qtyPerCraft;

            patternChain.cycle();
        }

        this.toCraft.add(requested, crafted);

        return null;
    }

    private Pair<CraftingStep, ICraftingTaskError> calculateInternal(IStackList<ItemStack> mutatedStorage, IStackList<ItemStack> results, ICraftingPatternChainList patternChainList, ICraftingPattern pattern) {
        if (System.currentTimeMillis() - calculationStarted > CALCULATION_TIMEOUT_MS) {
            return Pair.of(null, new CraftingTaskError(CraftingTaskErrorType.TOO_COMPLEX));
        }

        if (!patternsUsed.add(pattern)) {
            return Pair.of(null, new CraftingTaskError(CraftingTaskErrorType.RECURSIVE, pattern));
        }

        IStackList<ItemStack> itemsToExtract = API.instance().createItemStackList();

        NonNullList<ItemStack> took = NonNullList.create();

        for (NonNullList<ItemStack> possibleInputs : pattern.getInputs()) {
            if (possibleInputs.isEmpty()) {
                took.add(ItemStack.EMPTY);

                continue;
            }

            ItemStack possibleInput;

            if (possibleInputs.size() == 1) {
                possibleInput = possibleInputs.get(0);
            } else {
                NonNullList<ItemStack> sortedPossibleInputs = NonNullList.create();
                sortedPossibleInputs.addAll(possibleInputs);

                sortedPossibleInputs.sort((a, b) -> {
                    ItemStack ar = mutatedStorage.get(a);
                    ItemStack br = mutatedStorage.get(b);

                    return (br == null ? 0 : br.getCount()) - (ar == null ? 0 : ar.getCount());
                });

                sortedPossibleInputs.sort((a, b) -> {
                    ItemStack ar = results.get(a);
                    ItemStack br = results.get(b);

                    return (br == null ? 0 : br.getCount()) - (ar == null ? 0 : ar.getCount());
                });

                possibleInput = sortedPossibleInputs.get(0);
            }

            took.add(possibleInput);

            int flags = getFlags(possibleInput);

            ItemStack fromSelf = results.get(possibleInput, flags);
            ItemStack fromNetwork = mutatedStorage.get(possibleInput, flags);

            int remaining = possibleInput.getCount();

            while (remaining > 0) {
                if (fromSelf != null) {
                    int toTake = Math.min(remaining, fromSelf.getCount());

                    itemsToExtract.add(possibleInput, toTake);

                    results.remove(fromSelf, toTake);

                    remaining -= toTake;

                    took.set(took.size() - 1, ItemHandlerHelper.copyStackWithSize(fromSelf, possibleInput.getCount()));

                    fromSelf = results.get(possibleInput, flags);
                } else if (fromNetwork != null) {
                    int toTake = Math.min(remaining, fromNetwork.getCount());

                    this.toTake.add(possibleInput, toTake);

                    itemsToExtract.add(possibleInput, toTake);

                    mutatedStorage.remove(fromNetwork, toTake);

                    remaining -= toTake;

                    took.set(took.size() - 1, ItemHandlerHelper.copyStackWithSize(fromNetwork, possibleInput.getCount()));

                    fromNetwork = mutatedStorage.get(possibleInput, flags);
                } else {
                    ICraftingPattern subPattern = network.getCraftingManager().getPattern(possibleInput);

                    if (subPattern != null) {
                        ICraftingPatternChain subPatternChain = patternChainList.getChain(subPattern);

                        while ((fromSelf == null ? 0 : fromSelf.getCount()) < remaining) {
                            Pair<CraftingStep, ICraftingTaskError> result = calculateInternal(mutatedStorage, results, patternChainList, subPatternChain.current());

                            if (result.getRight() != null) {
                                return Pair.of(null, result.getRight());
                            }

                            this.steps.add(result.getLeft());

                            fromSelf = results.get(possibleInput, flags);
                            if (fromSelf == null) {
                                throw new IllegalStateException("Recursive calculation didn't yield anything");
                            }

                            fromNetwork = mutatedStorage.get(possibleInput, flags);

                            subPatternChain.cycle();
                        }

                        // fromSelf contains the amount crafted after the loop.
                        this.toCraft.add(possibleInput, fromSelf.getCount());
                    } else {
                        this.missing.add(possibleInput, remaining);

                        remaining = 0;
                    }
                }
            }
        }

        patternsUsed.remove(pattern);

        if (pattern.isProcessing()) {
            for (ItemStack output : pattern.getOutputs()) {
                results.add(output);
            }

            return Pair.of(new CraftingStepProcess(pattern, network, new ArrayList<>(itemsToExtract.getStacks())), null);
        } else {
            results.add(pattern.getOutput(took));

            for (ItemStack byproduct : pattern.getByproducts(took)) {
                results.add(byproduct);
            }

            return Pair.of(new CraftingStepCraft(pattern, inserter, network, new ArrayList<>(itemsToExtract.getStacks()), took), null);
        }
    }

    private int getQuantityPerCraft(ICraftingPattern pattern, ItemStack requested) {
        int qty = 0;

        for (ItemStack output : pattern.getOutputs()) {
            if (API.instance().getComparer().isEqualNoQuantity(output, requested)) {
                qty += output.getCount();

                if (!pattern.isProcessing()) {
                    break;
                }
            }
        }

        return qty;
    }

    @Override
    public boolean update() {
        if (executionStarted == -1) {
            executionStarted = System.currentTimeMillis();
        }

        boolean allCompleted = true;

        if (ticks % getTickInterval(pattern.getContainer().getSpeedUpgradeCount()) == 0) {
            inserter.insertOne();
        }

        for (CraftingStep step : steps) {
            if (!step.isCompleted()) {
                allCompleted = false;

                if (ticks % getTickInterval(step.getPattern().getContainer().getSpeedUpgradeCount()) == 0 && step.canExecute() && step.execute()) {
                    step.setCompleted();

                    network.getCraftingManager().onTaskChanged();
                }
            }
        }

        ticks++;

        return allCompleted && inserter.getItems().isEmpty();
    }

    @Override
    public void onCancelled() {
        inserter.insertAll();
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public ItemStack getRequested() {
        return requested;
    }

    @Override
    public int onTrackedItemInserted(ItemStack stack, int size) {
        for (CraftingStep step : steps) {
            if (step instanceof CraftingStepProcess) {
                size = ((CraftingStepProcess) step).onTrackedItemInserted(stack, size);

                if (size == 0) {
                    break;
                }
            }
        }

        return size;
    }

    @Override
    public List<ICraftingMonitorElement> getCraftingMonitorElements() {
        ICraftingMonitorElementList elements = API.instance().createCraftingMonitorElementList();

        if (!missing.isEmpty()) {
            elements.directAdd(new CraftingMonitorElementText("gui.refinedstorage:crafting_monitor.items_missing", 5));

            missing.getStacks().stream().map(stack -> new CraftingMonitorElementColor(new CraftingMonitorElementItemRender(
                stack,
                stack.getCount(),
                16
            ), "", CraftingMonitorElementColor.COLOR_ERROR)).forEach(elements::add);

            elements.commit();
        }

        if (!inserter.getItems().isEmpty()) {
            elements.directAdd(new CraftingMonitorElementText("gui.refinedstorage:crafting_monitor.items_inserting", 5));

            for (CraftingInserterItem item : inserter.getItems()) {
                ICraftingMonitorElement element = new CraftingMonitorElementItemRender(
                    item.getStack(),
                    item.getStack().getCount(),
                    16
                );

                if (item.getStatus() == CraftingInserterItemStatus.FULL) {
                    element = new CraftingMonitorElementColor(element, "gui.refinedstorage:crafting_monitor.network_full", CraftingMonitorElementColor.COLOR_ERROR);
                }

                elements.add(element);
            }

            elements.commit();
        }

        if (steps.stream().anyMatch(s -> s instanceof CraftingStepCraft && !s.isCompleted() && !((CraftingStepCraft) s).getExtractor().getItems().isEmpty())) {
            elements.directAdd(new CraftingMonitorElementText("gui.refinedstorage:crafting_monitor.items_crafting", 5));

            for (CraftingStep step : steps) {
                if (step instanceof CraftingStepCraft && !step.isCompleted()) {
                    CraftingExtractor extractor = ((CraftingStepCraft) step).getExtractor();

                    for (int i = 0; i < extractor.getItems().size(); ++i) {
                        ItemStack item = extractor.getItems().get(i);
                        CraftingExtractorItemStatus status = extractor.getStatus().get(i);

                        ICraftingMonitorElement element = new CraftingMonitorElementItemRender(
                            item,
                            item.getCount(),
                            16
                        );

                        if (status == CraftingExtractorItemStatus.MISSING) {
                            element = new CraftingMonitorElementColor(element, "gui.refinedstorage:crafting_monitor.waiting_for_items", CraftingMonitorElementColor.COLOR_INFO);
                        }

                        elements.add(element);
                    }
                }
            }

            elements.commit();
        }

        if (steps.stream().anyMatch(s -> s instanceof CraftingStepProcess && !s.isCompleted())) {
            elements.directAdd(new CraftingMonitorElementText("gui.refinedstorage:crafting_monitor.items_processing", 5));

            for (CraftingStep step : steps) {
                if (step instanceof CraftingStepProcess && !step.isCompleted()) {
                    CraftingExtractor extractor = ((CraftingStepProcess) step).getExtractor();

                    for (int i = 0; i < extractor.getItems().size(); ++i) {
                        ItemStack item = extractor.getItems().get(i);
                        CraftingExtractorItemStatus status = extractor.getStatus().get(i);

                        ICraftingMonitorElement element = new CraftingMonitorElementItemRender(
                            item,
                            item.getCount(),
                            16
                        );

                        if (status == CraftingExtractorItemStatus.MISSING) {
                            element = new CraftingMonitorElementColor(element, "gui.refinedstorage:crafting_monitor.waiting_for_items", CraftingMonitorElementColor.COLOR_INFO);
                        } else if (status == CraftingExtractorItemStatus.MACHINE_DOES_NOT_ACCEPT) {
                            element = new CraftingMonitorElementColor(element, "gui.refinedstorage:crafting_monitor.machine_does_not_accept", CraftingMonitorElementColor.COLOR_ERROR);
                        } else if (status == CraftingExtractorItemStatus.MACHINE_NONE) {
                            element = new CraftingMonitorElementColor(element, "gui.refinedstorage:crafting_monitor.machine_none", CraftingMonitorElementColor.COLOR_ERROR);
                        } else if (status == CraftingExtractorItemStatus.EXTRACTED) {
                            element = new CraftingMonitorElementColor(element, "gui.refinedstorage:crafting_monitor.item_inserted_into_machine", CraftingMonitorElementColor.COLOR_SUCCESS);
                        }

                        elements.add(element);
                    }
                }
            }

            elements.commit();
        }

        return elements.getElements();
    }

    @Override
    public List<ICraftingPreviewElement> getPreviewStacks() {
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

        return new ArrayList<>(map.values());
    }

    @Override
    public ICraftingPattern getPattern() {
        return pattern;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public long getExecutionStarted() {
        return executionStarted;
    }

    @Override
    public IStackList<ItemStack> getMissing() {
        return missing;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public NBTTagCompound writeToNbt(NBTTagCompound tag) {
        tag.setTag(NBT_REQUESTED, requested.serializeNBT());
        tag.setInteger(NBT_QUANTITY, quantity);
        tag.setTag(NBT_PATTERN, writePatternToNbt(pattern));
        tag.setTag(NBT_INSERTER, inserter.writeToNbt());
        tag.setInteger(NBT_TICKS, ticks);
        tag.setUniqueId(NBT_ID, id);
        tag.setLong(NBT_EXECUTION_STARTED, executionStarted);

        NBTTagList steps = new NBTTagList();
        for (CraftingStep step : this.steps) {
            steps.appendTag(step.writeToNbt());
        }

        tag.setTag(NBT_STEPS, steps);

        NBTTagList missing = new NBTTagList();
        for (ItemStack missingItem : this.missing.getStacks()) {
            missing.appendTag(missingItem.serializeNBT());
        }

        tag.setTag(NBT_MISSING, missing);

        return tag;
    }

    private int getTickInterval(int speedUpgrades) {
        switch (speedUpgrades) {
            case 1:
                return 8;
            case 2:
                return 6;
            case 3:
                return 4;
            case 4:
                return 2;
            case 0:
            default:
                return 10;
        }
    }

    private static final String NBT_PATTERN_STACK = "Stack";
    private static final String NBT_PATTERN_CONTAINER_POS = "ContainerPos";

    public static NBTTagCompound writePatternToNbt(ICraftingPattern pattern) {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setTag(NBT_PATTERN_STACK, pattern.getStack().serializeNBT());
        tag.setLong(NBT_PATTERN_CONTAINER_POS, pattern.getContainer().getPosition().toLong());

        return tag;
    }

    public static ICraftingPattern readPatternFromNbt(NBTTagCompound tag, World world) throws CraftingTaskReadException {
        BlockPos containerPos = BlockPos.fromLong(tag.getLong(NBT_PATTERN_CONTAINER_POS));

        INetworkNode node = API.instance().getNetworkNodeManager(world).getNode(containerPos);

        if (node instanceof ICraftingPatternContainer) {
            ItemStack stack = new ItemStack(tag.getCompoundTag(NBT_PATTERN_STACK));

            if (stack.getItem() instanceof ICraftingPatternProvider) {
                return ((ICraftingPatternProvider) stack.getItem()).create(world, stack, (ICraftingPatternContainer) node);
            } else {
                throw new CraftingTaskReadException("Pattern stack is not a crafting pattern provider");
            }
        } else {
            throw new CraftingTaskReadException("Crafting pattern container doesn't exist anymore");
        }
    }

    public static int getFlags(ItemStack stack) {
        if (stack.getItem().isDamageable()) {
            return IComparer.COMPARE_NBT;
        }

        return IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    }
}
