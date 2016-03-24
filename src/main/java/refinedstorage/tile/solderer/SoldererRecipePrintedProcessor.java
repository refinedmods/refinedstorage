package refinedstorage.tile.solderer;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageItems;
import refinedstorage.item.ItemProcessor;

public class SoldererRecipePrintedProcessor implements ISoldererRecipe {
    private int type;

    public SoldererRecipePrintedProcessor(int type) {
        this.type = type;
    }

    @Override
    public ItemStack getRow(int row) {
        if (row == 1) {
            switch (type) {
                case ItemProcessor.TYPE_PRINTED_BASIC:
                    return new ItemStack(Items.iron_ingot);
                case ItemProcessor.TYPE_PRINTED_IMPROVED:
                    return new ItemStack(Items.gold_ingot);
                case ItemProcessor.TYPE_PRINTED_ADVANCED:
                    return new ItemStack(Items.diamond);
                case ItemProcessor.TYPE_PRINTED_SILICON:
                    return new ItemStack(RefinedStorageItems.SILICON);
            }
        }

        return null;
    }

    @Override
    public ItemStack getResult() {
        return new ItemStack(RefinedStorageItems.PROCESSOR, 1, type);
    }

    @Override
    public int getDuration() {
        switch (type) {
            case ItemProcessor.TYPE_PRINTED_BASIC:
                return 100;
            case ItemProcessor.TYPE_PRINTED_IMPROVED:
                return 150;
            case ItemProcessor.TYPE_PRINTED_ADVANCED:
                return 200;
            case ItemProcessor.TYPE_PRINTED_SILICON:
                return 90;
        }

        return 0;
    }
}
