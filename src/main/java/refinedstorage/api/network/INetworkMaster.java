package refinedstorage.api.network;

import cofh.api.energy.EnergyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.ICraftingTask;
import refinedstorage.api.storage.CompareFlags;
import refinedstorage.api.storage.IGroupedStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface INetworkMaster {
    /**
     * @return The energy storage of this network
     */
    EnergyStorage getEnergy();

    /**
     * @return The energy usage of this network
     */
    int getEnergyUsage();

    /**
     * @return The position of this network in the world (usually where the controller is)
     */
    BlockPos getPosition();

    /**
     * @return If this network is able to run (usually corresponds to the redstone setting)
     */
    boolean canRun();

    /**
     * @return A list with all network slaves
     */
    List<INetworkSlave> getSlaves();

    /**
     * @param slave The slave to add
     */
    void addSlave(@Nonnull INetworkSlave slave);

    /**
     * @param slave The slave to remove
     */
    void removeSlave(@Nonnull INetworkSlave slave);

    /**
     * @return The grid handler for this network
     */
    IGridHandler getGridHandler();

    /**
     * @return The wireless grid handler for this network
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
     * Creates a crafting task from a pattern.
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
     * Returns crafting patterns from an item stack.
     *
     * @param pattern The item to get a pattern for
     * @param flags   The flags to compare on, see {@link CompareFlags}
     * @return A list of crafting patterns where the given pattern is one of the outputs
     */
    List<ICraftingPattern> getPatterns(ItemStack pattern, int flags);

    /**
     * @param pattern The {@link ItemStack} to get a pattern for
     * @param flags   The flags to compare on, see {@link CompareFlags}
     * @return The pattern, or null if the pattern is not found
     */
    @Nullable
    ICraftingPattern getPattern(ItemStack pattern, int flags);

    /**
     * Sends to all clients in a grid a packet with all the items in this network.
     */
    void sendStorageToClient();

    /**
     * Sends a player a packet with all the items in this network.
     */
    void sendStorageToClient(EntityPlayerMP player);

    /**
     * Pushes an item to this network.
     *
     * @param stack    The stack prototype to push, do NOT modify
     * @param size     The amount of that prototype that has to be pushed
     * @param simulate If we are simulating
     * @return null if the push was successful, or a {@link ItemStack} with the remainder
     */
    @Nullable
    ItemStack push(@Nonnull ItemStack stack, int size, boolean simulate);

    /**
     * Takes an item from this network.
     *
     * @param stack A prototype of the stack to take, do NOT modify
     * @param size  The amount of that prototype that has to be taken
     * @param flags The flags to compare on, see {@link CompareFlags}
     * @return null if we didn't takeFromNetwork anything, or a {@link ItemStack} with the result
     */
    @Nullable
    ItemStack take(@Nonnull ItemStack stack, int size, int flags);
}
