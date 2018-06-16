package com.raoulvdberge.refinedstorage.api.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Represents a crafting task.
 */
public interface ICraftingTask {
    /**
     * Calculates what this task will do, but doesn't run the task yet.
     *
     * @return the error, or null if there was no error
     */
    @Nullable
    ICraftingTaskError calculate();

    /**
     * Updates this task.
     * {@link ICraftingTask#calculate()} must be run before this!
     *
     * @return true if this crafting task is finished and can be deleted from the list, false otherwise
     */
    boolean update();

    /**
     * Called when this task is cancelled.
     */
    void onCancelled();

    /**
     * @return the amount of items that have to be crafted
     */
    int getQuantity();

    /**
     * @return the stack requested
     */
    ItemStack getRequested();

    /**
     * Called when a stack is inserted into the system through {@link com.raoulvdberge.refinedstorage.api.network.INetwork#insertItemTracked(ItemStack, int)}.
     *
     * @param stack the stack
     * @return the size remaining, decremented by the crafting task when it was relevant to it
     */
    int onTrackedItemInserted(ItemStack stack, int size);

    /**
     * Writes this task to NBT.
     *
     * @param tag the tag
     * @return the written tag
     */
    NBTTagCompound writeToNBT(NBTTagCompound tag);

    /**
     * {@link ICraftingTask#calculate()} must be run before this!
     *
     * @return the elements of this task for display in the crafting monitor
     */
    List<ICraftingMonitorElement> getCraftingMonitorElements();

    /**
     * {@link ICraftingTask#calculate()} must be run before this!
     *
     * @return get a list of {@link ICraftingPreviewElement}s
     */
    List<ICraftingPreviewElement> getPreviewStacks();

    /**
     * @return the crafting pattern corresponding to this task
     */
    ICraftingPattern getPattern();

    /**
     * Used to check if the crafting task has recursive elements (eg. block needs 9 ingots, ingots are crafted by a block).
     * {@link ICraftingTask#calculate()} must be run before this!
     *
     * @return true if no recursion was found, false otherwise
     */
    boolean isValid();

    /**
     * @return the missing items
     */
    IStackList<ItemStack> getMissing();

    /**
     * @return the id of this task
     */
    UUID getId();
}
