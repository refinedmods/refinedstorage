package com.raoulvdberge.refinedstorage.apiimpl.solderer;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.solderer.ISoldererRecipe;
import com.raoulvdberge.refinedstorage.item.ItemProcessor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SoldererRecipeProcessor implements ISoldererRecipe {
    private int type;
    private ItemStack result;
    private List<NonNullList<ItemStack>> rows = new ArrayList<>();

    public SoldererRecipeProcessor(int type) {
        this.type = type;

        this.result = new ItemStack(RSItems.PROCESSOR, 1, type);

        this.rows.add(NonNullList.withSize(1, createPrintedProcessor()));
        this.rows.add(OreDictionary.getOres("dustRedstone"));
        this.rows.add(NonNullList.withSize(1, new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_PRINTED_SILICON)));
    }

    private ItemStack createPrintedProcessor() {
        switch (type) {
            case ItemProcessor.TYPE_BASIC:
                return new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_PRINTED_BASIC);
            case ItemProcessor.TYPE_IMPROVED:
                return new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_PRINTED_IMPROVED);
            case ItemProcessor.TYPE_ADVANCED:
                return new ItemStack(RSItems.PROCESSOR, 1, ItemProcessor.TYPE_PRINTED_ADVANCED);
            default:
                return ItemStack.EMPTY;
        }
    }

    @Override
    @Nonnull
    public NonNullList<ItemStack> getRow(int row) {
        return rows.get(row);
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
