package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.task.*;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.calculator.CraftingCalculator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class CraftingTaskFactory implements ICraftingTaskFactory {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "v6");

    @Override
    public ICalculationResult create(INetwork network, ICraftingRequestInfo requested, int quantity, ICraftingPattern pattern) {
        CraftingCalculator calculator = new CraftingCalculator(network, requested, quantity, pattern);
        return calculator.calculate();
    }

    @Override
    public ICraftingTask createFromNbt(INetwork network, CompoundTag tag) throws CraftingTaskReadException {
        return new CraftingTask(network, tag);
    }
}