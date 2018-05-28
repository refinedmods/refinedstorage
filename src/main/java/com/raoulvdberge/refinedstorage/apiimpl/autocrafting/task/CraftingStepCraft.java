package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.List;

public class CraftingStepCraft extends CraftingStep {
    private CraftingInserter inserter;
    private CraftingExtractor extractor;
    
    private NonNullList<ItemStack> took;

    public CraftingStepCraft(ICraftingPattern pattern, CraftingInserter inserter, INetwork network, List<ItemStack> toExtract, NonNullList<ItemStack> took) {
        super(pattern);

        this.inserter = inserter;
        this.extractor = new CraftingExtractor(network, toExtract);
        this.took = took;
    }

    @Override
    public boolean execute() {
        extractor.extractOne();

        boolean allExtracted = extractor.isAllExtracted();

        if (allExtracted) {
            inserter.insert(pattern.getOutput(took));

            for (ItemStack byproduct : pattern.getByproducts(took)) {
                inserter.insert(byproduct);
            }
        }

        return allExtracted;
    }

    @Override
    public boolean canExecute() {
        extractor.updateStatus();

        return extractor.isAllAvailable();
    }

    public CraftingExtractor getExtractor() {
        return extractor;
    }
}
