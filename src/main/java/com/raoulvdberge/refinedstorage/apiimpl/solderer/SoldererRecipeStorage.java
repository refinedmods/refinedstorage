package com.raoulvdberge.refinedstorage.apiimpl.solderer;

import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.solderer.ISoldererRecipe;
import com.raoulvdberge.refinedstorage.block.EnumItemStorageType;
import com.raoulvdberge.refinedstorage.item.ItemBlockStorage;
import com.raoulvdberge.refinedstorage.item.ItemProcessor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

public class SoldererRecipeStorage implements ISoldererRecipe {
    private EnumItemStorageType type;
    private NonNullList<ItemStack> rows = NonNullList.create();

    public SoldererRecipeStorage(EnumItemStorageType type, int storagePart) {
        this.type = type;

        this.rows.add(new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC));
        this.rows.add(new ItemStack(RSItems.STORAGE_PART, 1, storagePart));
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
        return ItemBlockStorage.initNBT(new ItemStack(RSBlocks.STORAGE, 1, type.getId()));
    }

    @Override
    public int getDuration() {
        return 200;
    }
}
