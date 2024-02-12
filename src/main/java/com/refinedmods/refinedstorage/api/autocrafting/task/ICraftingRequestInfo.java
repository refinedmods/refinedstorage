package com.refinedmods.refinedstorage.api.autocrafting.task;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
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
    CompoundTag writeToNbt();
}
