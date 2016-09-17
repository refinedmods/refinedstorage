package refinedstorage.api.solderer;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A basic solderer recipe.
 * Implement {@link ISoldererRecipe} for custom behavior.
 */
public class SoldererRecipe implements ISoldererRecipe {
    private int duration;
    private ItemStack result;
    private ItemStack[] rows;

    /**
     * @param result   the result
     * @param duration the duration in ticks
     * @param rows     the rows of this recipe, has to be 3 rows (null for an empty row)
     */
    public SoldererRecipe(@Nonnull ItemStack result, int duration, ItemStack... rows) {
        if (rows.length != 3) {
            throw new IllegalArgumentException("Solderer recipe expects 3 rows, got " + rows.length + " rows");
        }

        this.duration = duration;
        this.result = result;
        this.rows = rows;
    }

    /**
     * @param row the row in the solderer that we want the stack for (between 0 - 2)
     * @return a stack for that row, null if there is no stack
     */
    @Override
    @Nullable
    public ItemStack getRow(int row) {
        return rows[row];
    }

    /**
     * @return the result stack
     */
    @Override
    @Nonnull
    public ItemStack getResult() {
        return result;
    }

    /**
     * @return the time it takes to complete this recipe
     */
    @Override
    public int getDuration() {
        return duration;
    }
}
