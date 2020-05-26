package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v5;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingRequestInfo;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTaskFactory;
import com.refinedmods.refinedstorage.api.network.INetwork;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class CraftingTaskFactory implements ICraftingTaskFactory {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "v5");

    @Nonnull
    @Override
    public ICraftingTask create(INetwork network, ICraftingRequestInfo requested, int quantity, ICraftingPattern pattern) {
        return new CraftingTask(network, requested, quantity, pattern);
    }

    @Override
    public ICraftingTask createFromNbt(INetwork network, CompoundNBT tag) throws CraftingTaskReadException {
        return new CraftingTask(network, tag);
    }
}