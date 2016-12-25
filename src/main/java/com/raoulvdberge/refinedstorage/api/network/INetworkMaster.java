package com.raoulvdberge.refinedstorage.api.network;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingManager;
import com.raoulvdberge.refinedstorage.api.network.grid.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.network.grid.IItemGridHandler;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemHandler;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.security.ISecurityManager;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCache;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a network master, usually is a controller.
 */
public interface INetworkMaster {
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
     * @return the {@link ISecurityManager} of this network
     */
    ISecurityManager getSecurityManager();

    /**
     * @return the {@link ICraftingManager} of this network
     */
    ICraftingManager getCraftingManager();

    /**
     * @return the {@link IItemGridHandler} of this network
     */
    IItemGridHandler getItemGridHandler();

    /**
     * @return the {@link IFluidGridHandler} of this network
     */
    IFluidGridHandler getFluidGridHandler();

    /**
     * @return the {@link INetworkItemHandler} of this network
     */
    INetworkItemHandler getNetworkItemHandler();

    /**
     * @return the {@link IStorageCache<ItemStack>} of this network
     */
    IStorageCache<ItemStack> getItemStorageCache();

    /**
     * @return the {@link IStorageCache<FluidStack>} of this network
     */
    IStorageCache<FluidStack> getFluidStorageCache();

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
     * Makes the network send a crafting monitor update to all players as soon as it can.
     */
    void markCraftingMonitorForUpdate();

    /**
     * Sends a crafting monitor update to all players that are watching a crafting monitor.
     * <p>
     * WARNING: In most cases, you should just use {@link INetworkMaster#markCraftingMonitorForUpdate()}, if not,
     * you can get high bandwidth usage.
     */
    void sendCraftingMonitorUpdate();

    /**
     * Sends a crafting monitor update to a specific player.
     *
     * @param player the player
     */
    void sendCraftingMonitorUpdate(EntityPlayerMP player);

    /**
     * @param name the name of the reader writer channel
     * @return the reader writer channel, or null if nothing was found
     */
    @Nullable
    IReaderWriterChannel getReaderWriterChannel(String name);

    /**
     * Adds a new reader writer channel.
     *
     * @param name the name of this channel
     */
    void addReaderWriterChannel(String name);

    /**
     * Removes a reader writer channel.
     *
     * @param name the name of the channel to remove
     */
    void removeReaderWriterChannel(String name);

    /**
     * Sends a reader writer channel update to all players watching a reader or writer.
     */
    void sendReaderWriterChannelUpdate();

    /**
     * Sends a reader writer channel update to a specific player.
     *
     * @param player the player to send to
     */
    void sendReaderWriterChannelUpdate(EntityPlayerMP player);

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

    default ItemStack insertItemTracked(@Nonnull ItemStack stack, int size) {
        ItemStack remainder = insertItem(stack, size, false);

        int inserted = remainder == null ? size : (size - remainder.getCount());

        getCraftingManager().track(stack, inserted);

        return remainder;
    }

    /**
     * Extracts an item from this network.
     *
     * @param stack    the prototype of the stack to extract, do NOT modify
     * @param size     the amount of that prototype that has to be extracted
     * @param flags    the flags to compare on, see {@link IComparer}
     * @param simulate true if we are simulating, false otherwise
     * @return null if we didn't extract anything, or a stack with the result
     */
    @Nullable
    ItemStack extractItem(@Nonnull ItemStack stack, int size, int flags, boolean simulate);

    /**
     * Extracts an item from this network.
     *
     * @param stack    the prototype of the stack to extract, do NOT modify
     * @param size     the amount of that prototype that has to be extracted
     * @param simulate true if we are simulating, false otherwise
     * @return null if we didn't extract anything, or a stack with the result
     */
    default ItemStack extractItem(@Nonnull ItemStack stack, int size, boolean simulate) {
        return extractItem(stack, size, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT, simulate);
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
     * @param stack    the prototype of the stack to extract, do NOT modify
     * @param size     the amount of that prototype that has to be extracted
     * @param flags    the flags to compare on, see {@link IComparer}
     * @param simulate true if we are simulating, false otherwise
     * @return null if we didn't extract anything, or a stack with the result
     */
    @Nullable
    FluidStack extractFluid(@Nonnull FluidStack stack, int size, int flags, boolean simulate);

    /**
     * Extracts a fluid from this network.
     *
     * @param stack    the prototype of the stack to extract, do NOT modify
     * @param size     the amount of that prototype that has to be extracted
     * @param simulate true if we are simulating, false otherwise
     * @return null if we didn't extract anything, or a stack with the result
     */
    default FluidStack extractFluid(FluidStack stack, int size, boolean simulate) {
        return extractFluid(stack, size, IComparer.COMPARE_NBT, simulate);
    }

    /**
     * @return the world where this network is in
     */
    World getNetworkWorld();
}
