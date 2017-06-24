package com.raoulvdberge.refinedstorage.apiimpl.solderer;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.solderer.ISoldererRecipe;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SoldererRecipeUpgrade implements ISoldererRecipe {
    private ItemStack result;
    private List<NonNullList<ItemStack>> rows = new ArrayList<>();

    public SoldererRecipeUpgrade(int type) {
        this.result = new ItemStack(RSItems.UPGRADE, 1, type);

        this.rows.add(ItemUpgrade.getRequirement(result));
        this.rows.add(OreDictionary.getOres("dustRedstone"));
        this.rows.add(NonNullList.withSize(1, new ItemStack(RSItems.UPGRADE, 1, 0)));
    }

    public SoldererRecipeUpgrade(ItemStack result) {
        this.result = result;

        this.rows.add(ItemUpgrade.getRequirement(result));
        this.rows.add(OreDictionary.getOres("dustRedstone"));
        this.rows.add(NonNullList.withSize(1, new ItemStack(RSItems.UPGRADE, 1, 0)));
    }

    @Override
    @Nonnull
    public NonNullList<ItemStack> getRow(int row) {
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
