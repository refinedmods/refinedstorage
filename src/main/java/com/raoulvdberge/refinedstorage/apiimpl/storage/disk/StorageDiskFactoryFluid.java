package com.raoulvdberge.refinedstorage.apiimpl.storage.disk;

import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskFactory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class StorageDiskFactoryFluid implements IStorageDiskFactory<FluidStack> {
    public static final String ID = "normal_fluid";

    @Override
    public IStorageDisk<FluidStack> createFromNbt(World world, NBTTagCompound tag) {
        StorageDiskFluid disk = new StorageDiskFluid(world, tag.getInteger(StorageDiskFluid.NBT_CAPACITY));

        NBTTagList list = (NBTTagList) tag.getTag(StorageDiskFluid.NBT_FLUIDS);

        for (int i = 0; i < list.tagCount(); ++i) {
            FluidStack stack = FluidStack.loadFluidStackFromNBT(list.getCompoundTagAt(i));

            if (stack != null) {
                disk.getRawStacks().put(stack.getFluid(), stack);
            }
        }

        return disk;
    }

    @Override
    public IStorageDisk<FluidStack> create(World world, int capacity) {
        return new StorageDiskFluid(world, capacity);
    }
}
