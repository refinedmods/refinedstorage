package refinedstorage.api.network;

import cofh.api.energy.EnergyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.ICraftingTask;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.api.storage.IGroupedStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a network master, usually is a controller.
 */
public interface INetworkMaster {
    /**
     * @return The energy storage of this network
     */
    EnergyStorage getEnergy();

    /**
     * @return The energy usage per tick of this network
     */
    int getEnergyUsage();

    /**
     * @return The position of this network in the world
     */
    BlockPos getPosition();

    /**
     * @return If this network is able to run (usually corresponds to the redstone setting)
     */
    boolean canRun();

    /**
     * @return A graph of connected nodes to this network
     */
    INetworkNodeGraph getNodeGraph();

    /**
     * @return The {@link IGridHandler} for this network
     */
    IGridHandler getGridHandler();

    /**
     * @return The {@link IWirelessGridHandler} for this network
     */
    IWirelessGridHandler getWirelessGridHandler();

    /**
     * @return The {@link IGroupedStorage} of this network
     */
    IGroupedStorage getStorage();

    /**
     * @return The crafting tasks in this network, do NOT modify this list
     */
    List<ICraftingTask> getCraftingTasks();

    /**
     * Adds a crafting task to the top of the crafting task stack.
     *
     * @param task The crafting task to add
     */
    void addCraftingTask(@Nonnull ICraftingTask task);

    /**
     * Adds a crafting task to the bottom of the crafting task stack.
     *
     * @param task The crafting task to add as last
     */
    void addCraftingTaskAsLast(@Nonnull ICraftingTask task);

    /**
     * Creates a crafting task from a {@link ICraftingPattern}.
     *
     * @param pattern The pattern to create a task for
     * @return A task
     */
    ICraftingTask createCraftingTask(@Nonnull ICraftingPattern pattern);

    /**
     * Cancels a crafting task.
     *
     * @param task The task to cancel
     */
    void cancelCraftingTask(@Nonnull ICraftingTask task);

    /**
     * @return A list of crafting patterns in this network, do NOT modify this list
     */
    List<ICraftingPattern> getPatterns();

    /**
     * Rebuilds the pattern list.
     */
    void rebuildPatterns();

    /**
     * Returns crafting patterns from an item stack.
     *
     * @param pattern The {@link ItemStack} to get a pattern for
     * @param flags   The flags to compare on, see {@link CompareUtils}
     * @return A list of crafting patterns where the given pattern is one of the outputs
     */
    List<ICraftingPattern> getPatterns(ItemStack pattern, int flags);

    /**
     * @param pattern The {@link ItemStack} to get a pattern for
     * @param flags   The flags to compare on, see {@link CompareUtils}
     * @return The pattern, or null if the pattern is not found
     */
    @Nullable
    ICraftingPattern getPattern(ItemStack pattern, int flags);

    /**
     * Sends a grid packet with all the items to all clients that are watching a grid.
     */
    void sendStorageToClient();

    /**
     * Sends a grid packet with all the items to a specific player.
     */
    void sendStorageToClient(EntityPlayerMP player);

    /**
     * Sends a storage change to all clients that are watching a grid.
     *
     * @param stack The stack
     * @param delta The delta
     */
    void sendStorageDeltaToClient(ItemStack stack, int delta);

    /**
     * Inserts an item to this network.
     *
     * @param stack    The stack prototype to insert, do NOT modify
     * @param size     The amount of that prototype that has to be inserted
     * @param simulate If we are simulating
     * @return null if the insert was successful, or an {@link ItemStack} with the remainder
     */
    @Nullable
    ItemStack insertItem(@Nonnull ItemStack stack, int size, boolean simulate);

    /**
     * Extracts an item from this network.
     *
     * @param stack The prototype of the stack to extract, do NOT modify
     * @param size  The amount of that prototype that has to be extracted
     * @param flags The flags to compare on, see {@link CompareUtils}
     * @return null if we didn't extract anything, or a {@link ItemStack} with the result
     */
    @Nullable
    ItemStack extractItem(@Nonnull ItemStack stack, int size, int flags);
}
