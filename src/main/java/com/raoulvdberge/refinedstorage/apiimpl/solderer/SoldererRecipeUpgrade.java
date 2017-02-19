package com.raoulvdberge.refinedstorage.apiimpl.solderer;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.solderer.ISoldererRecipe;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SoldererRecipeUpgrade implements ISoldererRecipe {
    private ItemStack[] rows;
    private ItemStack result;

    public SoldererRecipeUpgrade(int type) {
        this.result = new ItemStack(RSItems.UPGRADE, 1, type);
        this.rows = new ItemStack[]{
            ItemUpgrade.getRequirement(result),
            new ItemStack(Items.REDSTONE),
            new ItemStack(RSItems.UPGRADE, 1, 0)
        };
    }

    public SoldererRecipeUpgrade(ItemStack result) {
        this.result = result;
        this.rows = new ItemStack[]{
            ItemUpgrade.getRequirement(result),
            new ItemStack(Items.REDSTONE),
            new ItemStack(RSItems.UPGRADE, 1, 0),
        };
    }

    @Override
    @Nullable
    public ItemStack getRow(int row) {
        return rows[row];
    }

    @Override
    @Nonnull
    public ItemStack getResult() {
        return result;
    }

    @Override
    public int getDuration() {
        return 250;
    }
}
