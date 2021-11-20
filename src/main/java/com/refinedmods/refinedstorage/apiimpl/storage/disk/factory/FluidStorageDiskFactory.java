package com.refinedmods.refinedstorage.apiimpl.storage.disk.factory;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskFactory;
import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.FluidStorageDisk;
import com.refinedmods.refinedstorage.item.FluidStorageDiskItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.UUID;

public class FluidStorageDiskFactory implements IStorageDiskFactory<FluidStack> {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "fluid");

    @Override
    public IStorageDisk<FluidStack> createFromNbt(ServerWorld world, CompoundNBT tag) {
        FluidStorageDisk disk = new FluidStorageDisk(
            world,
            tag.getInt(FluidStorageDisk.NBT_CAPACITY),
            tag.contains(FluidStorageDisk.NBT_OWNER) ? tag.getUniqueId(FluidStorageDisk.NBT_OWNER) : null
        );

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
                item = RSItems.FLUID_STORAGE_DISKS.get(FluidStorageType.SIXTY_FOUR_K).get();
                break;
            case 256_000:
                item = RSItems.FLUID_STORAGE_DISKS.get(FluidStorageType.TWO_HUNDRED_FIFTY_SIX_K).get();
                break;
            case 1024_000:
                item = RSItems.FLUID_STORAGE_DISKS.get(FluidStorageType.THOUSAND_TWENTY_FOUR_K).get();
                break;
            case 4096_000:
                item = RSItems.FLUID_STORAGE_DISKS.get(FluidStorageType.FOUR_THOUSAND_NINETY_SIX_K).get();
                break;
            default:
                item = RSItems.FLUID_STORAGE_DISKS.get(FluidStorageType.CREATIVE).get();
                break;
        }

        ItemStack stack = new ItemStack(item);
        item.setId(stack, id);
        return stack;
    }

    @Override
    public IStorageDisk<FluidStack> create(ServerWorld world, int capacity, @Nullable UUID owner) {
        return new FluidStorageDisk(world, capacity, owner);
    }
}
