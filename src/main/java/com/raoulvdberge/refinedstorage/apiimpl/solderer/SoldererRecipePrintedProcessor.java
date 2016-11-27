package com.raoulvdberge.refinedstorage.apiimpl.solderer;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.solderer.ISoldererRecipe;
import com.raoulvdberge.refinedstorage.item.ItemProcessor;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class SoldererRecipePrintedProcessor implements ISoldererRecipe {
    private int type;
    private boolean fullBlock;
    private ItemStack result;
    private ItemStack requirement;

    public SoldererRecipePrintedProcessor(int type, boolean fullBlock) {
        this.type = type;
        this.fullBlock = fullBlock;
        this.result = new ItemStack(RSItems.PROCESSOR, fullBlock ? 9 : 1, type);

        switch (type) {
            case ItemProcessor.TYPE_PRINTED_BASIC:
                this.requirement = fullBlock ? new ItemStack(Blocks.IRON_BLOCK) : new ItemStack(Items.IRON_INGOT);
                break;
            case ItemProcessor.TYPE_PRINTED_IMPROVED:
                this.requirement = fullBlock ? new ItemStack(Blocks.GOLD_BLOCK) : new ItemStack(Items.GOLD_INGOT);
                break;
            case ItemProcessor.TYPE_PRINTED_ADVANCED:
                this.requirement = fullBlock ? new ItemStack(Blocks.DIAMOND_BLOCK) : new ItemStack(Items.DIAMOND);
                break;
            case ItemProcessor.TYPE_PRINTED_SILICON:
                if (fullBlock) {
                    throw new IllegalArgumentException("Printed silicon can't be made from block form!");
                }

                this.requirement = new ItemStack(RSItems.SILICON);
                break;
        }
    }

    @Override
    @Nonnull
    public ItemStack getRow(int row) {
        return row == 1 ? requirement : ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public ItemStack getResult() {
        return result;
    }

    @Override
    public int getDuration() {
        switch (type) {
            case ItemProcessor.TYPE_PRINTED_BASIC:
                return 100 * (fullBlock ? 9 : 1);
            case ItemProcessor.TYPE_PRINTED_IMPROVED:
                return 150 * (fullBlock ? 9 : 1);
            case ItemProcessor.TYPE_PRINTED_ADVANCED:
                return 200 * (fullBlock ? 9 : 1);
            case ItemProcessor.TYPE_PRINTED_SILICON:
                return 90 * (fullBlock ? 9 : 1);
            default:
                return 0;
        }
    }
}
