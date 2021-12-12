package com.refinedmods.refinedstorage.api.autocrafting.task;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.network.INetwork;
import net.minecraft.nbt.CompoundTag;

/**
 * A factory that creates a crafting task.
 * Register this factory in the {@link ICraftingTaskRegistry}.
 */
public interface ICraftingTaskFactory {
    /**
     * Returns a crafting task for a given pattern.
     *
     * @param network   the network
     * @param requested the request info
     * @param pattern   the pattern
     * @param quantity  the quantity
     * @return the calculation result
     */
    ICalculationResult create(INetwork network, ICraftingRequestInfo requested, int quantity, ICraftingPattern pattern);

    /**
     * Returns a crafting task for a given NBT tag.
     *
     * @param network the network
     * @param tag     the tag
     * @return the crafting task
     */
    ICraftingTask createFromNbt(INetwork network, CompoundTag tag) throws CraftingTaskReadException;
}
