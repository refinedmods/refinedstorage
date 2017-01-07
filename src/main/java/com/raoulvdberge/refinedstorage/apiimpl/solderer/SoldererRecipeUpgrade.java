package com.raoulvdberge.refinedstorage.apiimpl.solderer;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.solderer.ISoldererRecipe;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

public class SoldererRecipeUpgrade implements ISoldererRecipe {
    private ItemStack result;
    private NonNullList<ItemStack> rows = NonNullList.create();

    public SoldererRecipeUpgrade(int type) {
        this.result = new ItemStack(RSItems.UPGRADE, 1, type);

        this.rows.add(ItemUpgrade.getRequirement(result));
        this.rows.add(new ItemStack(Items.REDSTONE));
        this.rows.add(new ItemStack(RSItems.UPGRADE, 1, 0));
    }

    public SoldererRecipeUpgrade(ItemStack result) {
        this.result = result;

        this.rows.add(ItemUpgrade.getRequirement(result));
        this.rows.add(new ItemStack(Items.REDSTONE));
        this.rows.add(new ItemStack(RSItems.UPGRADE, 1, 0));
    }

    @Override
    @Nonnull
    public ItemStack getRow(int row) {
        return rows.get(row);
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
