package com.refinedmods.refinedstorage.api.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorListener;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICalculationResult;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * The crafting manager handles the storing, updating, adding and deleting of crafting tasks in a network.
 */
public interface ICraftingManager {
    /**
     * @return the crafting tasks in this network, do NOT modify this
     */
    Collection<ICraftingTask> getTasks();

    /**
     * Returns a crafting task by id.
     *
     * @param id the id
     * @return the task, or null if no task was found for the given id
     */
    @Nullable
    ICraftingTask getTask(UUID id);

    /**
     * @return named crafting pattern containers
     */
    Map<Component, List<IItemHandlerModifiable>> getNamedContainers();

    /**
     * Starts a crafting task.
     *
     * @param task the task to start
     */
    void start(@Nonnull ICraftingTask task);

    /**
     * Cancels a crafting task.
     *
     * @param id the id of the task to cancel, or null to cancel all
     */
    void cancel(@Nullable UUID id);

    /**
     * Creates a crafting task for a given stack, but doesn't add it to the list.
     *
     * @param stack    the stack to craft
     * @param quantity the quantity to craft
     * @return the calculation result
     */
    ICalculationResult create(ItemStack stack, int quantity);

    /**
     * Creates a crafting task for a given stack, but doesn't add it to the list.
     *
     * @param stack    the stack to craft
     * @param quantity the quantity to craft
     * @return the calculation result
     */
    ICalculationResult create(FluidStack stack, int quantity);

    /**
     * Schedules a crafting task if the task isn't scheduled yet.
     *
     * @param source the source
     * @param stack  the stack
     * @param amount the amount of items to request
     * @return the crafting task created, or null if no task is created
     */
    @Nullable
    ICraftingTask request(Object source, ItemStack stack, int amount);

    /**
     * Schedules a crafting task if the task isn't scheduled yet.
     *
     * @param source the source
     * @param stack  the stack
     * @param amount the mB of the fluid to request
     * @return the crafting task created, or null if no task is created
     */
    @Nullable
    ICraftingTask request(Object source, FluidStack stack, int amount);

    /**
     * Tracks an incoming stack.
     *
     * @param stack the stack, can be empty
     */
    int track(@Nonnull ItemStack stack, int size);

    /**
     * Tracks an incoming stack.
     *
     * @param stack the stack, can be empty
     */
    int track(@Nonnull FluidStack stack, int size);

    /**
     * @return the crafting patterns in this network
     */
    List<ICraftingPattern> getPatterns();

    /**
     * Rebuilds the pattern list.
     */
    void invalidate();

    /**
     * Return a crafting pattern from an item stack.
     *
     * @param pattern the stack to get a pattern for
     * @return the crafting pattern, or null if none is found
     */
    @Nullable
    ICraftingPattern getPattern(ItemStack pattern);

    /**
     * Return a crafting pattern from a fluid stack.
     *
     * @param pattern the stack to get a pattern for
     * @return the crafting pattern, or null if none is found
     */
    @Nullable
    ICraftingPattern getPattern(FluidStack pattern);

    /**
     * Updates the tasks in this manager.
     */
    void update();

    /**
     * @param tag the tag to read from
     */
    void readFromNbt(CompoundTag tag);

    /**
     * @param tag the tag to write to
     * @return the written tag
     */
    CompoundTag writeToNbt(CompoundTag tag);

    /**
     * @param listener the listener
     */
    void addListener(ICraftingMonitorListener listener);

    /**
     * @param listener the listener
     */
    void removeListener(ICraftingMonitorListener listener);

    /**
     * Calls all {@link ICraftingMonitorListener}s.
     */
    void onTaskChanged();

    /**
     * @param pattern pattern to look for
     * @return a set with all containers that have this pattern
     */
    Set<ICraftingPatternContainer> getAllContainers(ICraftingPattern pattern);
}
