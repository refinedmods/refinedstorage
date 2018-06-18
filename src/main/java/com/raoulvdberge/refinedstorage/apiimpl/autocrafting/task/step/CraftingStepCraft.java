package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.step;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.extractor.CraftingExtractor;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.inserter.CraftingInserter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;

import java.util.List;

public class CraftingStepCraft extends CraftingStep {
    private static final String NBT_EXTRACTOR = "Extractor";
    private static final String NBT_TOOK = "Took";

    private CraftingInserter inserter;
    private CraftingExtractor extractor;

    private NonNullList<ItemStack> took;

    public CraftingStepCraft(ICraftingPattern pattern, CraftingInserter inserter, INetwork network, List<ItemStack> toExtract, NonNullList<ItemStack> took) {
        super(pattern);

        if (pattern.isProcessing()) {
            throw new IllegalArgumentException("Cannot pass processing pattern to craft handler");
        }

        this.inserter = inserter;
        this.extractor = new CraftingExtractor(network, toExtract, false);
        this.took = took;
    }

    @Override
    public boolean canExecute() {
        extractor.updateStatus(null);

        return extractor.isAllAvailable();
    }

    @Override
    public boolean execute() {
        extractor.extractOne(null);

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
    public String getType() {
        return "craft";
    }

    @Override
    public NBTTagCompound writeToNbt() {
        NBTTagCompound tag = super.writeToNbt();

        tag.setTag(NBT_EXTRACTOR, extractor.writeToNbt());

        NBTTagList took = new NBTTagList();

        for (ItemStack stack : this.took) {
            took.appendTag(stack.serializeNBT());
        }

        tag.setTag(NBT_TOOK, took);

        return tag;
    }

    public CraftingExtractor getExtractor() {
        return extractor;
    }
}
