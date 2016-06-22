package refinedstorage.api.network;

import cofh.api.energy.EnergyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.ICraftingTask;
import refinedstorage.api.storage.CompareFlags;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

public interface INetworkMaster {
    /**
     * @return The world this network is in
     */
    World getWorld();

    /**
     * Initializes this network.
     * Starting from this call {@link INetworkMaster#update()} will be called every server tick.
     *
     * @param world The world this network is in
     */
    void setWorld(World world);

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
     * @return If this network is able to run (usually corresponds to redstone setting)
     */
    boolean canRun();

    /**
     * Updates this network.
     */
    void update();

    /**
     * @return A iterator with all network slaves
     */
    Iterator<INetworkSlave> getSlaves();

    /**
     * @param slave The slave to add
     */
    void addSlave(BlockPos slave);

    /**
     * @param slave The slave to remove
     */
    void removeSlave(BlockPos slave);

    /**
     * @return The grid handler for this network
     */
    IGridHandler getGridHandler();

    /**
     * @return The wireless grid handler for this network
     */
    IWirelessGridHandler getWirelessGridHandler();

    /**
     * @return The items stored in this network, do NOT modify this list
     */
    List<ItemStack> getItems();

    /**
     * @return The crafting tasks in this, do NOT modify this list
     */
    List<ICraftingTask> getCraftingTasks();

    /**
     * Adds a crafting task to the top of the crafting task stack.
     *
     * @param task The crafting task to add
     */
    void addCraftingTask(ICraftingTask task);

    /**
     * Adds a crafting task to the bottom of the crafting task stack.
     *
     * @param task The crafting task to add
     */
    void addCraftingTaskAsLast(ICraftingTask task);

    /**
     * Creates a crafting task from a pattern
     *
     * @param pattern The pattern to create a task for
     * @return The task
     */
    ICraftingTask createCraftingTask(ICraftingPattern pattern);

    /**
     * Cancels a crafting task.
     *
     * @param task The task to cancel
     */
    void cancelCraftingTask(ICraftingTask task);

    /**
     * @return A list of crafting patterns in this network
     */
    List<ICraftingPattern> getPatterns();

    /**
     * Returns crafting patterns from an item stack.
     *
     * @param pattern The item to get a pattern for
     * @param flags   The flags we compare on
     * @return A list of crafting patterns where the given pattern is one of the outputs
     */
    List<ICraftingPattern> getPattern(ItemStack pattern, int flags);

    /**
     * Returns a crafting pattern from an item stack.
     * If there are multiple crafting patterns with the same output, it'll return the one
     * where there are the most ingredients of in the network.
     *
     * @param pattern The item to get a pattern for
     * @return The pattern
     */
    ICraftingPattern getPatternWithBestScore(ItemStack pattern);

    /**
     * Returns a crafting pattern from an item stack.
     * If there are multiple crafting patterns with the same output, it'll return the one
     * where there are the most ingredients of in the network.
     *
     * @param pattern The item to get a pattern for
     * @param flags   The flags we compare on
     * @return The pattern
     */
    ICraftingPattern getPatternWithBestScore(ItemStack pattern, int flags);

    /**
     * Sends to all clients watching a network (for example a grid) a packet with all the items in this network.
     */
    void updateItemsWithClient();

    /**
     * Sends a player a packet with all the items in this network.
     */
    void updateItemsWithClient(EntityPlayerMP player);

    /**
     * Pushes an item to this network.
     *
     * @param stack    The stack prototype to push, do NOT modify
     * @param size     The amount of that prototype that has to be pushed
     * @param simulate If we are simulating
     * @return null if the push was successful, or a {@link ItemStack} with the remainder
     */
    ItemStack push(ItemStack stack, int size, boolean simulate);

    /**
     * Takes an item from storage.
     * If the stack we found in the system is smaller than the requested size, return the stack anyway.
     * For example: this method is called for dirt (64x) while there is only dirt (32x), return the dirt (32x) anyway.
     *
     * @param stack A prototype of the stack to take, do NOT modify
     * @param size  The amount of that prototype that has to be taken
     * @return null if we didn't take anything, or a {@link ItemStack} with the result
     */
    ItemStack take(ItemStack stack, int size);

    /**
     * Takes an item from storage.
     * If the stack we found in the system is smaller than the requested size, return the stack anyway.
     * For example: this method is called for dirt (64x) while there is only dirt (32x), return the dirt (32x) anyway.
     *
     * @param stack A prototype of the stack to take, do NOT modify
     * @param size  The amount of that prototype that has to be taken
     * @param flags On what we are comparing to take the item, see {@link CompareFlags}
     * @return null if we didn't take anything, or a {@link ItemStack} with the result
     */
    ItemStack take(ItemStack stack, int size, int flags);

    /**
     * Returns an item from storage, based on the given prototype.
     *
     * @param stack The stack to search
     * @param flags The flags to compare on
     * @return The stack we found
     */
    @Nullable
    ItemStack getItem(ItemStack stack, int flags);

    NBTTagCompound writeToNBT(NBTTagCompound tag);

    void readFromNBT(NBTTagCompound tag);
}
