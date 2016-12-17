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
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

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
    IStackList<ItemStack> createItemStackList();

    /**
     * @return an empty fluid stack list
     */
    @Nonnull
    IStackList<FluidStack> createFluidStackList();

    /**
     * @return an empty crafting monitor element list
     */
    @Nonnull
    ICraftingMonitorElementList createCraftingMonitorElementList();

    /**
     * Let's the neighbors of a node know that there is a node placed at the given position.
     *
     * @param world the world
     * @param pos   the position of the node
     */
    void discoverNode(World world, BlockPos pos);

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
