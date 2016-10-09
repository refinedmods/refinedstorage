package refinedstorage.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementRegistry;
import refinedstorage.api.autocrafting.registry.ICraftingTaskRegistry;
import refinedstorage.api.solderer.ISoldererRegistry;
import refinedstorage.api.util.IComparer;
import refinedstorage.api.util.IFluidStackList;
import refinedstorage.api.util.IItemStackList;

import javax.annotation.Nonnull;

/**
 * Represents a Refined Storage API implementation.
 * Delivered by the {@link RSAPIInject} annotation.
 */
public interface IRSAPI {
    /**
     * @return the comparer
     */
    @Nonnull
    IComparer getComparer();

    /**
     * @return the solderer registry
     */
    @Nonnull
    ISoldererRegistry getSoldererRegistry();

    /**
     * @return the crafting task registry
     */
    @Nonnull
    ICraftingTaskRegistry getCraftingTaskRegistry();

    /**
     * @return the crafting monitor element registry
     */
    @Nonnull
    ICraftingMonitorElementRegistry getCraftingMonitorElementRegistry();

    /**
     * @return an empty item stack list
     */
    @Nonnull
    IItemStackList createItemStackList();

    /**
     * @return an empty fluid stack list
     */
    @Nonnull
    IFluidStackList createFluidStackList();

    /**
     * @param stack the stack
     * @return a hashcode for the given stack
     */
    int getItemStackHashCode(ItemStack stack);

    /**
     * @param stack the stack
     * @return a hashcode for the given stack
     */
    int getFluidStackHashCode(FluidStack stack);
}
