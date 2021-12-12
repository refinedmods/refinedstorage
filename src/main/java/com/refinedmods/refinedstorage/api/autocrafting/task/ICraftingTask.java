package com.refinedmods.refinedstorage.api.autocrafting.task;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.refinedmods.refinedstorage.api.network.INetwork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.UUID;

/**
 * Represents a crafting task.
 */
public interface ICraftingTask {
    /**
     * Updates this task.
     *
     * @return true if this crafting task is finished, false otherwise
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
     * @return the completion percentage
     */
    int getCompletionPercentage();

    /**
     * @return the stack requested
     */
    ICraftingRequestInfo getRequested();

    /**
     * Called when a stack is inserted into the system through {@link INetwork#insertItemTracked(ItemStack, int)}.
     *
     * @param stack the stack
     * @return the remainder of this stack after processing of the task
     */
    int onTrackedInsert(ItemStack stack, int size);

    /**
     * Called when a stack is inserted into the system through {@link INetwork#insertFluidTracked(FluidStack, int)}.
     *
     * @param stack the stack
     * @return the remainder of this stack after processing of the task
     */
    int onTrackedInsert(FluidStack stack, int size);

    /**
     * Writes this task to NBT.
     *
     * @param tag the tag
     * @return the written tag
     */
    CompoundTag writeToNbt(CompoundTag tag);

    /**
     * @return the elements of this task for display in the crafting monitor
     */
    List<ICraftingMonitorElement> getCraftingMonitorElements();

    /**
     * @return the crafting pattern corresponding to this task
     */
    ICraftingPattern getPattern();

    /**
     * @return the unix time in ms when this task has started
     */
    long getStartTime();

    /**
     * @return the id of this task
     */
    UUID getId();

    /**
     * Start the CraftingTask
     */
    void start();
}
