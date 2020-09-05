package com.refinedmods.refinedstorage.apiimpl.storage.disk.factory;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskFactory;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.FluidStorageDisk;
import com.refinedmods.refinedstorage.item.FluidStorageDiskItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

import java.util.UUID;

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
    public ItemStack createDiskItem(IStorageDisk<FluidStack> disk, UUID id) {
        FluidStorageDiskItem item;
        switch (disk.getCapacity()) {
            case 64_000:
                item = RSItems.SIXTY_FOUR_K_FLUID_STORAGE_DISK;
                break;
            case 256_000:
                item = RSItems.TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_DISK;
                break;
            case 1024_000:
                item = RSItems.THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_DISK;
                break;
            case 4096_000:
                item = RSItems.FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_DISK;
                break;
            default:
                item = RSItems.CREATIVE_FLUID_STORAGE_DISK;
                break;
        }

        ItemStack stack = new ItemStack(item);
        item.setId(stack, id);
        return stack;
    }

    @Override
    public IStorageDisk<FluidStack> create(ServerWorld world, int capacity) {
        return new FluidStorageDisk(world, capacity);
    }
}
