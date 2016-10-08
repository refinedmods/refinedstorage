package refinedstorage.api.network;

import cofh.api.energy.EnergyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.task.ICraftingTask;
import refinedstorage.api.network.grid.IFluidGridHandler;
import refinedstorage.api.network.grid.IItemGridHandler;
import refinedstorage.api.storage.fluid.IGroupedFluidStorage;
import refinedstorage.api.storage.item.IGroupedItemStorage;
import refinedstorage.api.util.IComparer;
import refinedstorage.apiimpl.API;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a network master, usually is a controller.
 */
public interface INetworkMaster {
    /**
     * @return the energy storage of this network
     */
    EnergyStorage getEnergy();

    /**
     * @return the energy usage per tick of this network
     */
    int getEnergyUsage();

    /**
     * @return the position of this network in the world
     */
    BlockPos getPosition();

    /**
     * @return if this network is able to run (usually corresponds to the redstone configuration)
     */
    boolean canRun();

    /**
     * @return a graph of connected nodes to this network
     */
    INetworkNodeGraph getNodeGraph();

    /**
     * @return the {@link IItemGridHandler} of this network
     */
    IItemGridHandler getItemGridHandler();

    /**
     * @return the {@link IFluidGridHandler} of this network
     */
    IFluidGridHandler getFluidGridHandler();

    /**
     * @return the {@link IWirelessGridHandler} of this network
     */
    IWirelessGridHandler getWirelessGridHandler();

    /**
     * @return the {@link IGroupedItemStorage} of this network
     */
    IGroupedItemStorage getItemStorage();

    /**
     * @return the {@link IGroupedFluidStorage} of this network
     */
    IGroupedFluidStorage getFluidStorage();

    /**
     * @return the crafting tasks in this network, do NOT modify this list
     */
    List<ICraftingTask> getCraftingTasks();

    /**
     * Adds a crafting task.
     *
     * @param task the task to add
     */
    void addCraftingTask(@Nonnull ICraftingTask task);

    /**
     * Cancels a crafting task.
     *
     * @param task the task to cancel
     */
    void cancelCraftingTask(@Nonnull ICraftingTask task);

    /**
     * @return a list of crafting patterns in this network, do NOT modify this list
     */
    List<ICraftingPattern> getPatterns();

    /**
     * Rebuilds the pattern list.
     */
    void rebuildPatterns();

    /**
     * Returns crafting patterns from an item stack.
     *
     * @param pattern the stack to get a pattern for
     * @param flags   the flags to compare on, see {@link CompareUtils}
     * @return a list of crafting patterns where the given pattern is one of the outputs
     */
    List<ICraftingPattern> getPatterns(ItemStack pattern, int flags);

    /**
     * Returns a crafting pattern for an item stack.
     * This returns a single crafting pattern, as opposed to {@link INetworkMaster#getPatterns(ItemStack, int)}.
     * Internally, this makes a selection out of the available patterns.
     * It makes this selection based on the item count of the pattern outputs in the system.
     *
     * @param pattern the stack to get a pattern for
     * @param flags   the flags to compare on, see {@link CompareUtils}
     * @return the pattern, or null if the pattern is not found
     */
    @Nullable
    ICraftingPattern getPattern(ItemStack pattern, int flags);

    /**
     * Returns a crafting pattern for an item stack.
     * This returns a single crafting pattern, as opposed to {@link INetworkMaster#getPatterns(ItemStack, int)}.
     * Internally, this makes a selection out of the available patterns.
     * It makes this selection based on the item count of the pattern outputs in the system.
     *
     * @param pattern the stack to get a pattern for
     * @return the pattern, or null if the pattern is not found
     */
    default ICraftingPattern getPattern(ItemStack pattern) {
        return getPattern(pattern, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT);
    }

    /**
     * Returns if there is a pattern with a given stack as output.
     *
     * @param stack the stack
     * @return true if there is a pattern, false otherwise
     */
    default boolean hasPattern(ItemStack stack) {
        return getPattern(stack) != null;
    }

    /**
     * Creates a crafting task.
     *
     * @param stack    the stack to create a task for
     * @param pattern  the pattern
     * @param quantity the quantity
     * @return the crafting task
     */
    default ICraftingTask createCraftingTask(@Nullable ItemStack stack, ICraftingPattern pattern, int quantity) {
        return API.instance().getCraftingTaskRegistry().getFactory(pattern.getId()).create(getNetworkWorld(), this, stack, pattern, quantity, null);
    }

