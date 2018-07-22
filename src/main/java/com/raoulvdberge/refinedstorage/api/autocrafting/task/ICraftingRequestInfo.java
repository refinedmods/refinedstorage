package com.raoulvdberge.refinedstorage.api.autocrafting.task;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

/**
 * Contains information about a crafting request.
 */
public interface ICraftingRequestInfo {
    /**
     * @return the item requested, or null if no item was requested
     */
    @Nullable
    ItemStack getItem();

    /**
     * @return the fluid requested, or null if no fluid was requested
     */
    @Nullable
    FluidStack getFluid();

    /**
     * @return the written tag
     */
    NBTTagCompound writeToNbt();
}
