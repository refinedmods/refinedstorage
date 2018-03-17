package com.raoulvdberge.refinedstorage.api.autocrafting;

import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
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
     * @return all the crafting pattern containers
     */
    List<ICraftingPatternContainer> getContainers();

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

    /**
     * Creates a crafting task.
     *
     * @param stack     the stack to create a task for
     * @param pattern   the pattern
     * @param quantity  the quantity
     * @param automated whether this crafting task is created in an automated way
     * @return the crafting task
     */
    ICraftingTask create(@Nullable ItemStack stack, ICraftingPattern pattern, int quantity, boolean automated);

    /**
     * Creates a crafting task.
     *
     * @param stack        the stack to create a task for
     * @param patternChain the pattern
     * @param quantity     the quantity
     * @param automated    whether this crafting task is created in an automated way
     * @return the crafting task
     */
    ICraftingTask create(@Nullable ItemStack stack, ICraftingPatternChain patternChain, int quantity, boolean automated);

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
     * Returns crafting patterns from an item stack.
     *
     * @param pattern the stack to get a pattern for
     * @param flags   the flags to compare on, see {@link IComparer}
     * @return a list of crafting patterns where the given pattern is one of the outputs
     */
    List<ICraftingPattern> getPatterns(ItemStack pattern, int flags);

    /**
     * Returns a crafting pattern for an item stack.
     * This returns a single crafting pattern, as opposed to {@link ICraftingManager#getPatterns(ItemStack, int)}.
     * Internally, this makes a selection out of the available patterns.
     * It makes this selection based on the item count of the pattern outputs in the system.
     *
     * @param pattern the stack to get a pattern for
     * @param flags   the flags to compare on, see {@link IComparer}
     * @return the pattern, or null if the pattern is not found
     */
    @Nullable
    default ICraftingPattern getPattern(ItemStack pattern, int flags) {
        ICraftingPatternChain chain = getPatternChain(pattern, flags);

        return chain == null ? null : chain.cycle();
    }

    /**
     * Returns a crafting pattern for an item stack.
     * This returns a single crafting pattern, as opposed to {@link ICraftingManager#getPatterns(ItemStack, int)}.
     * Internally, this makes a selection out of the available patterns.
     * It makes this selection based on the item count of the pattern outputs in the {@link IStackList<ItemStack>} provided.
     *
     * @param pattern  the stack to get a pattern for
     * @param flags    the flags to compare on, see {@link IComparer}
     * @param itemList the {@link IStackList<ItemStack>} used to calculate the best fitting pattern
     * @return the pattern, or null if the pattern is not found
     */
    @Nullable
    default ICraftingPattern getPattern(ItemStack pattern, int flags, IStackList<ItemStack> itemList) {
        ICraftingPatternChain chain = getPatternChain(pattern, flags, itemList);

        return chain == null ? null : chain.cycle();
    }

    /**
     * Returns a crafting pattern for an item stack.
     * This returns a single crafting pattern, as opposed to {@link ICraftingManager#getPatterns(ItemStack, int)}.
     * Internally, this makes a selection out of the available patterns.
     * It makes this selection based on the item count of the pattern outputs in the system.
     *
     * @param pattern the stack to get a pattern for
     * @return the pattern, or null if the pattern is not found
     */
    @Nullable
    default ICraftingPattern getPattern(ItemStack pattern) {
        return getPattern(pattern, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT);
    }

    /**
     * Returns a crafting pattern chain for an item stack.
     * This returns a single crafting pattern, as opposed to {@link ICraftingManager#getPatterns(ItemStack, int)}.
     * Internally, this makes a selection out of the available patterns.
     * It makes this selection based on the item count of the pattern outputs in the system.
     *
     * @param pattern the stack to get a pattern for
     * @param flags   the flags to compare on, see {@link IComparer}
     * @return the pattern chain, or null if the pattern chain is not found
     */
    @Nullable
    ICraftingPatternChain getPatternChain(ItemStack pattern, int flags);

    /**
     * Returns a crafting pattern chain for an item stack.
     * This returns a single crafting pattern, as opposed to {@link ICraftingManager#getPatterns(ItemStack, int)}.
     * Internally, this makes a selection out of the available patterns.
     * It makes this selection based on the item count of the pattern outputs in the {@link IStackList<ItemStack>} provided.
     *
     * @param pattern  the stack to get a pattern for
     * @param flags    the flags to compare on, see {@link IComparer}
     * @param itemList the {@link IStackList<ItemStack>} used to calculate the best fitting pattern
     * @return the pattern chain, or null if the pattern chain is not found
     */
    @Nullable
    ICraftingPatternChain getPatternChain(ItemStack pattern, int flags, IStackList<ItemStack> itemList);

    /**
     * Returns a crafting pattern for an item stack.
     * This returns a single crafting pattern, as opposed to {@link ICraftingManager#getPatterns(ItemStack, int)}.
     * Internally, this makes a selection out of the available patterns.
     * It makes this selection based on the item count of the pattern outputs in the system.
     *
     * @param pattern the stack to get a pattern for
     * @return the pattern chain, or null if the pattern chain is not found
     */
    @Nullable
    default ICraftingPatternChain getPatternChain(ItemStack pattern) {
        return getPatternChain(pattern, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT);
    }

    /**
     * Returns if there is a pattern with a given stack as output.
     *
     * @param stack the stack
     * @return true if there is a pattern, false otherwise
     */
    default boolean hasPattern(ItemStack stack) {
        return hasPattern(stack, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT);
    }

    /**
     * Returns if there is a pattern with a given stack as output.
     *
     * @param stack the stack
     * @param flags the flags to compare on, see {@link IComparer}
     * @return true if there is a pattern, false otherwise
     */
    boolean hasPattern(ItemStack stack, int flags);

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
    void markCraftingMonitorForUpdate();

    /**
     * Sends a crafting monitor update to all players that are watching a crafting monitor.
     * <p>
     * WARNING: In most cases, you should just use {@link ICraftingManager#markCraftingMonitorForUpdate()}, if not, you can get high bandwidth usage.
     */
    void sendCraftingMonitorUpdate();

    /**
     * Sends a crafting monitor update to a specific player.
     *
     * @param player the player
     */
    void sendCraftingMonitorUpdate(EntityPlayerMP player);
}
