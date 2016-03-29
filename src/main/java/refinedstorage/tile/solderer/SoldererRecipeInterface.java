package refinedstorage.tile.solderer;

import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageItems;
import refinedstorage.item.ItemProcessor;

public class SoldererRecipeInterface implements ISoldererRecipe {
    @Override
    public ItemStack getRow(int row) {
        if (row == 0) {
            return new ItemStack(RefinedStorageBlocks.IMPORTER);
        } else if (row == 1) {
            return new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC);
        } else if (row == 2) {
            return new ItemStack(RefinedStorageBlocks.EXPORTER);
        }

        return null;
    }

    @Override
    public ItemStack getResult() {
        return new ItemStack(RefinedStorageBlocks.INTERFACE);
    }

    @Override
    public int getDuration() {
        return 200;
    }
}
