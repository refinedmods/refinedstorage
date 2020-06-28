package com.refinedmods.refinedstorage.apiimpl.network.node.exporter;

import com.refinedmods.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.refinedmods.refinedstorage.api.autocrafting.task.interceptor.IOutputInterceptor;
import com.refinedmods.refinedstorage.api.autocrafting.task.interceptor.IOutputInterceptorFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fluids.FluidStack;

public class ExporterOutputInterceptorFactory implements IOutputInterceptorFactory {
    @Override
    public IOutputInterceptor create(CompoundNBT tag) throws CraftingTaskReadException {
        ItemStack interestedItemStack = ItemStack.read(tag.getCompound("Item"));
        FluidStack interestedFluidStack = FluidStack.loadFluidStackFromNBT(tag.getCompound("Fluid"));
        DimensionType dim = DimensionType.byName(new ResourceLocation(tag.getString("Dim")));
        if (dim == null) {
            throw new CraftingTaskReadException("Dimension " + tag.getString("Dim") + " no longer exists");
        }

        BlockPos pos = BlockPos.fromLong(tag.getLong("Pos"));

        return new ExporterOutputInterceptor(interestedItemStack, interestedFluidStack, dim, pos);
    }
}
