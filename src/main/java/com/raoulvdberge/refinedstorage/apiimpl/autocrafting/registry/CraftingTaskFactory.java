package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.registry;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.registry.ICraftingTaskFactory;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.CraftingTask;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public class CraftingTaskFactory implements ICraftingTaskFactory {
    public static final String ID = "normal";

    @Nonnull
    @Override
    public ICraftingTask create(INetwork network, ItemStack stack, int quantity, ICraftingPattern pattern) {
        return new CraftingTask(network, stack, quantity, pattern);
    }

    @Override
    public ICraftingTask createFromNbt(INetwork network, NBTTagCompound tag) throws CraftingTaskReadException {
        return new CraftingTask(network, tag);
    }
}