package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class CraftingStepCraft implements ICraftingStep {
    private INetwork network;
    private IStackList<ItemStack> toExtract;
    private NonNullList<ItemStack> took;
    private ICraftingPattern pattern;

    public CraftingStepCraft(INetwork network, IStackList<ItemStack> toExtract, NonNullList<ItemStack> took, ICraftingPattern pattern) {
        this.network = network;
        this.toExtract = toExtract;
        this.took = took;
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

        return true;
    }

    @Override
    public ICraftingPattern getPattern() {
        return pattern;
    }
}
