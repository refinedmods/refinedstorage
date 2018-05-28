package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import net.minecraft.item.ItemStack;

public class CraftingStepCraft implements ICraftingStep {
    private INetwork network;
    private IStackList<ItemStack> toExtract;
    private ICraftingPattern pattern;

    public CraftingStepCraft(INetwork network, IStackList<ItemStack> toExtract, ICraftingPattern pattern) {
        this.network = network;
        this.toExtract = toExtract;
        this.pattern = pattern;
    }

    @Override
    public boolean execute() {
        for (ItemStack toExtractItem : toExtract.getStacks()) {
            ItemStack inNetwork = network.extractItem(toExtractItem, toExtractItem.getCount(), true);

            if (inNetwork == null || inNetwork.getCount() < toExtractItem.getCount()) {
                return false;
            }
        }

        for (ItemStack toExtractItem : toExtract.getStacks()) {
            network.extractItem(toExtractItem, toExtractItem.getCount(), false);
        }

        for (ItemStack output : pattern.getOutputs()) {
            network.insertItem(output, output.getCount(), false);
        }

        for (ItemStack byproduct : pattern.getByproducts()) {
            network.insertItem(byproduct, byproduct.getCount(), false);
        }

        return true;
    }

    @Override
    public ICraftingPattern getPattern() {
        return pattern;
    }
}
