package refinedstorage.tile.solderer;

import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageItems;
import refinedstorage.block.EnumCraftingCPUType;
import refinedstorage.item.ItemBlockStorage;
import refinedstorage.item.ItemProcessor;

public class SoldererRecipeCraftingCPU implements ISoldererRecipe {
    private EnumCraftingCPUType type;
    private int storagePart;

    public SoldererRecipeCraftingCPU(EnumCraftingCPUType type, int storagePart) {
        this.type = type;
        this.storagePart = storagePart;
    }

    @Override
    public ItemStack getRow(int row) {
        if (row == 0) {
            return new ItemStack(RefinedStorageItems.STORAGE_PART, 1, storagePart);
        } else if (row == 1) {
            return new ItemStack(RefinedStorageBlocks.CRAFTING_UNIT);
        } else if (row == 2) {
            return new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED);
        }

        return null;
    }

    @Override
    public ItemStack getResult() {
        return ItemBlockStorage.initNBT(new ItemStack(RefinedStorageBlocks.CRAFTING_CPU, 1, type.getId()));
    }

    @Override
    public int getDuration() {
        return 200;
    }
}
