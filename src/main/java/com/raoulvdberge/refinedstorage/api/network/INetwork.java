package com.raoulvdberge.refinedstorage.api.network;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingManager;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemHandler;
import com.raoulvdberge.refinedstorage.api.network.security.ISecurityManager;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.cache.IStorageCache;
import com.raoulvdberge.refinedstorage.api.storage.tracker.IStorageTracker;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

/**
 * Represents a network, usually is a controller.
 */
public interface INetwork {
    /**
     * @return the energy usage per tick of this network
     */
    int getEnergyUsage();

    /**
     * @return the position of this network in the world
     */
    BlockPos getPosition();

    /**
     * @return true if this network is able to run (usually corresponds to the redstone configuration), false otherwise
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
     * Inserts an item in this network.
     *
     * @param stack  the stack prototype to insert, can be empty, do NOT modify
     * @param size   the amount of that prototype that has to be inserted
     * @param action the action
     * @return an empty stack if the insert was successful, or a stack with the remainder
     */
    @Nonnull
    ItemStack insertItem(@Nonnull ItemStack stack, int size, Action action);

    /**
     * Inserts an item and notifies the crafting manager of the incoming item.
     *
     * @param stack the stack prototype to insert, can be empty, do NOT modify
     * @param size  the amount of that prototype that has to be inserted
     * @return an empty stack if the insert was successful, or a stack with the remainder
     */
    @Nonnull
    default ItemStack insertItemTracked(@Nonnull ItemStack stack, int size) {
        int remainder = getCraftingManager().track(stack, size);

        if (remainder == 0) {
            return ItemStack.EMPTY;
        }

        return insertItem(stack, remainder, Action.PERFORM);
    }

    /**
     * Extracts an item from this network.
     *
     * @param stack  the prototype of the stack to extract, do NOT modify
     * @param size   the amount of that prototype that has to be extracted
     * @param flags  the flags to compare on, see {@link IComparer}
     * @param action the action
     * @param filter a filter for the storage
     * @return an empty stack if nothing was extracted, or a stack with the result
     */
    @Nonnull
    ItemStack extractItem(@Nonnull ItemStack stack, int size, int flags, Action action, Predicate<IStorage<ItemStack>> filter);

    /**
     * Extracts an item from this network.
     *
     * @param stack  the prototype of the stack to extract, do NOT modify
     * @param size   the amount of that prototype that has to be extracted
     * @param flags  the flags to compare on, see {@link IComparer}
     * @param action the action
     * @return an empty stack if nothing was extracted, or a stack with the result
     */
    @Nonnull
    default ItemStack extractItem(@Nonnull ItemStack stack, int size, int flags, Action action) {
        return extractItem(stack, size, flags, action, s -> true);
    }

    /**
     * Extracts an item from this network.
     *
     * @param stack  the prototype of the stack to extract, do NOT modify
     * @param size   the amount of that prototype that has to be extracted
     * @param action the action
     * @return an empty stack if nothing was extracted, or a stack with the result
     */
    @Nonnull
    default ItemStack extractItem(@Nonnull ItemStack stack, int size, Action action) {
        return extractItem(stack, size, IComparer.COMPARE_NBT, action);
    }

    /**
     * Inserts a fluid in this network.
     *
     * @param stack  the stack prototype to insert, do NOT modify
     * @param size   the amount of that prototype that has to be inserted
     * @param action the action
     * @return an empty stack if the insert was successful, or a stack with the remainder
     */
    @Nonnull
    FluidStack insertFluid(@Nonnull FluidStack stack, int size, Action action);

    /**
     * Inserts a fluid and notifies the crafting manager of the incoming fluid.
     *
     * @param stack the stack prototype to insert, do NOT modify
     * @param size  the amount of that prototype that has to be inserted
     * @return an empty stack if the insert was successful, or a stack with the remainder
     */
    @Nonnull
    default FluidStack insertFluidTracked(@Nonnull FluidStack stack, int size) {
        int remainder = getCraftingManager().track(stack, size);

        if (remainder == 0) {
            return FluidStack.EMPTY;
        }

        return insertFluid(stack, remainder, Action.PERFORM);
    }

    /**
     * Extracts a fluid from this network.
     *
     * @param stack  the prototype of the stack to extract, do NOT modify
     * @param size   the amount of that prototype that has to be extracted
     * @param flags  the flags to compare on, see {@link IComparer}
     * @param action the action
     * @return an empty stack if nothing was extracted, or a stack with the result
     */
    @Nonnull
    FluidStack extractFluid(@Nonnull FluidStack stack, int size, int flags, Action action, Predicate<IStorage<FluidStack>> filter);

    /**
     * Extracts a fluid from this network.
     *
     * @param stack  the prototype of the stack to extract, do NOT modify
     * @param size   the amount of that prototype that has to be extracted
     * @param flags  the flags to compare on, see {@link IComparer}
     * @param action the action
     * @return an empty stack if nothing was extracted, or a stack with the result
     */
    @Nonnull
    default FluidStack extractFluid(FluidStack stack, int size, int flags, Action action) {
        return extractFluid(stack, size, flags, action, s -> true);
    }

    /**
     * Extracts a fluid from this network.
     *
     * @param stack  the prototype of the stack to extract, do NOT modify
     * @param size   the amount of that prototype that has to be extracted
     * @param action the action
     * @return an empty stack if nothing was extracted, or a stack with the result
     */
    @Nonnull
    default FluidStack extractFluid(FluidStack stack, int size, Action action) {
        return extractFluid(stack, size, IComparer.COMPARE_NBT, action);
    }

    /**
     * @return the storage tracker for items
     */
    IStorageTracker<ItemStack> getItemStorageTracker();

    /**
     * @return the storage tracker for fluids
     */
    IStorageTracker<FluidStack> getFluidStorageTracker();

    /**
     * @return the world where this network is in
     */
    World world();
}
