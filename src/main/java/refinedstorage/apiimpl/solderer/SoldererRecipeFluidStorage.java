package refinedstorage.apiimpl.solderer;

import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageItems;
import refinedstorage.api.solderer.ISoldererRecipe;
import refinedstorage.block.EnumFluidStorageType;
import refinedstorage.item.ItemBlockFluidStorage;
import refinedstorage.item.ItemProcessor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SoldererRecipeFluidStorage implements ISoldererRecipe {
    private EnumFluidStorageType type;
    private ItemStack[] rows;

    public SoldererRecipeFluidStorage(EnumFluidStorageType type, int storagePart) {
        this.type = type;
        this.rows = new ItemStack[]{
            new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
            new ItemStack(RefinedStorageBlocks.MACHINE_CASING),
            new ItemStack(RefinedStorageItems.FLUID_STORAGE_PART, 1, storagePart)
        };
    }

    @Override
    @Nullable
    public ItemStack getRow(int row) {
        return rows[row];
    }

    @Override
    @Nonnull
    public ItemStack getResult() {
        return ItemBlockFluidStorage.initNBT(new ItemStack(RefinedStorageBlocks.FLUID_STORAGE, 1, type.getId()));
    }

    @Override
    public int getDuration() {
        return 200;
    }
}
