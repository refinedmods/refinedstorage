package com.raoulvdberge.refinedstorage.api.autocrafting;

import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorListener;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    ICraftingTask getTask(UUID id);

    /**
     * @return named crafting pattern containers
     */
    Map<String, List<IItemHandlerModifiable>> getNamedContainers();

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
     * @return a new pattern chain list
     */
    ICraftingPatternChainList createPatternChainList();

    /**
     * Schedules a crafting task if the task isn't scheduled yet.
     *
     * @param stack      the stack
     * @param toSchedule the amount of tasks to schedule
     * @return the crafting task created, or null if no task is created
     */
    @Nullable
    ICraftingTask schedule(ItemStack stack, int toSchedule);

    /**
     * Tracks an incoming stack.
     *
     * @param stack the stack
     */
    void track(ItemStack stack, int size);

    /**
     * @return a list of crafting patterns in this network, do NOT modify this list
     */
    List<ICraftingPattern> getPatterns();

    /**
     * Rebuilds the pattern list.
     */
    void rebuild();

    /**
     * Return a crafting pattern from an item stack.
     *
     * @param pattern the stack to get a pattern for
     * @return the crafting pattern, or null if none is found
     */
    @Nullable
    ICraftingPattern getPattern(ItemStack pattern);

    /**
     * Updates the tasks in this manager.
     */
    void update();

    /**
     * @param tag the tag to read from
     */
    void readFromNbt(NBTTagCompound tag);

    /**
     * @param tag the tag to write to
     * @return the written tag
     */
    NBTTagCompound writeToNBT(NBTTagCompound tag);

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
}
