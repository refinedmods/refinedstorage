package com.raoulvdberge.refinedstorage.api.autocrafting;

import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * The crafting manager handles the storing, updating, adding and deleting of crafting tasks in a network.
 */
public interface ICraftingManager {
    /**
     * @return the crafting tasks in this network, do NOT modify this list
     */
    List<ICraftingTask> getTasks();

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
     * @param task the task to cancel
     */
    void cancel(@Nonnull ICraftingTask task);

    @Nullable
    ICraftingTask create(ItemStack stack, int quantity);

    /**
     * Schedules a crafting task if the task isn't scheduled yet.
     *
     * @param stack      the stack
     * @param toSchedule the amount of tasks to schedule
     * @param compare    the compare value to find patterns, see {@link IComparer}
     * @return the crafting task created, or null if no task is created
     */
    @Nullable
    ICraftingTask schedule(ItemStack stack, int toSchedule, int compare);

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
     * @param flags   the flags to compare on, see {@link IComparer}
     * @return the crafting pattern, or null if none is found
     */
    @Nullable
    ICraftingPattern getPattern(ItemStack pattern, int flags);

    /**
     * Updates the tasks in this manager.
     */
    void update();

    /**
     * @param tag the tag to read from
     */
    void readFromNBT(NBTTagCompound tag);

    /**
     * @param tag the tag to write to
     * @return the written tag
     */
    NBTTagCompound writeToNBT(NBTTagCompound tag);

    /**
     * Makes the network send a crafting monitor update to all players as soon as it can.
     */
    // TODO: rework system to be subscribed-based, per task
    void markCraftingMonitorForUpdate();

    /**
     * Sends a crafting monitor update to all players that are watching a crafting monitor.
     * <p>
     * WARNING: In most cases, you should just use {@link ICraftingManager#markCraftingMonitorForUpdate()}, if not, you can get high bandwidth usage.
     */
    // TODO: rework system to be subscribed-based, per task
    void sendCraftingMonitorUpdate();

    /**
     * Sends a crafting monitor update to a specific player.
     *
     * @param player the player
     */
    // TODO: rework system to be subscribed-based, per task
    void sendCraftingMonitorUpdate(EntityPlayerMP player);
}
