package com.raoulvdberge.refinedstorage.apiimpl.solderer;

import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.solderer.ISoldererRecipe;
import com.raoulvdberge.refinedstorage.block.EnumFluidStorageType;
import com.raoulvdberge.refinedstorage.item.ItemBlockFluidStorage;
import com.raoulvdberge.refinedstorage.item.ItemProcessor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

public class SoldererRecipeFluidStorage implements ISoldererRecipe {
    private EnumFluidStorageType type;
    private NonNullList<ItemStack> rows = NonNullList.create();

    public SoldererRecipeFluidStorage(EnumFluidStorageType type, int storagePart) {
        this.type = type;

        this.rows.add(new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC));
        this.rows.add(new ItemStack(RSItems.FLUID_STORAGE_PART, 1, storagePart));
        this.rows.add(new ItemStack(RSBlocks.MACHINE_CASING));
    }

    @Override
    @Nonnull
    public ItemStack getRow(int row) {
        return rows.get(row);
    }

    @Override
    @Nonnull
    public ItemStack getResult() {
        return ItemBlockFluidStorage.initNBT(new ItemStack(RSBlocks.FLUID_STORAGE, 1, type.getId()));
    }

    @Override
    public int getDuration() {
        return 200;
    }
}
