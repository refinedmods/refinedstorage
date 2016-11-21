package com.raoulvdberge.refinedstorage.api;

import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementList;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementRegistry;
import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElementRegistry;
import com.raoulvdberge.refinedstorage.api.autocrafting.registry.ICraftingTaskRegistry;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandlerRegistry;
import com.raoulvdberge.refinedstorage.api.solderer.ISoldererRegistry;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IFluidStackList;
import com.raoulvdberge.refinedstorage.api.util.IItemStackList;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.function.Predicate;

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
     * @return the crafting preview element registry
     */
    @Nonnull
    ICraftingPreviewElementRegistry getCraftingPreviewElementRegistry();

    /**
     * @return the reader writer handler registry
     */
    @Nonnull
    IReaderWriterHandlerRegistry getReaderWriterHandlerRegistry();

    /**
     * @return a new reader writer channel
     */
    @Nonnull
    IReaderWriterChannel createReaderWriterChannel(String name, INetworkMaster network);

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

    @Nonnull
    ICraftingMonitorElementList createCraftingMonitorElementList();

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

    /**
     * @return a set with the predicates to check if a block is connectable
     */
    Set<Predicate<TileEntity>> getConnectableConditions();
}
