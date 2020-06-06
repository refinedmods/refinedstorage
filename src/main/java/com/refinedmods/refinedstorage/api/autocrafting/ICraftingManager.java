package com.refinedmods.refinedstorage.api.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorListener;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.CraftingTask;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
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
    Map<ITextComponent, List<IItemHandlerModifiable>> getNamedContainers();

    /**
     * Adds a crafting task.
     *
     * @param task the task to add
     */
    void add(@Nonnull ICraftingTask task);

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
     * @return the crafting task, or null if no pattern was found for the given stack
     */
    @Nullable
    ICraftingTask create(ItemStack stack, int quantity);

    /**
     * Creates a crafting task for a given stack, but doesn't add it to the list.
     *
     * @param stack    the stack to craft
     * @param quantity the quantity to craft
     * @return the crafting task, or null if no pattern was found for the given stack
     */
    @Nullable
    ICraftingTask create(FluidStack stack, int quantity);

    /**
     * @return a new pattern chain list
     */
    ICraftingPatternChainList createPatternChainList();

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
    void readFromNbt(CompoundNBT tag);

    /**
     * @param tag the tag to write to
     * @return the written tag
     */
    CompoundNBT writeToNbt(CompoundNBT tag);

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
     * @param pattern to look for
     * @return a LinkedHashSet with all container that have this pattern
     */

    Set<ICraftingPatternContainer> getAllContainer(ICraftingPattern pattern);

    /**
     * Reserve a Ingredients so that they may not be used in Autocrafting Calculation
     * @param task the CraftingTask that the Ingredients are reserved by
     * @param reserved Map of reserved UUID's corresponding to the UUID of Entries in the StorageCache
     */
    void reserveIngredients(CraftingTask task, Map<UUID,Integer> reserved);

    /**
     * Get all reserved Ingredients
     * @return Collection of all reserved Ingredients
     */

    Collection<Map<UUID,Integer>> getReservedIngredients();

    /**
     * Remove the reserved Ingredients for this CraftingTask
     * @param task CraftingTask that no longer reserves Items
     */
    void clearReservedIngredients(CraftingTask task);
}
