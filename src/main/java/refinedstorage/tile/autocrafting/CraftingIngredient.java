package refinedstorage.tile.autocrafting;

import net.minecraft.item.ItemStack;

public class CraftingIngredient {
    private ItemStack stack;
    private boolean satisfied;
    private boolean subtaskCreated;

    public CraftingIngredient(ItemStack stack) {
        this.stack = stack;
    }

    public ItemStack getStack() {
        return stack;
    }

    public boolean isSatisfied() {
        return satisfied;
    }

    public void setSatisfied() {
        this.satisfied = true;
    }

    public boolean isSubtaskCreated() {
        return subtaskCreated;
    }

    public void setSubtaskCreated() {
        subtaskCreated = true;
    }
}
