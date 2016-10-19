package com.raoulvdberge.refinedstorage.api.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.util.IItemStackList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Represents a item in a crafting task that can be processed.
 */
public interface IProcessable {
    /**
     * @return the pattern
     */
    ICraftingPattern getPattern();

    /**
     * @return the stacks to insert
     */
    IItemStackList getToInsert();

    /**
     * Check if the processing can start
     *
     * @param list a list to compare the need inputs against
     * @return true if processing can start
     */
    boolean canStartProcessing(IItemStackList list);

    void setStartedProcessing();

    boolean hasStartedProcessing();

    /**
     * @return true if we received all outputs, false otherwise
     */
    boolean hasReceivedOutputs();

    /**
     * @param i the output to check
     * @return true if we received the given output, false otherwise
     */
    boolean hasReceivedOutput(int i);

    /**
     * @param stack the stack that was inserted in the storage system
     * @return true if this item belonged to the processable item, false otherwise
     */
    boolean onReceiveOutput(ItemStack stack);

    /**
     * Writes the processable to NBT.
     *
     * @param tag the tag
     * @return the written tag
     */
    NBTTagCompound writeToNBT(NBTTagCompound tag);
}
