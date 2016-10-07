package refinedstorage.apiimpl.solderer;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import refinedstorage.RSItems;
import refinedstorage.api.solderer.ISoldererRecipe;
import refinedstorage.item.ItemProcessor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SoldererRecipePrintedProcessor implements ISoldererRecipe {
    private int type;
    private ItemStack requirement;
    private ItemStack result;

    public SoldererRecipePrintedProcessor(int type) {
        this.type = type;
        this.result = new ItemStack(RSItems.PROCESSOR, 1, type);

        switch (type) {
            case ItemProcessor.TYPE_PRINTED_BASIC:
                this.requirement = new ItemStack(Items.IRON_INGOT);
                break;
            case ItemProcessor.TYPE_PRINTED_IMPROVED:
                this.requirement = new ItemStack(Items.GOLD_INGOT);
                break;
            case ItemProcessor.TYPE_PRINTED_ADVANCED:
                this.requirement = new ItemStack(Items.DIAMOND);
                break;
            case ItemProcessor.TYPE_PRINTED_SILICON:
                this.requirement = new ItemStack(RSItems.SILICON);
                break;
        }
    }

    @Override
    @Nullable
    public ItemStack getRow(int row) {
        if (row == 1) {
            return requirement;
        }

        return null;
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
                return 100;
            case ItemProcessor.TYPE_PRINTED_IMPROVED:
                return 150;
            case ItemProcessor.TYPE_PRINTED_ADVANCED:
                return 200;
            case ItemProcessor.TYPE_PRINTED_SILICON:
                return 90;
            default:
                return 0;
        }
    }
}
