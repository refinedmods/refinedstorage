package com.raoulvdberge.refinedstorage.api.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Gets thrown from {@link com.raoulvdberge.refinedstorage.api.autocrafting.registry.ICraftingTaskFactory#createFromNbt(INetwork, NBTTagCompound)}.
 */
public class CraftingTaskReadException extends Exception {
    /**
     * @param message the message
     */
    public CraftingTaskReadException(String message) {
        super(message);
    }
}
