package com.raoulvdberge.refinedstorage.apiimpl.solderer;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.solderer.ISoldererRecipe;
import com.raoulvdberge.refinedstorage.item.ItemProcessor;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SoldererRecipeProcessor implements ISoldererRecipe {
    private int type;
    private ItemStack[] rows;
    private ItemStack result;

    public SoldererRecipeProcessor(int type) {
        this.type = type;

        ItemStack printedProcessor = null;

        switch (type) {
            case ItemProcessor.TYPE_BASIC:
                printedProcessor = new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_PRINTED_BASIC);
                break;
            case ItemProcessor.TYPE_IMPROVED:
                printedProcessor = new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_PRINTED_IMPROVED);
                break;
            case ItemProcessor.TYPE_ADVANCED:
                printedProcessor = new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_PRINTED_ADVANCED);
                break;
        }

        this.result = new ItemStack(RSItems.PROCESSOR, 1, type);
        this.rows = new ItemStack[]{
            printedProcessor,
            new ItemStack(Items.REDSTONE),
            new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_PRINTED_SILICON)
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
        return result;
    }

    @Override
    public int getDuration() {
        switch (type) {
            case ItemProcessor.TYPE_BASIC:
                return 250;
            case ItemProcessor.TYPE_IMPROVED:
                return 300;
            case ItemProcessor.TYPE_ADVANCED:
                return 350;
            default:
                return 0;
        }
    }
}
