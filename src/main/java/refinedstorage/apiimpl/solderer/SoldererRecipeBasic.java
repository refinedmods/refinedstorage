package refinedstorage.apiimpl.solderer;

import net.minecraft.item.ItemStack;
import refinedstorage.api.solderer.ISoldererRecipe;

import javax.annotation.Nonnull;

/**
 * A solderer recipe with basic behaviour.
 * Implement {@link ISoldererRecipe} for custom behaviour.
 */
public class SoldererRecipeBasic implements ISoldererRecipe {
    private int duration;
    private ItemStack result;
    private ItemStack[] rows;

    /**
     * @param result   The result
     * @param duration The duration in ticks
     * @param rows     The rows of this recipe, has to be 3 rows (null for an empty row)
     */
    public SoldererRecipeBasic(@Nonnull ItemStack result, int duration, ItemStack... rows) {
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
