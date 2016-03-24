package refinedstorage.tile.solderer;

import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageItems;
import refinedstorage.item.ItemProcessor;

public class SoldererRecipeWirelessGrid implements ISoldererRecipe {
    private int type;

    public SoldererRecipeWirelessGrid(int type) {
        this.type = type;
    }

    @Override
    public ItemStack getRow(int row) {
        if (row == 0) {
            return new ItemStack(RefinedStorageItems.WIRELESS_GRID_PLATE);
        } else if (row == 1) {
            return new ItemStack(RefinedStorageBlocks.GRID, 1, type);
        } else if (row == 2) {
            return new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED);
        }

        return null;
    }

    @Override
    public ItemStack getResult() {
        return new ItemStack(RefinedStorageItems.WIRELESS_GRID, 1, type);
    }

    @Override
    public int getDuration() {
        return 1000;
    }
}
