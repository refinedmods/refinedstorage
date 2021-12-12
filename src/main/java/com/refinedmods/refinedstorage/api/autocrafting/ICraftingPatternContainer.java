package com.refinedmods.refinedstorage.api.autocrafting;

import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Represents a network node that contains crafting patterns.
 */
public interface ICraftingPatternContainer {
    /**
     * Returns the interval of when a crafting step with a pattern in this container can update.
     * Minimum value is 0 (each tick).
     * <p>
     * Note: rather than maxing out the update interval, implementors might want to balance around {@link #getMaximumSuccessfulCraftingUpdates()}.
     * This method merely speeds up the update rate, it might be more interesting to increase the output rate in {@link #getMaximumSuccessfulCraftingUpdates()}.
     *
     * @return the update interval
     */
    default int getUpdateInterval() {
        return 10;
    }

    /**
     * Returns the amount of successful crafting updates that this container can have per crafting step update.
     * If this limit is reached, crafting patterns from this container won't be able to update until the next
     * eligible crafting step update interval from {@link #getUpdateInterval()}.
     *
     * @return the maximum amount of successful crafting updates
     */
    default int getMaximumSuccessfulCraftingUpdates() {
        return 1;
    }

    /**
     * @return the inventory that this container is connected to, or null if no inventory is present
     */
    @Nullable
    IItemHandler getConnectedInventory();

    /**
     * @return the fluid inventory that this container is connected to, or null if no fluid inventory is present
     */
    @Nullable
    IFluidHandler getConnectedFluidInventory();

    /**
     * @return the tile that this container is connected to, or null if no tile is present
     */
    @Nullable
    BlockEntity getConnectedTile();

    /**
     * @return the tile that this container is facing
     */
    BlockEntity getFacingTile();

    /**
     * @return the direction to the facing tile
     */
    Direction getDirection();

    /**
     * @return the patterns stored in this container
     */
    List<ICraftingPattern> getPatterns();

    /**
     * @return the pattern inventory, or null if no pattern inventory is present
     */
    @Nullable
    IItemHandlerModifiable getPatternInventory();

    /**
     * The name of this container for categorizing in the Crafting Manager GUI.
     *
     * @return the name of this container
     */
    Component getName();

    /**
     * @return the position of this container
     */
    BlockPos getPosition();

    /**
     * Containers may be daisy-chained together.  If this container points to
     * another one, gets the root container in the chain.  If containers are
     * not daisy-chained, returns this container.  If there was a container
     * loop, returns null.
     *
     * @return the root pattern container
     */
    @Nullable
    ICraftingPatternContainer getRootContainer();

    /**
     * @return the UUID of this container
     */
    UUID getUuid();

    /**
     * @return true if the connected inventory is locked for processing patterns, false otherwise
     */
    default boolean isLocked() {
        return false;
    }

    /**
     * Unlock the container so it may be used by processing pattern
     */
    void unlock();

    /**
     * Called when this container is used by a processing pattern to insert items or fluids in the connected inventory.
     */
    default void onUsedForProcessing() {
    }

    /**
     * @return whether the container is successfully connected to the inventory it wants to insert to
     */
    default boolean hasConnectedInventory() {
        return getConnectedInventory() != null;
    }

    /**
     * @return whether the container is successfully connected to the fluid inventory it wants to insert to
     */
    default boolean hasConnectedFluidInventory() {
        return getConnectedFluidInventory() != null;
    }

    /**
     * Called by Autocrafting when it uses this crafter in a processing recipe that has items as input
     *
     * @param toInsert Collection of Itemstack stacklist entries to insert into the inventory
     * @param action   action to perform
     * @return whether insertion was successful
     */
    default boolean insertItemsIntoInventory(Collection<StackListEntry<ItemStack>> toInsert, Action action) {
        IItemHandler dest = getConnectedInventory();


        if (toInsert.isEmpty()) {
            return true;
        }

        if (dest == null) {
            return false;
        }

        Deque<StackListEntry<ItemStack>> stacks = new ArrayDeque<>(toInsert);

        StackListEntry<ItemStack> currentEntry = stacks.poll();

        ItemStack current = currentEntry != null ? currentEntry.getStack() : null;

        List<Integer> availableSlots = IntStream.range(0, dest.getSlots()).boxed().collect(Collectors.toList());

        while (current != null && !availableSlots.isEmpty()) {
            ItemStack remainder = ItemStack.EMPTY;

            for (int i = 0; i < availableSlots.size(); ++i) {
                int slot = availableSlots.get(i);

                // .copy() is mandatory!
                remainder = dest.insertItem(slot, current.copy(), action == Action.SIMULATE);

                // If we inserted *something*
                if (remainder.isEmpty() || current.getCount() != remainder.getCount()) {
                    availableSlots.remove(i);
                    break;
                }
            }

            if (remainder.isEmpty()) { // If we inserted successfully, get a next stack.
                currentEntry = stacks.poll();

                current = currentEntry != null ? currentEntry.getStack() : null;
            } else if (current.getCount() == remainder.getCount()) { // If we didn't insert anything over ALL these slots, stop here.
                break;
            } else { // If we didn't insert all, continue with other slots and use our remainder.
                current = remainder;
            }
        }

        boolean success = current == null && stacks.isEmpty();

        if (!success && action == Action.PERFORM) {
            LogManager.getLogger().warn("Inventory unexpectedly didn't accept {}, the remainder has been voided!", current != null ? current.getDescriptionId() : null);
        }

        return success;
    }

    /**
     * Called by Autocrafting when it uses this crafter in a processing recipe that has fluids as input
     *
     * @param toInsert Collection of Fluidstack stacklist entries to insert into the inventory
     * @param action   action to perform
     * @return whether insertion was successful
     */
    default boolean insertFluidsIntoInventory(Collection<StackListEntry<FluidStack>> toInsert, Action action) {
        IFluidHandler dest = getConnectedFluidInventory();

        if (toInsert.isEmpty()) {
            return true;
        }

        if (dest == null) {
            return false;
        }

        for (StackListEntry<FluidStack> entry : toInsert) {
            int filled = dest.fill(entry.getStack(), action == Action.SIMULATE ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);

            if (filled != entry.getStack().getAmount()) {
                if (action == Action.PERFORM) {
                    LogManager.getLogger().warn("Inventory unexpectedly didn't accept all of {}, the remainder has been voided!", entry.getStack().getTranslationKey());
                }

                return false;
            }
        }

        return true;
    }
}
