package refinedstorage.tile.solderer;

import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageItems;
import refinedstorage.block.EnumStorageType;
import refinedstorage.item.ItemBlockStorage;
import refinedstorage.item.ItemProcessor;

public class SoldererRecipeStorage implements ISoldererRecipe {
    private EnumStorageType type;
    private int storagePart;

    public SoldererRecipeStorage(EnumStorageType type, int storagePart) {
        this.type = type;
        this.storagePart = storagePart;
    }

    @Override
    public ItemStack getRow(int row) {
        if (row == 0) {
            return new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC);
        } else if (row == 1) {
            return new ItemStack(RefinedStorageBlocks.MACHINE_CASING);
        } else if (row == 2) {
            return new ItemStack(RefinedStorageItems.STORAGE_PART, 1, storagePart);
        }

        return null;
    }

    @Override
    public ItemStack getResult() {
        return ItemBlockStorage.initNBT(new ItemStack(RefinedStorageBlocks.STORAGE, 1, type.getId()));
    }

    @Override
    public int getDuration() {
        return 200;
    }
}
