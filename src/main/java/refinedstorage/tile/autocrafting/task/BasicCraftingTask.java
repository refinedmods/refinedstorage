package refinedstorage.tile.autocrafting.task;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import refinedstorage.tile.TileController;
import refinedstorage.tile.autocrafting.CraftingPattern;

import java.util.ArrayList;
import java.util.List;

public class BasicCraftingTask implements ICraftingTask {
    private CraftingPattern pattern;
    private boolean satisfied[];
    private boolean checked[];
    private boolean childTasks[];
    private List<ItemStack> itemsTook = new ArrayList<ItemStack>();

    public BasicCraftingTask(CraftingPattern pattern) {
        this.pattern = pattern;
        this.satisfied = new boolean[pattern.getInputs().length];
        this.checked = new boolean[pattern.getInputs().length];
        this.childTasks = new boolean[pattern.getInputs().length];
    }

    public CraftingPattern getPattern() {
        return pattern;
    }

    public boolean update(TileController controller) {
        boolean done = true;

        for (int i = 0; i < pattern.getInputs().length; ++i) {
            checked[i] = true;

            ItemStack input = pattern.getInputs()[i];

            if (!satisfied[i]) {
                done = false;

                ItemStack took = controller.take(input.copy());

                if (took != null) {
                    itemsTook.add(took);

                    satisfied[i] = true;
                } else if (!childTasks[i]) {
                    CraftingPattern pattern = controller.getPattern(input);

                    if (pattern != null) {
                        controller.addCraftingTask(pattern);

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

    @Override
    public void onCancelled(TileController controller) {
        for (ItemStack took : itemsTook) {
            controller.push(took);
        }
    }

    @Override
    public String getInfo() {
        StringBuilder builder = new StringBuilder();

        builder.append(TextFormatting.YELLOW).append("{missing_items}").append(TextFormatting.RESET).append("\n");

        int missingItems = 0;

        for (int i = 0; i < pattern.getInputs().length; ++i) {
            ItemStack input = pattern.getInputs()[i];

            if (checked[i] && !satisfied[i] && !childTasks[i]) {
                builder.append("- ").append(input.getDisplayName()).append("\n");

                missingItems++;
            }
        }

        if (missingItems == 0) {
            builder.append(TextFormatting.GRAY).append(TextFormatting.ITALIC).append("{none}").append(TextFormatting.RESET).append("\n");
        }

        builder.append(TextFormatting.YELLOW).append("{items_crafting}").append(TextFormatting.RESET).append("\n");

        int itemsCrafting = 0;

        for (int i = 0; i < pattern.getInputs().length; ++i) {
            ItemStack input = pattern.getInputs()[i];

            if (!satisfied[i] && childTasks[i]) {
                builder.append("- ").append(input.getDisplayName()).append("\n");

                itemsCrafting++;
            }
        }

        if (itemsCrafting == 0) {
            builder.append(TextFormatting.GRAY).append(TextFormatting.ITALIC).append("{none}").append(TextFormatting.RESET).append("\n");
        }

        return builder.toString();
    }
}
