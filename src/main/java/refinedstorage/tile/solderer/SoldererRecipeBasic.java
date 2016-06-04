package refinedstorage.tile.solderer;

import net.minecraft.item.ItemStack;

public class SoldererRecipeBasic implements ISoldererRecipe {
    private int duration;
    private ItemStack result;
    private ItemStack[] rows;

    public SoldererRecipeBasic(ItemStack result, int duration, ItemStack... rows) {
        this.duration = duration;
        this.result = result;
        this.rows = rows;

        if (rows.length != 3) {
            throw new IllegalArgumentException("Solderer recipe expects 3 rows, got " + rows.length + " rows");
        }
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
        return duration;
    }
}
