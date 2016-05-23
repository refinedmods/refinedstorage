package refinedstorage.tile.solderer;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageItems;
import refinedstorage.item.ItemProcessor;

public class SoldererRecipeDiskDrive implements ISoldererRecipe {
    @Override
    public ItemStack getRow(int row) {
        if (row == 0) {
            return new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED);
        } else if (row == 1) {
            return new ItemStack(RefinedStorageBlocks.MACHINE_CASING);
        } else if (row == 2) {
            return new ItemStack(Blocks.CHEST);
        }

        return null;
    }

    @Override
    public ItemStack getResult() {
        return new ItemStack(RefinedStorageBlocks.DISK_DRIVE);
    }

    @Override
    public int getDuration() {
        return 500;
    }
}