    /**
     * Schedules a crafting task if the task isn't scheduled yet.
     *
     * @param stack      the stack
     * @param toSchedule the amount of tasks to schedule
     * @param compare    the compare value to find patterns
     */
    default void scheduleCraftingTaskIfUnscheduled(ItemStack stack, int toSchedule, int compare) {
        int alreadyScheduled = 0;

        for (ICraftingTask task : getCraftingTasks()) {
            for (ItemStack output : task.getPattern().getOutputs()) {
                if (API.instance().getComparer().isEqual(output, stack, compare)) {
                    alreadyScheduled++;
                }
            }
        }

        for (int i = 0; i < toSchedule - alreadyScheduled; ++i) {
            ICraftingPattern pattern = getPattern(stack, compare);

            if (pattern != null) {
                addCraftingTask(createCraftingTask(stack, pattern, 1));
            }
        }
    }

    /**
     * Sends a grid update packet with all the items to all clients that are watching a grid connected to this network.
     */
    void sendItemStorageToClient();

    /**
     * Sends a grid update packet with all the items to a specific player.
     */
    void sendItemStorageToClient(EntityPlayerMP player);

    /**
     * Sends a item storage change to all clients that are watching a grid connected to this network.
     *
     * @param stack the stack
     * @param delta the delta
     */
    void sendItemStorageDeltaToClient(ItemStack stack, int delta);

    /**
     * Sends a grid update packet with all the fluids to all clients that are watching a grid connected to this network.
     */
    void sendFluidStorageToClient();

    /**
     * Sends a grid packet with all the fluids to a specific player.
     */
    void sendFluidStorageToClient(EntityPlayerMP player);

    /**
     * Sends a fluids storage change to all clients that are watching a grid connected to this network.
     *
     * @param stack the stack
     * @param delta the delta
     */
    void sendFluidStorageDeltaToClient(FluidStack stack, int delta);

    /**
     * Inserts an item in this network.
     *
     * @param stack    the stack prototype to insert, do NOT modify
     * @param size     the amount of that prototype that has to be inserted
     * @param simulate true if we are simulating, false otherwise
     * @return null if the insert was successful, or a stack with the remainder
     */
    @Nullable
    ItemStack insertItem(@Nonnull ItemStack stack, int size, boolean simulate);

    /**
     * Extracts an item from this network.
     *
     * @param stack the prototype of the stack to extract, do NOT modify
     * @param size  the amount of that prototype that has to be extracted
     * @param flags the flags to compare on, see {@link CompareUtils}
     * @return null if we didn't extract anything, or a stack with the result
     */
    @Nullable
    ItemStack extractItem(@Nonnull ItemStack stack, int size, int flags);

    /**
     * Extracts an item from this network.
     *
     * @param stack the prototype of the stack to extract, do NOT modify
     * @param size  the amount of that prototype that has to be extracted
     * @return null if we didn't extract anything, or a stack with the result
     */
    default ItemStack extractItem(ItemStack stack, int size) {
        return extractItem(stack, size, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT);
    }

    /**
     * Inserts a fluid in this network.
     *
     * @param stack    the stack prototype to insert, do NOT modify
     * @param size     the amount of that prototype that has to be inserted
     * @param simulate true if we are simulating, false otherwise
     * @return null if the insert was successful, or a stack with the remainder
     */
    @Nullable
    FluidStack insertFluid(@Nonnull FluidStack stack, int size, boolean simulate);

    /**
     * Extracts a fluid from this network.
     *
     * @param stack the prototype of the stack to extract, do NOT modify
     * @param size  the amount of that prototype that has to be extracted
     * @param flags the flags to compare on, see {@link CompareUtils}
     * @return null if we didn't extract anything, or a stack with the result
     */
    @Nullable
    FluidStack extractFluid(@Nonnull FluidStack stack, int size, int flags);

    /**
     * Extracts a fluid from this network.
     *
     * @param stack the prototype of the stack to extract, do NOT modify
     * @param size  the amount of that prototype that has to be extracted
     * @return null if we didn't extract anything, or a stack with the result
     */
    default FluidStack extractFluid(FluidStack stack, int size) {
        return extractFluid(stack, size, IComparer.COMPARE_NBT);
    }

    /**
     * @return the world where this network is in
     */
    World getNetworkWorld();
}
