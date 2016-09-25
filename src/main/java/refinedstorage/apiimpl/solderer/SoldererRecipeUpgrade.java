package refinedstorage.apiimpl.solderer;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageItems;
import refinedstorage.api.solderer.ISoldererRecipe;
import refinedstorage.item.ItemUpgrade;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SoldererRecipeUpgrade implements ISoldererRecipe {
    private ItemStack[] rows;
    private ItemStack result;

    public SoldererRecipeUpgrade(int type) {
        this.result = new ItemStack(RefinedStorageItems.UPGRADE, 1, type);
        this.rows = new ItemStack[]{
            ItemUpgrade.getRequirement(type),
            new ItemStack(RefinedStorageItems.UPGRADE, 1, 0),
            new ItemStack(Items.REDSTONE)
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
