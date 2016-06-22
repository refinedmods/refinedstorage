package refinedstorage.apiimpl.solderer;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageItems;
import refinedstorage.api.solderer.ISoldererRecipe;
import refinedstorage.item.ItemProcessor;

public class SoldererRecipeProcessor implements ISoldererRecipe {
    private int type;
    private ItemStack[] rows;
    private ItemStack result;

    public SoldererRecipeProcessor(int type) {
        this.type = type;

        ItemStack printedProcessor = null;

        switch (type) {
            case ItemProcessor.TYPE_BASIC:
                printedProcessor = new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_PRINTED_BASIC);
                break;
            case ItemProcessor.TYPE_IMPROVED:
                printedProcessor = new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_PRINTED_IMPROVED);
                break;
            case ItemProcessor.TYPE_ADVANCED:
                printedProcessor = new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_PRINTED_ADVANCED);
                break;
        }

        this.result = new ItemStack(RefinedStorageItems.PROCESSOR, 1, type);
        this.rows = new ItemStack[]{
            printedProcessor,
            new ItemStack(Items.REDSTONE),
            new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_PRINTED_SILICON)
        };
    }

    @Override
    public ItemStack getRow(int row) {
        return rows[row];
    }

    @Override
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
