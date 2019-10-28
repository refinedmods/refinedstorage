package com.raoulvdberge.refinedstorage.apiimpl.storage.disk.factory;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskFactory;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.FluidStorageDisk;
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
        if (world == null) {
            throw new IllegalArgumentException("World cannot be null");
        }

        FluidStorageDisk disk = new FluidStorageDisk(world, tag.getInt(FluidStorageDisk.NBT_CAPACITY));

        ListNBT list = tag.getList(FluidStorageDisk.NBT_FLUIDS, Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < list.size(); ++i) {
            FluidStack stack = FluidStack.loadFluidStackFromNBT(list.getCompound(i));

            if (stack != null) {
                disk.getRawStacks().put(stack.getRawFluid(), stack);
            }
        }

        return disk;
    }

    @Override
    public IStorageDisk<FluidStack> create(ServerWorld world, int capacity) {
        if (world == null) {
            throw new IllegalArgumentException("World cannot be null");
        }

        return new FluidStorageDisk(world, capacity);
    }
}
