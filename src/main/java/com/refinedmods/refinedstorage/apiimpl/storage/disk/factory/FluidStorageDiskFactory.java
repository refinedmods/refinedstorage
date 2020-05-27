package com.refinedmods.refinedstorage.apiimpl.storage.disk.factory;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskFactory;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.FluidStorageDisk;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

public class FluidStorageDiskFactory implements IStorageDiskFactory<FluidStack> {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "fluid");

    @Override
    public IStorageDisk<FluidStack> createFromNbt(ServerWorld world, CompoundNBT tag) {
        FluidStorageDisk disk = new FluidStorageDisk(world, tag.getInt(FluidStorageDisk.NBT_CAPACITY));

        ListNBT list = tag.getList(FluidStorageDisk.NBT_FLUIDS, Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < list.size(); ++i) {
            FluidStack stack = FluidStack.loadFluidStackFromNBT(list.getCompound(i));

            if (!stack.isEmpty()) {
                disk.getRawStacks().put(stack.getRawFluid(), stack);
            }
        }

        return disk;
    }

    @Override
    public IStorageDisk<FluidStack> create(ServerWorld world, int capacity) {
        return new FluidStorageDisk(world, capacity);
    }
}
