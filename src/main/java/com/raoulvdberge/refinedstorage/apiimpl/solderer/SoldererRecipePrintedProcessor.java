package com.raoulvdberge.refinedstorage.apiimpl.solderer;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.solderer.ISoldererRecipe;
import com.raoulvdberge.refinedstorage.item.ItemProcessor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;

public class SoldererRecipePrintedProcessor implements ISoldererRecipe {
    private int type;
    private boolean fullBlock;
    private ItemStack result;
    private NonNullList<ItemStack> requirement;

    public SoldererRecipePrintedProcessor(int type, boolean fullBlock) {
        this.type = type;
        this.fullBlock = fullBlock;
        this.result = new ItemStack(RSItems.PROCESSOR, fullBlock ? 9 : 1, type);

        switch (type) {
            case ItemProcessor.TYPE_PRINTED_BASIC:
                this.requirement = fullBlock ? OreDictionary.getOres("blockIron") : OreDictionary.getOres("ingotIron");
                break;
            case ItemProcessor.TYPE_PRINTED_IMPROVED:
                this.requirement = fullBlock ? OreDictionary.getOres("blockGold") : OreDictionary.getOres("ingotGold");
                break;
            case ItemProcessor.TYPE_PRINTED_ADVANCED:
                this.requirement = fullBlock ? OreDictionary.getOres("blockDiamond") : OreDictionary.getOres("gemDiamond");
                break;
            case ItemProcessor.TYPE_PRINTED_SILICON:
                if (fullBlock) {
                    throw new IllegalArgumentException("Printed silicon can't be made from block form!");
                }

                this.requirement = OreDictionary.getOres("itemSilicon");
                break;
        }
    }

    @Override
    @Nonnull
    public NonNullList<ItemStack> getRow(int row) {
        return row == 1 ? requirement : RSUtils.emptyNonNullList();
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
                return 100 * (fullBlock ? 6 : 1);
            case ItemProcessor.TYPE_PRINTED_IMPROVED:
                return 150 * (fullBlock ? 6 : 1);
            case ItemProcessor.TYPE_PRINTED_ADVANCED:
                return 200 * (fullBlock ? 6 : 1);
            case ItemProcessor.TYPE_PRINTED_SILICON:
                return 90 * (fullBlock ? 6 : 1);
            default:
                return 0;
        }
    }
}
