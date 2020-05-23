package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.v5;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingRequestInfo;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTaskFactory;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class CraftingTaskFactory implements ICraftingTaskFactory {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "v5");

    @Nonnull
    @Override
    public ICraftingTask create(INetwork network, ICraftingRequestInfo requested, int quantity, ICraftingPattern pattern) {
        return new com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.v5.CraftingTask(network, requested, quantity, pattern);
    }

    @Override
    public ICraftingTask createFromNbt(INetwork network, CompoundNBT tag) throws CraftingTaskReadException {
        return new com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.v5.CraftingTask(network, tag);
    }
}