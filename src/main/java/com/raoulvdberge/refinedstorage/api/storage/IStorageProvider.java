package com.raoulvdberge.refinedstorage.api.storage;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public interface IStorageProvider {
    void addItemStorages(List<IStorage<ItemStack>> storages);

    void addFluidStorages(List<IStorage<FluidStack>> storages);
}
