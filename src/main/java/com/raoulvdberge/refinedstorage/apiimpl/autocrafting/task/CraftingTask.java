package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementList;
import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementError;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementInfo;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementItemRender;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementText;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementItemStack;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.step.CraftingStep;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.step.CraftingStepCraft;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.step.CraftingStepProcess;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

import java.util.*;

public class CraftingTask implements ICraftingTask {
    private INetwork network;
    private ItemStack requested;
    private int quantity;
    private ICraftingPattern pattern;
    private List<CraftingStep> steps = new LinkedList<>();
    private CraftingInserter inserter;
    private int ticks = 0;

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

    @Override
    public void calculate() {
        int qty = this.quantity;

        IStackList<ItemStack> results = API.instance().createItemStackList();
        IStackList<ItemStack> storage = network.getItemStorageCache().getList().copy();

        this.toCraft.add(requested, quantity);

        while (qty > 0) {
            this.steps.add(calculateInternal(storage, results, pattern));

            qty -= getQuantityPerCraft(pattern, requested);
        }
    }

    private CraftingStep calculateInternal(IStackList<ItemStack> mutatedStorage, IStackList<ItemStack> results, ICraftingPattern pattern) {
        IStackList<ItemStack> itemsToExtract = API.instance().createItemStackList();

        NonNullList<ItemStack> took = NonNullList.create();

        for (NonNullList<ItemStack> possibleInputs : pattern.getInputs()) {
            if (possibleInputs.isEmpty()) {
                took.add(ItemStack.EMPTY);

                continue;
            }

            ItemStack possibleInput = possibleInputs.get(0); // TODO: Use first for now.

            took.add(possibleInput);

            ItemStack fromSelf = results.get(possibleInput);
            ItemStack fromNetwork = mutatedStorage.get(possibleInput);

            int available = (fromNetwork == null ? 0 : fromNetwork.getCount()) + (fromSelf == null ? 0 : fromSelf.getCount());

            if (available < possibleInput.getCount()) {
                ICraftingPattern subPattern = network.getCraftingManager().getPattern(possibleInput, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT);

                int needed = possibleInput.getCount() - available;

                if (subPattern != null) {
                    while ((fromSelf == null ? 0 : fromSelf.getCount()) < needed) {
                        this.steps.add(calculateInternal(mutatedStorage, results, subPattern));

                        fromSelf = results.get(possibleInput);
                        if (fromSelf == null) {
                            throw new IllegalStateException("Recursive calculation didn't yield anything");
                        }
                    }
                }
            }

            fromNetwork = mutatedStorage.get(possibleInput);

            int remaining = possibleInput.getCount();

            while (remaining > 0) {
                if (fromSelf != null) {
                    int toTake = Math.min(remaining, fromSelf.getCount());

                    this.toCraft.add(possibleInput, toTake);

                    itemsToExtract.add(possibleInput, toTake);

                    results.remove(possibleInput, toTake);

                    remaining -= toTake;

                    fromSelf = results.get(possibleInput);
                } else if (fromNetwork != null) {
                    int toTake = Math.min(remaining, fromNetwork.getCount());

                    this.toTake.add(possibleInput, toTake);

                    itemsToExtract.add(possibleInput, toTake);

                    mutatedStorage.remove(possibleInput, toTake);

                    remaining -= toTake;

                    fromNetwork = mutatedStorage.get(possibleInput);
                } else {
                    this.missing.add(possibleInput, remaining);

                    remaining = 0;
                }
            }
        }

        if (pattern.isProcessing()) {
            for (ItemStack output : pattern.getOutputs()) {
                results.add(output);
            }

            return new CraftingStepProcess(pattern, network, new ArrayList<>(itemsToExtract.getStacks()));
        } else {
            results.add(pattern.getOutput(took));

            for (ItemStack byproduct : pattern.getByproducts(took)) {
                results.add(byproduct);
            }

            return new CraftingStepCraft(pattern, inserter, network, new ArrayList<>(itemsToExtract.getStacks()), took);
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
        boolean allCompleted = true;

        for (CraftingStep step : steps) {
            if (!step.isCompleted()) {
                allCompleted = false;

                if (ticks % getTickInterval(step.getPattern().getContainer().getSpeedUpgradeCount()) == 0 && step.canExecute() && step.execute()) {
                    step.setCompleted();

                    network.getCraftingManager().sendCraftingMonitorUpdate();
                }
            }
        }

        inserter.insertSingle();

        ticks++;

        return allCompleted && inserter.getItems().isEmpty();
    }

    @Override
    public void onCancelled() {
        // TODO
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
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        return tag;
    }

    @Override
    public List<ICraftingMonitorElement> getCraftingMonitorElements() {
        ICraftingMonitorElementList elements = API.instance().createCraftingMonitorElementList();

        elements.directAdd(new CraftingMonitorElementItemRender(
            network.getCraftingManager().getTasks().indexOf(this),
            requested,
            quantity,
            0
        ));

        if (!missing.isEmpty()) {
            elements.directAdd(new CraftingMonitorElementText("gui.refinedstorage:crafting_monitor.items_missing", 16));

            missing.getStacks().stream().map(stack -> new CraftingMonitorElementError(new CraftingMonitorElementItemRender(
                -1,
                stack,
                stack.getCount(),
                32
            ), "")).forEach(elements::add);

            elements.commit();
        }

        if (!inserter.getItems().isEmpty()) {
            elements.directAdd(new CraftingMonitorElementText("gui.refinedstorage:crafting_monitor.items_inserting", 16));

            for (CraftingInserterItem item : inserter.getItems()) {
                ICraftingMonitorElement element = new CraftingMonitorElementItemRender(
                    -1,
                    item.getStack(),
                    item.getStack().getCount(),
                    32
                );

                if (item.getStatus() == CraftingInserterItemStatus.FULL) {
                    element = new CraftingMonitorElementError(element, "gui.refinedstorage:crafting_monitor.network_full");
                }

                elements.add(element);
            }

            elements.commit();
        }

        if (steps.stream().anyMatch(s -> s instanceof CraftingStepCraft && !s.isCompleted())) {
            elements.directAdd(new CraftingMonitorElementText("gui.refinedstorage:crafting_monitor.items_crafting", 16));

            for (CraftingStep step : steps) {
                if (step instanceof CraftingStepCraft && !step.isCompleted()) {
                    CraftingExtractor extractor = ((CraftingStepCraft) step).getExtractor();

                    for (int i = 0; i < extractor.getItems().size(); ++i) {
                        ItemStack item = extractor.getItems().get(i);
                        CraftingExtractorItemStatus status = extractor.getStatus().get(i);

                        ICraftingMonitorElement element = new CraftingMonitorElementItemRender(
                            -1,
                            item,
                            item.getCount(),
                            32
                        );

                        if (status == CraftingExtractorItemStatus.MISSING) {
                            element = new CraftingMonitorElementInfo(element, "gui.refinedstorage:crafting_monitor.waiting_for_items");
                        }

                        elements.add(element);
                    }
                }
            }

            elements.commit();
        }

        if (steps.stream().anyMatch(s -> s instanceof CraftingStepProcess && !s.isCompleted())) {
            elements.directAdd(new CraftingMonitorElementText("gui.refinedstorage:crafting_monitor.items_processing", 16));

            for (CraftingStep step : steps) {
                if (step instanceof CraftingStepProcess && !step.isCompleted()) {
                    CraftingExtractor extractor = ((CraftingStepProcess) step).getExtractor();

                    for (int i = 0; i < extractor.getItems().size(); ++i) {
                        ItemStack item = extractor.getItems().get(i);
                        CraftingExtractorItemStatus status = extractor.getStatus().get(i);

                        ICraftingMonitorElement element = new CraftingMonitorElementItemRender(
                            -1,
                            item,
                            item.getCount(),
                            32
                        );

                        if (status == CraftingExtractorItemStatus.MISSING) {
                            element = new CraftingMonitorElementInfo(element, "gui.refinedstorage:crafting_monitor.waiting_for_items");
                        } else if (status == CraftingExtractorItemStatus.MACHINE_DOES_NOT_ACCEPT) {
                            element = new CraftingMonitorElementError(element, "gui.refinedstorage:crafting_monitor.machine_does_not_accept");
                        } else if (status == CraftingExtractorItemStatus.MACHINE_NONE) {
                            element = new CraftingMonitorElementError(element, "gui.refinedstorage:crafting_monitor.machine_none");
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
    public IStackList<ItemStack> getMissing() {
        return missing;
    }

    private int getTickInterval(int speedUpgrades) {
        switch (speedUpgrades) {
            case 1:
                return 10;
            case 2:
                return 8;
            case 3:
                return 6;
            case 4:
                return 4;
            case 0:
            default:
                return 20;
        }
    }
}
