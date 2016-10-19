package com.raoulvdberge.refinedstorage.api.autocrafting.registry;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A factory that creates a crafting task.
 * Register this factory in the {@link ICraftingTaskRegistry}.
 */
public interface ICraftingTaskFactory {
    /**
     * Returns a crafting task for a given NBT tag and pattern.
     *
     * @param world    the world
     * @param network  the network
     * @param stack    the stack to create task for
     * @param pattern  the pattern
     * @param quantity the quantity
     * @param tag      the NBT tag, if this is null it isn't reading from disk but is used for making a task on demand
     * @return the crafting task
     */
    @Nonnull
    ICraftingTask create(World world, INetworkMaster network, @Nullable ItemStack stack, ICraftingPattern pattern, int quantity, @Nullable NBTTagCompound tag);
}
