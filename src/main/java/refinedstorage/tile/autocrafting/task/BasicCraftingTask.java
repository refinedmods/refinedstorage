package refinedstorage.tile.autocrafting.task;

import net.minecraft.item.ItemStack;
import refinedstorage.tile.TileController;
import refinedstorage.tile.autocrafting.CraftingPattern;

public class BasicCraftingTask implements ICraftingTask {
    private CraftingPattern pattern;
    private boolean satisfied[];
    private boolean childTasks[];

    public BasicCraftingTask(CraftingPattern pattern) {
        this.pattern = pattern;
        this.satisfied = new boolean[pattern.getInputs().length];
        this.childTasks = new boolean[pattern.getInputs().length];
    }

    public CraftingPattern getPattern() {
        return pattern;
    }

    public boolean update(TileController controller) {
        boolean done = true;

        for (int i = 0; i < pattern.getInputs().length; ++i) {
            ItemStack input = pattern.getInputs()[i];

            if (!satisfied[i]) {
                done = false;

                ItemStack took = controller.take(input.copy());

                if (took != null) {
                    satisfied[i] = true;
                } else if (!childTasks[i]) {
                    CraftingPattern pattern = controller.getPatternForItem(input);

                    if (pattern != null) {
                        controller.addCraftingTask(new BasicCraftingTask(pattern));

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

    @Override
    public void onDone(TileController controller) {
        for (ItemStack output : pattern.getOutputs()) {
            controller.push(output);
        }
    }
}
