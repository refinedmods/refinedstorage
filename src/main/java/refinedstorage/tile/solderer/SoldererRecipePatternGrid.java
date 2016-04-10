package refinedstorage.tile.solderer;

import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageItems;
import refinedstorage.block.EnumGridType;
import refinedstorage.item.ItemProcessor;

public class SoldererRecipePatternGrid implements ISoldererRecipe {
    @Override
    public ItemStack getRow(int row) {
        if (row == 0) {
            return new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED);
        } else if (row == 1) {
            return new ItemStack(RefinedStorageBlocks.GRID, 1, EnumGridType.NORMAL.getId());
        } else if (row == 2) {
            return new ItemStack(RefinedStorageItems.PATTERN);
        }

        return null;
    }

    @Override
    public ItemStack getResult() {
        return new ItemStack(RefinedStorageBlocks.GRID, 1, EnumGridType.PATTERN.getId());
    }

    @Override
    public int getDuration() {
        return 500;
    }
}
