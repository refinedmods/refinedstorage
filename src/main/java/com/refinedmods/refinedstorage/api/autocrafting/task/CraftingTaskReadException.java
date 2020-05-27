package com.refinedmods.refinedstorage.api.autocrafting.task;

import com.refinedmods.refinedstorage.api.network.INetwork;

/**
 * Gets thrown from {@link ICraftingTaskFactory#createFromNbt(INetwork, net.minecraft.nbt.CompoundNBT)}.
 */
public class CraftingTaskReadException extends Exception {
    /**
     * @param message the message
     */
    public CraftingTaskReadException(String message) {
        super(message);
    }
}
