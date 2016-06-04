package refinedstorage.solderer;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageItems;
import refinedstorage.api.solderer.ISoldererRecipe;
import refinedstorage.item.ItemUpgrade;

public class SoldererRecipeUpgrade implements ISoldererRecipe {
    private ItemStack[] rows;
    private ItemStack result;

    public SoldererRecipeUpgrade(int type) {
        ItemStack requirement = null;

        switch (type) {
            case ItemUpgrade.TYPE_RANGE:
                requirement = new ItemStack(Items.ENDER_PEARL);
                break;
            case ItemUpgrade.TYPE_SPEED:
                requirement = new ItemStack(Items.SUGAR);
                break;
            case ItemUpgrade.TYPE_CRAFTING:
                requirement = new ItemStack(Blocks.CRAFTING_TABLE);
                break;
        }

        this.result = new ItemStack(RefinedStorageItems.UPGRADE, 1, type);
        this.rows = new ItemStack[]{
            requirement,
            new ItemStack(RefinedStorageItems.UPGRADE, 1, 0),
            requirement
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
