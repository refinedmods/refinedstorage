package refinedstorage.api.solderer;

import net.minecraft.item.ItemStack;

/**
 * A solderer recipe with basic behaviour
 * Implement {@link refinedstorage.api.solderer.ISoldererRecipe} for custom behaviour
 */
public class SoldererRecipeBasic implements ISoldererRecipe {
    private int duration;
    private ItemStack result;
    private ItemStack[] rows;

    /**
     * @param result   The result that this recipe gives back
     * @param duration The duration of this recipe
     * @param rows     The rows of this recipe, has to be 3 rows (null for an empty row)
     */
    public SoldererRecipeBasic(ItemStack result, int duration, ItemStack... rows) {
        if (rows.length != 3) {
            throw new IllegalArgumentException("Solderer recipe expects 3 rows, got " + rows.length + " rows");
        }

        this.duration = duration;
        this.result = result;
        this.rows = rows;
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
