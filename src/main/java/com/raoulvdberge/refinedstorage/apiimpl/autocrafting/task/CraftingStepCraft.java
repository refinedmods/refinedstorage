package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class CraftingStepCraft extends CraftingStep {
    private INetwork network;

    private IStackList<ItemStack> toExtract;
    private NonNullList<ItemStack> took;

    public CraftingStepCraft(ICraftingPattern pattern, INetwork network, IStackList<ItemStack> toExtract, NonNullList<ItemStack> took) {
        super(pattern);

        this.network = network;
        this.toExtract = toExtract;
        this.took = took;
    }

    @Override
    public void execute() {
        for (ItemStack toExtractItem : toExtract.getStacks()) {
            ItemStack extracted = network.extractItem(toExtractItem, toExtractItem.getCount(), false);

            if (extracted == null) {
                throw new IllegalStateException("Did not extract anything");
            }
        }

        ItemStack output = pattern.getOutput(took);

        network.insertItem(output, output.getCount(), false);

        for (ItemStack byproduct : pattern.getByproducts(took)) {
            network.insertItem(byproduct, byproduct.getCount(), false);
        }
    }

    @Override
    public boolean canExecute() {
        for (ItemStack toExtractItem : toExtract.getStacks()) {
            ItemStack inNetwork = network.extractItem(toExtractItem, toExtractItem.getCount(), true);

            if (inNetwork == null || inNetwork.getCount() < toExtractItem.getCount()) {
                return false;
            }
        }

        return true;
    }

    public IStackList<ItemStack> getToExtract() {
        return toExtract;
    }
}
