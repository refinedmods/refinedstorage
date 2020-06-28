package com.refinedmods.refinedstorage.api.autocrafting.task;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.util.IStackList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;

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
     */
    int onTrackedInsert(ItemStack stack, int size);

    /**
     * Called when a stack is inserted into the system through {@link INetwork#insertFluidTracked(FluidStack, int)}.
     *
     * @param stack the stack
     */
    int onTrackedInsert(FluidStack stack, int size);

    /**
     * Writes this task to NBT.
     *
     * @param tag the tag
     * @return the written tag
     */
    CompoundNBT writeToNbt(CompoundNBT tag);

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
    List<ICraftingPreviewElement<?>> getPreviewStacks();

    /**
     * @return the crafting pattern corresponding to this task
     */
    ICraftingPattern getPattern();

    /**
     * @return the time in ms when this task has started
     */
    long getExecutionStarted();

    /**
     * @return the missing items
     */
    IStackList<ItemStack> getMissing();

    /**
     * @return the missing fluids
     */
    IStackList<FluidStack> getMissingFluids();

    /**
     * @return true if any items or fluids are missing, false otherwise
     */
    default boolean hasMissing() {
        return !getMissing().isEmpty() || !getMissingFluids().isEmpty();
    }

    /**
     * @return the id of this task
     */
    UUID getId();

    /**
     * Adds a output hook.
     *
     * @param outputHook the hook
     */
    void addOutputHook(ICraftingTaskOutputHook outputHook);
}
