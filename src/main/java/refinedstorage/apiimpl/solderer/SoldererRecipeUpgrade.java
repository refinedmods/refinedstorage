package refinedstorage.apiimpl.solderer;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import refinedstorage.RSItems;
import refinedstorage.api.solderer.ISoldererRecipe;
import refinedstorage.item.ItemUpgrade;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SoldererRecipeUpgrade implements ISoldererRecipe {
    private ItemStack[] rows;
    private ItemStack result;

    public SoldererRecipeUpgrade(int type) {
        this.result = new ItemStack(RSItems.UPGRADE, 1, type);
        this.rows = new ItemStack[]{
            ItemUpgrade.getRequirement(type),
            new ItemStack(RSItems.UPGRADE, 1, 0),
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
