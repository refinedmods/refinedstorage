package refinedstorage.apiimpl.solderer;

import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageItems;
import refinedstorage.api.solderer.ISoldererRecipe;
import refinedstorage.item.ItemUpgrade;

public class SoldererRecipeUpgrade implements ISoldererRecipe {
    private ItemStack[] rows;
    private ItemStack result;

    public SoldererRecipeUpgrade(int type) {
        this.result = new ItemStack(RefinedStorageItems.UPGRADE, 1, type);
        this.rows = new ItemStack[]{
            ItemUpgrade.getRequirement(type),
            new ItemStack(RefinedStorageItems.UPGRADE, 1, 0),
            ItemUpgrade.getRequirement(type)
        };
    }

    @Override
    public ItemStack getRow(int row) {
        return rows[row];
    }

    @Override
    public ItemStack getResult() {
        return result;
    }

    @Override
    public int getDuration() {
        return 250;
    }
}
