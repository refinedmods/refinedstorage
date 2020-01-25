package com.raoulvdberge.refinedstorage.api.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.network.INetwork;

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
