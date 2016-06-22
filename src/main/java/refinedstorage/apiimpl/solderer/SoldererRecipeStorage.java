package refinedstorage.apiimpl.solderer;

import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageItems;
import refinedstorage.api.solderer.ISoldererRecipe;
import refinedstorage.block.EnumStorageType;
import refinedstorage.item.ItemBlockStorage;
import refinedstorage.item.ItemProcessor;

public class SoldererRecipeStorage implements ISoldererRecipe {
    private EnumStorageType type;
    private ItemStack[] rows;

    public SoldererRecipeStorage(EnumStorageType type, int storagePart) {
        this.type = type;
        this.rows = new ItemStack[]{
            new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
            new ItemStack(RefinedStorageBlocks.MACHINE_CASING),
            new ItemStack(RefinedStorageItems.STORAGE_PART, 1, storagePart)
        };
    }

    @Override
    public ItemStack getRow(int row) {
        return rows[row];
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
