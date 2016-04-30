package refinedstorage.tile.autocrafting;

import net.minecraft.item.ItemStack;
import refinedstorage.item.ItemPattern;
import refinedstorage.tile.TileController;

import java.util.ArrayList;
import java.util.List;

public class CraftingTask {
    private CraftingPattern pattern;
    private boolean satisfied[];
    private boolean childTasks[];

    public CraftingTask(CraftingPattern pattern) {
        this.pattern = pattern;
        this.satisfied = new boolean[pattern.getIngredients().length];
        this.childTasks = new boolean[pattern.getIngredients().length];
    }

    public CraftingPattern getPattern() {
        return pattern;
    }

    public boolean update(TileController controller) {
        boolean done = true;

        for (int i = 0; i < pattern.getIngredients().length; ++i) {
            ItemStack ingredient = pattern.getIngredients()[i];

            if (!satisfied[i]) {
                done = false;

                ItemStack took = controller.take(ingredient.copy());

                if (took != null) {
                    satisfied[i] = true;
                } else if (!childTasks[i]) {
                    CraftingPattern pattern = controller.getPatternForItem(ingredient);

                    if (pattern != null) {
                        controller.addCraftingTask(new CraftingTask(pattern));

                        childTasks[i] = true;

                        break;
                    }
                } else {
                    break;
                }
            }
        }

        return done;
    }
}
