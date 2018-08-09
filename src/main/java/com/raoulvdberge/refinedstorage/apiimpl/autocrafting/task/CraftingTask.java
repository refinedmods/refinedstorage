package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternChain;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternChainList;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementList;
import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingRequestInfo;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTaskError;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementColor;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementFluidRender;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementItemRender;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementText;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementFluidStack;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementItemStack;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.StorageDiskFluid;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.StorageDiskItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.*;

public class CraftingTask implements ICraftingTask {
    private INetwork network;
    private ICraftingRequestInfo requested;
    private int quantity;
    private ICraftingPattern pattern;
    private UUID id = UUID.randomUUID();
    private int ticks;
    private long executionStarted = -1;

    private IStorage<ItemStack> internalStorage;
    private IStorage<FluidStack> internalFluidStorage;

    private IStackList<ItemStack> toExtractInitial = API.instance().createItemStackList();
    private IStackList<FluidStack> toExtractInitialFluids = API.instance().createFluidStackList();

    private List<Crafting> crafting = new ArrayList<>();
    private List<Processing> processing = new ArrayList<>();

    private IStackList<ItemStack> toTake = API.instance().createItemStackList();
    private IStackList<FluidStack> toTakeFluids = API.instance().createFluidStackList();

    private IStackList<ItemStack> missing = API.instance().createItemStackList();
    private IStackList<FluidStack> missingFluids = API.instance().createFluidStackList();

    private IStackList<ItemStack> toCraft = API.instance().createItemStackList();
    private IStackList<FluidStack> toCraftFluids = API.instance().createFluidStackList();

    public CraftingTask(INetwork network, ICraftingRequestInfo requested, int quantity, ICraftingPattern pattern) {
        this.network = network;
        this.requested = requested;
        this.quantity = quantity;
        this.pattern = pattern;

        this.internalStorage = new StorageDiskItem(network.world(), -1);
        this.internalFluidStorage = new StorageDiskFluid(network.world(), -1);
    }

    @Override
    @Nullable
    public ICraftingTaskError calculate() {
        int qty = this.quantity;
        int qtyPerCraft = getQuantityPerCraft();
        int crafted = 0;

        IStackList<ItemStack> results = API.instance().createItemStackList();
        IStackList<FluidStack> fluidResults = API.instance().createFluidStackList();

        IStackList<ItemStack> storage = network.getItemStorageCache().getList().copy();
        IStackList<FluidStack> fluidStorage = network.getFluidStorageCache().getList().copy();

        ICraftingPatternChainList patternChainList = network.getCraftingManager().createPatternChainList();

        ICraftingPatternChain patternChain = patternChainList.getChain(pattern);

        while (qty > 0) {
            ICraftingTaskError result = calculateInternal(storage, fluidStorage, results, fluidResults, patternChainList, patternChain.current());

            if (result != null) {
                return result;
            }

            qty -= qtyPerCraft;

            crafted += qtyPerCraft;

            patternChain.cycle();
        }

        if (requested.getItem() != null) {
            this.toCraft.add(requested.getItem(), crafted);
        } else {
            this.toCraftFluids.add(requested.getFluid(), crafted);
        }

        return null;
    }

