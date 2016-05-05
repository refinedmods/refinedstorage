package refinedstorage.tile.solderer;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageItems;
import refinedstorage.item.ItemUpgrade;

public class SoldererRecipeUpgrade implements ISoldererRecipe {
    private int type;

    public SoldererRecipeUpgrade(int type) {
        this.type = type;
    }

    @Override
    public ItemStack getRow(int row) {
        if (row == 0) {
            return getBottomAndTopItem();
        } else if (row == 1) {
            return new ItemStack(RefinedStorageItems.UPGRADE, 1, 0);
        } else if (row == 2) {
            return getBottomAndTopItem();
        }

        return null;
    }

    private ItemStack getBottomAndTopItem() {
        switch (type) {
            case ItemUpgrade.TYPE_RANGE:
                return new ItemStack(Items.ender_pearl);
            case ItemUpgrade.TYPE_SPEED:
                return new ItemStack(Blocks.redstone_block);
        }

        return null;
    }

    @Override
    public ItemStack getResult() {
        return new ItemStack(RefinedStorageItems.UPGRADE, 1, type);
    }

    @Override
    public int getDuration() {
        return 250;
    }
}
