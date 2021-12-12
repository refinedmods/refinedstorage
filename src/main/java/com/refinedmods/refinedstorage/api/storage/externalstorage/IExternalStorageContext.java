package com.refinedmods.refinedstorage.api.storage.externalstorage;

import com.refinedmods.refinedstorage.api.storage.AccessType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * Provides information about an external storage.
 */
public interface IExternalStorageContext {
    /**
     * @return the priority of the external storage
     */
    int getPriority();

    /**
     * @return the access type of the external storage
     */
    AccessType getAccessType();

    /**
     * @param stack the stack to test
     * @return true if the external storage accepts the item, false otherwise
     */
    boolean acceptsItem(ItemStack stack);

    /**
     * @param stack the stack to test
     * @return true if the external storage accepts the fluid, false otherwise
     */
    boolean acceptsFluid(FluidStack stack);
}
