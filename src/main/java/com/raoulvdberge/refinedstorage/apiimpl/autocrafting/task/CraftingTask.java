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
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementItemRender;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementItemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

import java.util.*;

public class CraftingTask implements ICraftingTask {
    private INetwork network;
    private ItemStack requested;
    private int quantity;
    private ICraftingPattern pattern;
    private List<CraftingStepSlot> steps = new LinkedList<>();

    private IStackList<ItemStack> toTake = API.instance().createItemStackList();
    private IStackList<ItemStack> missing = API.instance().createItemStackList();
    private IStackList<ItemStack> toCraft = API.instance().createItemStackList();

    public CraftingTask(INetwork network, ItemStack requested, int quantity, ICraftingPattern pattern) {
        this.network = network;
        this.requested = requested;
        this.quantity = quantity;
        this.pattern = pattern;
    }

    @Override
    public void calculate() {
        int qty = this.quantity;

        IStackList<ItemStack> results = API.instance().createItemStackList();
        IStackList<ItemStack> storage = network.getItemStorageCache().getList().copy();

        while (qty > 0) {
            this.steps.add(new CraftingStepSlot(calculateInternal(storage, results, pattern)));

            qty -= getQuantityPerCraft(pattern, requested);
        }
    }

    private ICraftingStep calculateInternal(IStackList<ItemStack> mutatedStorage, IStackList<ItemStack> results, ICraftingPattern pattern) {
        IStackList<ItemStack> itemsToExtract = API.instance().createItemStackList();

        NonNullList<ItemStack> took = NonNullList.create();

        for (NonNullList<ItemStack> possibleInputs : pattern.getInputs()) {
            if (possibleInputs.isEmpty()) {
                took.add(ItemStack.EMPTY);

                continue;
            }

            ItemStack possibleInput = possibleInputs.get(0); // TODO: Use first for now.

            took.add(possibleInput);

            int toExtract = possibleInput.getCount();

            ItemStack fromSelf = results.get(possibleInput);
            if (fromSelf != null) {
                results.remove(possibleInput, Math.min(possibleInput.getCount(), fromSelf.getCount()));

                toExtract -= fromSelf.getCount();
            }

            if (toExtract > 0) {
                ItemStack fromNetwork = mutatedStorage.get(possibleInput);

                int fromNetworkCount = fromNetwork == null ? 0 : Math.min(toExtract, fromNetwork.getCount());

                itemsToExtract.add(possibleInput);

                if (fromNetworkCount > 0) {
                    this.toTake.add(possibleInput, fromNetworkCount);

                    mutatedStorage.remove(possibleInput, fromNetworkCount);
                }

                if (fromNetworkCount < toExtract) {
                    int missing = toExtract - fromNetworkCount;

                    ICraftingPattern subPattern = network.getCraftingManager().getPattern(possibleInput, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT);

                    if (subPattern == null) {
                        this.missing.add(possibleInput, missing);
                    } else {
                        this.toCraft.add(possibleInput, missing);

                        while (missing > 0) {
                            this.steps.add(new CraftingStepSlot(calculateInternal(mutatedStorage, results, subPattern)));

                            missing -= getQuantityPerCraft(subPattern, possibleInput);
                        }
                    }
                }
            }
        }

        if (pattern.isProcessing()) {
            for (ItemStack output : pattern.getOutputs()) {
                results.add(output);
            }

            for (ItemStack byproduct : pattern.getByproducts()) {
                results.add(byproduct);
            }
        } else {
            results.add(pattern.getOutput(took));

            for (ItemStack byproduct : pattern.getByproducts(took)) {
                results.add(byproduct);
            }
        }

        return new CraftingStepCraft(network, itemsToExtract, pattern);
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
        boolean allFulfilled = true;

        for (CraftingStepSlot slot : steps) {
            if (!slot.isFulfilled()) {
                allFulfilled = false;

                if (slot.canExecute() && slot.getStep().execute()) {
                    slot.setFulfilled();
                }
            }
        }

        return allFulfilled;
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
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        return tag;
    }

    @Override
    public List<ICraftingMonitorElement> getCraftingMonitorElements() {
        ICraftingMonitorElementList elements = API.instance().createCraftingMonitorElementList();

        elements.directAdd(new CraftingMonitorElementItemRender(network.getCraftingManager().getTasks().indexOf(this), requested, quantity, 0));

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
}
