package com.raoulvdberge.refinedstorage.api.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.raoulvdberge.refinedstorage.api.util.IStackList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Represents a crafting task.
 */
public interface ICraftingTask {
    String NBT_QUANTITY = "Quantity";
    String NBT_PATTERN_ID = "PatternID";
    String NBT_PATTERN_STACK = "PatternStack";
    String NBT_PATTERN_CONTAINER = "PatternContainer";
    String NBT_REQUESTED = "Requested";
    String NBT_AUTOMATED = "Automated";

    /**
     * Calculates what this task will do, but doesn't run the task yet.
     */
    void calculate();

    /**
     * Updates this task. Gets called every few ticks, depending on the speed of the pattern container.
     * {@link ICraftingTask#calculate()} must be run before this!
     *
     * @param usedContainers a map keeping track of used containers and how many times
     * @return true if this crafting task is finished and can be deleted from the list, false otherwise
     */
    boolean update(Map<ICraftingPatternContainer, Integer> usedContainers);

    /**
     * Called when this task is cancelled.
     */
    void onCancelled();

    /**
     * Reschedule the task. This does a recalculation and restart of the task.
     */
    void reschedule();

    /**
     * @return the amount of items that have to be crafted
     */
    int getQuantity();

    /**
     * @return the stack requested, or null if no specific stack is associated with this task
     */
    @Nullable
    ItemStack getRequested();

    /**
     * Writes this task to NBT.
     *
     * @param tag the tag
     * @return the written tag
     */
    NBTTagCompound writeToNBT(NBTTagCompound tag);

    /**
     * Helper method to write default necessary elements to NBT.
     *
     * @param tag the tag
     * @return the written tag
     */
    default NBTTagCompound writeDefaultsToNBT(NBTTagCompound tag) {
        tag.setInteger(NBT_QUANTITY, getQuantity());
        tag.setString(NBT_PATTERN_ID, getPattern().getId());
        tag.setTag(NBT_PATTERN_STACK, getPattern().getStack().serializeNBT());
        tag.setLong(NBT_PATTERN_CONTAINER, getPattern().getContainer().getPosition().toLong());
        tag.setBoolean(NBT_AUTOMATED, isAutomated());

        if (getRequested() != null) {
            tag.setTag(NBT_REQUESTED, getRequested().serializeNBT());
        }

        return tag;
    }

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
     * {@link ICraftingTask#calculate()} must be run before this!
     *
     * @return the steps for this task
     */
    List<ICraftingStep> getSteps();

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
     * @return true if the task is finished, false otherwise
     */
    boolean isFinished();

    /**
     * @return the missing items
     */
    IStackList<ItemStack> getMissing();

    /**
     * Returns whether the crafting task is created in an automated way.
     * For example: through the Crafting Upgrade or the "Trigger task with redstone signal" option in the crafter.
     *
     * @return true if this crafting task is created in an automated way, false otherwise
     */
    boolean isAutomated();
}
