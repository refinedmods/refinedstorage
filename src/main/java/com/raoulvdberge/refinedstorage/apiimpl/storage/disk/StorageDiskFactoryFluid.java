package com.raoulvdberge.refinedstorage.apiimpl.storage.disk;

import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskFactory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

public class StorageDiskFactoryFluid implements IStorageDiskFactory<FluidStack> {
    public static final String ID = "normal_fluid";

    @Override
    public IStorageDisk<FluidStack> createFromNbt(World world, CompoundNBT tag) {
        StorageDiskFluid disk = new StorageDiskFluid(world, tag.getInt(StorageDiskFluid.NBT_CAPACITY));

        ListNBT list = tag.getList(StorageDiskFluid.NBT_FLUIDS, Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < list.size(); ++i) {
            FluidStack stack = FluidStack.loadFluidStackFromNBT(list.getCompound(i));

            if (stack != null) {
                disk.getRawStacks().put(stack.getRawFluid(), stack);
            }
        }

        return disk;
    }

    @Override
    public IStorageDisk<FluidStack> create(World world, int capacity) {
        return new StorageDiskFluid(world, capacity);
    }
}
