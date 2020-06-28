package com.refinedmods.refinedstorage.apiimpl.network.node.iface;

import com.refinedmods.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.refinedmods.refinedstorage.api.autocrafting.task.interceptor.IOutputInterceptor;
import com.refinedmods.refinedstorage.api.autocrafting.task.interceptor.IOutputInterceptorFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class InterfaceOutputInterceptorFactory implements IOutputInterceptorFactory {
    @Override
    public IOutputInterceptor create(CompoundNBT tag) throws CraftingTaskReadException {
        ItemStack interestedItemStack = ItemStack.read(tag.getCompound("Item"));
        DimensionType dim = DimensionType.byName(new ResourceLocation(tag.getString("Dim")));
        if (dim == null) {
            throw new CraftingTaskReadException("Dimension " + tag.getString("Dim") + " no longer exists");
        }

        BlockPos pos = BlockPos.fromLong(tag.getLong("Pos"));
        int slot = tag.getInt("Slot");

        return new InterfaceOutputInterceptor(interestedItemStack, dim, pos, slot);
    }
}