    @Nullable
    private ICraftingTaskError calculateInternal(
        IStackList<ItemStack> mutatedStorage,
        IStackList<FluidStack> mutatedFluidStorage,
        IStackList<ItemStack> results,
        IStackList<FluidStack> fluidResults,
        ICraftingPatternChainList patternChainList,
        ICraftingPattern pattern) {

        IStackList<ItemStack> itemsToExtract = API.instance().createItemStackList();
        IStackList<FluidStack> fluidsToExtract = API.instance().createFluidStackList();

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

                    toExtractInitial.add(took.get(took.size() - 1));
                } else {
                    ICraftingPattern subPattern = network.getCraftingManager().getPattern(possibleInput);

                    if (subPattern != null) {
                        ICraftingPatternChain subPatternChain = patternChainList.getChain(subPattern);

                        while ((fromSelf == null ? 0 : fromSelf.getCount()) < remaining) {
                            ICraftingTaskError result = calculateInternal(mutatedStorage, mutatedFluidStorage, results, fluidResults, patternChainList, subPatternChain.current());

                            if (result != null) {
                                return result;
                            }

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

                        itemsToExtract.add(possibleInput, remaining);

                        remaining = 0;
                    }
                }
            }
        }

        for (FluidStack input : pattern.getFluidInputs()) {
            FluidStack fromSelf = fluidResults.get(input, IComparer.COMPARE_NBT);
            FluidStack fromNetwork = mutatedFluidStorage.get(input, IComparer.COMPARE_NBT);

            int remaining = input.amount;

            while (remaining > 0) {
                if (fromSelf != null) {
                    int toTake = Math.min(remaining, fromSelf.amount);

                    fluidsToExtract.add(input, toTake);

                    fluidResults.remove(input, toTake);

                    remaining -= toTake;

                    fromSelf = fluidResults.get(input, IComparer.COMPARE_NBT);
                } else if (fromNetwork != null) {
                    int toTake = Math.min(remaining, fromNetwork.amount);

                    this.toTakeFluids.add(input, toTake);

                    fluidsToExtract.add(input, toTake);

                    mutatedFluidStorage.remove(fromNetwork, toTake);

                    remaining -= toTake;

                    fromNetwork = mutatedFluidStorage.get(input, IComparer.COMPARE_NBT);

                    toExtractInitialFluids.add(input);
                } else {
                    ICraftingPattern subPattern = network.getCraftingManager().getPattern(input);

                    if (subPattern != null) {
                        ICraftingPatternChain subPatternChain = patternChainList.getChain(subPattern);

                        while ((fromSelf == null ? 0 : fromSelf.amount) < remaining) {
                            ICraftingTaskError result = calculateInternal(mutatedStorage, mutatedFluidStorage, results, fluidResults, patternChainList, subPatternChain.current());

                            if (result != null) {
                                return result;
                            }

                            fromSelf = fluidResults.get(input, IComparer.COMPARE_NBT);
                            if (fromSelf == null) {
                                throw new IllegalStateException("Recursive fluid calculation didn't yield anything");
                            }

                            fromNetwork = mutatedFluidStorage.get(input, IComparer.COMPARE_NBT);

                            subPatternChain.cycle();
                        }

                        // fromSelf contains the amount crafted after the loop.
                        this.toCraftFluids.add(input, fromSelf.amount);
                    } else {
                        this.missingFluids.add(input, remaining);

                        fluidsToExtract.add(input, remaining);

                        remaining = 0;
                    }
                }
            }
        }

        if (pattern.isProcessing()) {
            IStackList<ItemStack> itemsToReceive = API.instance().createItemStackList();
            IStackList<FluidStack> fluidsToReceive = API.instance().createFluidStackList();

            for (ItemStack output : pattern.getOutputs()) {
                results.add(output);

                itemsToReceive.add(output);
            }

            for (FluidStack output : pattern.getFluidOutputs()) {
                fluidResults.add(output);

                fluidsToReceive.add(output);
            }

            processing.add(new Processing(pattern, itemsToReceive, fluidsToReceive, new ArrayList<>(itemsToExtract.getStacks()), new ArrayList<>(fluidsToExtract.getStacks())));
        } else {
            if (!fluidsToExtract.isEmpty()) {
                throw new IllegalStateException("Cannot extract fluids in normal pattern!");
            }

            crafting.add(new Crafting(pattern, took, itemsToExtract));

            results.add(pattern.getOutput(took));

            for (ItemStack byproduct : pattern.getByproducts(took)) {
                results.add(byproduct);
            }
        }

        return null;
    }

    private static int getTickInterval(int speedUpgrades) {
        switch (speedUpgrades) {
            case 0:
                return 10;
            case 1:
                return 8;
            case 2:
                return 6;
            case 3:
                return 4;
            case 4:
                return 2;
            default:
                return 2;
        }
    }

    private void extractInitial() {
        if (!toExtractInitial.isEmpty()) {
            List<ItemStack> toRemove = new ArrayList<>();

            for (ItemStack toExtract : toExtractInitial.getStacks()) {
                ItemStack result = network.extractItem(toExtract, toExtract.getCount(), getFlags(toExtract), Action.PERFORM);

                if (result != null) {
                    internalStorage.insert(toExtract, toExtract.getCount(), Action.PERFORM);

                    toRemove.add(result);
                }
            }

            for (ItemStack stack : toRemove) {
                toExtractInitial.remove(stack);
            }

            if (!toRemove.isEmpty()) {
                network.getCraftingManager().onTaskChanged();
            }
        }

        if (!toExtractInitialFluids.isEmpty()) {
            List<FluidStack> toRemove = new ArrayList<>();

            for (FluidStack toExtract : toExtractInitialFluids.getStacks()) {
                FluidStack result = network.extractFluid(toExtract, toExtract.amount, Action.PERFORM);

                if (result != null) {
                    internalFluidStorage.insert(toExtract, toExtract.amount, Action.PERFORM);

                    toRemove.add(result);
                }
            }

            for (FluidStack stack : toRemove) {
                toExtractInitialFluids.remove(stack);
            }

            if (!toRemove.isEmpty()) {
                network.getCraftingManager().onTaskChanged();
            }
        }
    }

    private void updateCrafting() {
        Iterator<Crafting> it = crafting.iterator();

        while (it.hasNext()) {
            Crafting c = it.next();

            if (ticks % getTickInterval(c.getPattern().getContainer().getSpeedUpgradeCount()) == 0) {
                boolean hasAll = true;

                for (ItemStack need : c.getToExtract().getStacks()) {
                    ItemStack result = this.internalStorage.extract(need, need.getCount(), getFlags(need), Action.SIMULATE);

                    if (result == null || result.getCount() != need.getCount()) {
                        hasAll = false;

                        break;
                    }
                }

                if (hasAll) {
                    for (ItemStack need : c.getToExtract().getStacks()) {
                        ItemStack result = this.internalStorage.extract(need, need.getCount(), getFlags(need), Action.PERFORM);

                        if (result == null || result.getCount() != need.getCount()) {
                            throw new IllegalStateException("Extractor check lied");
                        }
                    }

                    ItemStack output = c.getPattern().getOutput(c.getTook());
                    this.internalStorage.insert(output, output.getCount(), Action.PERFORM);

                    for (ItemStack byp : c.getPattern().getByproducts(c.getTook())) {
                        this.internalStorage.insert(byp, byp.getCount(), Action.PERFORM);
                    }

                    it.remove();

                    network.getCraftingManager().onTaskChanged();

                    return;
                }
            }
        }
    }

    private void updateProcessing() {
        Iterator<Processing> it = processing.iterator();

        while (it.hasNext()) {
            Processing p = it.next();

            if (p.getState() == ProcessingState.PROCESSED) {
                it.remove();

                network.getCraftingManager().onTaskChanged();

                continue;
            }

            if (p.getState() == ProcessingState.EXTRACTED_ALL) {
                continue;
            }

            if (ticks % getTickInterval(p.getPattern().getContainer().getSpeedUpgradeCount()) == 0) {
                ProcessingState originalState = p.getState();

                boolean hasAll = true;

                for (ItemStack need : p.getItemsToPut()) {
                    if (p.getPattern().getContainer().getConnectedInventory() == null) {
                        p.setState(ProcessingState.MACHINE_NONE);
                    } else {
                        ItemStack result = this.internalStorage.extract(need, need.getCount(), getFlags(need), Action.SIMULATE);

                        if (result == null || result.getCount() != need.getCount()) {
                            hasAll = false;

                            break;
                        } else if (!ItemHandlerHelper.insertItem(p.getPattern().getContainer().getConnectedInventory(), result, true).isEmpty()) {
                            p.setState(ProcessingState.MACHINE_DOES_NOT_ACCEPT);

                            break;
                        } else {
                            p.setState(ProcessingState.READY);
                        }
                    }
                }

                for (FluidStack need : p.getFluidsToPut()) {
                    if (p.getPattern().getContainer().getConnectedFluidInventory() == null) {
                        p.setState(ProcessingState.MACHINE_NONE);
                    } else {
                        FluidStack result = this.internalFluidStorage.extract(need, need.amount, IComparer.COMPARE_NBT, Action.SIMULATE);

                        if (result == null || result.amount != need.amount) {
                            hasAll = false;

                            break;
                        } else if (p.getPattern().getContainer().getConnectedFluidInventory().fill(result, false) != result.amount) {
                            p.setState(ProcessingState.MACHINE_DOES_NOT_ACCEPT);

                            break;
                        } else if (p.getState() == ProcessingState.READY) { // If the items were ok.
                            p.setState(ProcessingState.READY);
                        }
                    }
                }

                if (p.getState() == ProcessingState.READY && hasAll) {
                    for (ItemStack need : p.getItemsToPut()) {
                        ItemStack result = this.internalStorage.extract(need, need.getCount(), getFlags(need), Action.PERFORM);
                        if (result == null || result.getCount() != need.getCount()) {
                            throw new IllegalStateException("Could not extract from the internal inventory even though we could");
                        }

                        if (!ItemHandlerHelper.insertItem(p.getPattern().getContainer().getConnectedInventory(), result, false).isEmpty()) {
                            throw new IllegalStateException("Can't fill up inventory even though we could");
                        }
                    }

                    for (FluidStack need : p.getFluidsToPut()) {
                        FluidStack result = this.internalFluidStorage.extract(need, need.amount, IComparer.COMPARE_NBT, Action.PERFORM);
                        if (result == null || result.amount != need.amount) {
                            throw new IllegalStateException("Could not extract from the internal inventory even though we could");
                        }

                        if (p.getPattern().getContainer().getConnectedFluidInventory().fill(result, true) != result.amount) {
                            throw new IllegalStateException("Can't fill up inventory even though we could");
                        }
                    }

                    p.setState(ProcessingState.EXTRACTED_ALL);
                }

                if (originalState != p.getState()) {
                    network.getCraftingManager().onTaskChanged();
                }
            }
        }
    }

    @Override
    public boolean update() {
        if (executionStarted == -1) {
            executionStarted = System.currentTimeMillis();
        }

        ++ticks;

        extractInitial();

        if (this.crafting.isEmpty() && this.processing.isEmpty()) {
            List<Runnable> toPerform = new ArrayList<>();

            for (ItemStack stack : internalStorage.getStacks()) {
                ItemStack remainder = network.insertItem(stack, stack.getCount(), Action.PERFORM);

                toPerform.add(() -> {
                    if (remainder == null) {
                        internalStorage.extract(stack, stack.getCount(), IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT, Action.PERFORM);
                    } else {
                        internalStorage.extract(stack, stack.getCount() - remainder.getCount(), IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT, Action.PERFORM);
                    }
                });
            }

            for (FluidStack stack : internalFluidStorage.getStacks()) {
                FluidStack remainder = network.insertFluid(stack, stack.amount, Action.PERFORM);

                toPerform.add(() -> {
                    if (remainder == null) {
                        internalFluidStorage.extract(stack, stack.amount, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT, Action.PERFORM);
                    } else {
                        internalFluidStorage.extract(stack, stack.amount - remainder.amount, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT, Action.PERFORM);
                    }
                });
            }

            // Prevent CME.
            toPerform.forEach(Runnable::run);

            return internalStorage.getStacks().isEmpty() && internalFluidStorage.getStacks().isEmpty();
        } else {
            updateCrafting();
            updateProcessing();

            return false;
        }
    }

    @Override
    public void onCancelled() {
        for (ItemStack remainder : internalStorage.getStacks()) {
            network.insertItem(remainder, remainder.getCount(), Action.PERFORM);
        }

        for (FluidStack remainder : internalFluidStorage.getStacks()) {
            network.insertFluid(remainder, remainder.amount, Action.PERFORM);
        }
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public int getQuantityPerCraft() {
        int qty = 0;

        if (requested.getItem() != null) {
            for (ItemStack output : pattern.getOutputs()) {
                if (API.instance().getComparer().isEqualNoQuantity(output, requested.getItem())) {
                    qty += output.getCount();

                    if (!pattern.isProcessing()) {
                        break;
                    }
                }
            }
        } else {
            for (FluidStack output : pattern.getFluidOutputs()) {
                if (API.instance().getComparer().isEqual(output, requested.getFluid(), IComparer.COMPARE_NBT)) {
                    qty += output.amount;
                }
            }
        }

        return qty;
    }

    private static int getFlags(ItemStack stack) {
        if (stack.getItem().isDamageable()) {
            return IComparer.COMPARE_NBT;
        }

        return IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    }

    @Override
    public ICraftingRequestInfo getRequested() {
        return requested;
    }

    @Override
    public int onTrackedInsert(ItemStack stack, int size) {
        for (Processing p : this.processing) {
            if (p.getState() != ProcessingState.EXTRACTED_ALL) {
                continue;
            }

            ItemStack content = p.getItemsToReceive().get(stack);

            if (content != null) {
                int needed = content.getCount();

                if (needed > size) {
                    needed = size;
                }

                p.getItemsToReceive().remove(stack, needed);

                size -= needed;

                if (p.getItemsToReceive().isEmpty() && p.getFluidsToReceive().isEmpty()) {
                    p.setState(ProcessingState.PROCESSED);
                }

                internalStorage.insert(stack, needed, Action.PERFORM);

                if (size == 0) {
                    return 0;
                }
            }
        }

        return size;
    }

    @Override
    public int onTrackedInsert(FluidStack stack, int size) {
        for (Processing p : this.processing) {
            if (p.getState() != ProcessingState.EXTRACTED_ALL) {
                continue;
            }

            FluidStack content = p.getFluidsToReceive().get(stack);

            if (content != null) {
                int needed = content.amount;

                if (needed > size) {
                    needed = size;
                }

                p.getFluidsToReceive().remove(stack, needed);

                size -= needed;

                if (p.getItemsToReceive().isEmpty() && p.getFluidsToReceive().isEmpty()) {
                    p.setState(ProcessingState.PROCESSED);
                }

                internalFluidStorage.insert(stack, needed, Action.PERFORM);

                if (size == 0) {
                    return 0;
                }
            }
        }

        return size;
    }

    @Override
    public NBTTagCompound writeToNbt(NBTTagCompound tag) {
        return new NBTTagCompound();
    }

    @Override
    public List<ICraftingMonitorElement> getCraftingMonitorElements() {
        ICraftingMonitorElementList elements = API.instance().createCraftingMonitorElementList();

        if (!missing.isEmpty() && !missingFluids.isEmpty()) {
            elements.directAdd(new CraftingMonitorElementText("gui.refinedstorage:crafting_monitor.missing", 5));
        }

        if (!missing.isEmpty()) {
            for (ItemStack missing : this.missing.getStacks()) {
                elements.add(new CraftingMonitorElementColor(new CraftingMonitorElementItemRender(missing, missing.getCount(), 0), "", CraftingMonitorElementColor.COLOR_ERROR));
            }

            elements.commit();
        }

        if (!missingFluids.isEmpty()) {
            for (FluidStack missing : this.missingFluids.getStacks()) {
                elements.add(new CraftingMonitorElementColor(new CraftingMonitorElementFluidRender(missing, missing.amount, 0), "", CraftingMonitorElementColor.COLOR_ERROR));
            }

            elements.commit();
        }

        if (!this.crafting.isEmpty()) {
            elements.directAdd(new CraftingMonitorElementText("gui.refinedstorage:crafting_monitor.items_crafting", 5));

            for (Crafting c : this.crafting) {
                for (ItemStack s : c.getToExtract().getStacks()) {
                    elements.add(new CraftingMonitorElementItemRender(s, s.getCount(), 0));
                }
            }

            elements.commit();
        }

        if (!this.processing.isEmpty()) {
            elements.directAdd(new CraftingMonitorElementText("gui.refinedstorage:crafting_monitor.processing", 5));

            for (Processing p : this.processing) {
                for (ItemStack s : p.getItemsToReceive().getStacks()) {
                    elements.add(wrapAccordingToState(new CraftingMonitorElementItemRender(s, s.getCount(), 0), p.getState(), false));
                }
            }

            elements.commit();

            for (Processing p : this.processing) {
                for (FluidStack s : p.getFluidsToReceive().getStacks()) {
                    elements.add(wrapAccordingToState(new CraftingMonitorElementFluidRender(s, s.amount, 0), p.getState(), true));
                }
            }

            elements.commit();
        }

        return elements.getElements();
    }

    private ICraftingMonitorElement wrapAccordingToState(ICraftingMonitorElement element, ProcessingState state, boolean fluid) {
        switch (state) {
            case MACHINE_NONE:
                element = new CraftingMonitorElementColor(element, "gui.refinedstorage:crafting_monitor.machine_none", CraftingMonitorElementColor.COLOR_ERROR);
                break;
            case MACHINE_DOES_NOT_ACCEPT:
                element = new CraftingMonitorElementColor(element, fluid ? "gui.refinedstorage:crafting_monitor.machine_does_not_accept_fluid" : "gui.refinedstorage:crafting_monitor.machine_does_not_accept_item", CraftingMonitorElementColor.COLOR_ERROR);
                break;
        }

        return element;
    }

    @Override
    public List<ICraftingPreviewElement> getPreviewStacks() {
        Map<Integer, CraftingPreviewElementItemStack> map = new LinkedHashMap<>();
        Map<Integer, CraftingPreviewElementFluidStack> mapFluids = new LinkedHashMap<>();

        for (ItemStack stack : toCraft.getStacks()) {
            int hash = API.instance().getItemStackHashCode(stack);

            CraftingPreviewElementItemStack previewStack = map.get(hash);

            if (previewStack == null) {
                previewStack = new CraftingPreviewElementItemStack(stack);
            }

            previewStack.addToCraft(stack.getCount());

            map.put(hash, previewStack);
        }

        for (FluidStack stack : toCraftFluids.getStacks()) {
            int hash = API.instance().getFluidStackHashCode(stack);

            CraftingPreviewElementFluidStack previewStack = mapFluids.get(hash);

            if (previewStack == null) {
                previewStack = new CraftingPreviewElementFluidStack(stack);
            }

            previewStack.addToCraft(stack.amount);

            mapFluids.put(hash, previewStack);
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

        for (FluidStack stack : missingFluids.getStacks()) {
            int hash = API.instance().getFluidStackHashCode(stack);

            CraftingPreviewElementFluidStack previewStack = mapFluids.get(hash);

            if (previewStack == null) {
                previewStack = new CraftingPreviewElementFluidStack(stack);
            }

            previewStack.setMissing(true);
            previewStack.addToCraft(stack.amount);

            mapFluids.put(hash, previewStack);
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

        for (FluidStack stack : toTakeFluids.getStacks()) {
            int hash = API.instance().getFluidStackHashCode(stack);

            CraftingPreviewElementFluidStack previewStack = mapFluids.get(hash);

            if (previewStack == null) {
                previewStack = new CraftingPreviewElementFluidStack(stack);
            }

            previewStack.addAvailable(stack.amount);

            mapFluids.put(hash, previewStack);
        }

        List<ICraftingPreviewElement> elements = new ArrayList<>();

        elements.addAll(map.values());
        elements.addAll(mapFluids.values());

        return elements;
    }

    @Override
    public ICraftingPattern getPattern() {
        return pattern;
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
}
