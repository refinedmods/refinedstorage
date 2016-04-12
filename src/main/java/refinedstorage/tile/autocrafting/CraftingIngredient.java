package refinedstorage.tile.autocrafting;

import net.minecraft.item.ItemStack;

public class CraftingIngredient {
    private ItemStack stack;
    private boolean satisfied;
    private boolean childTaskCreated;

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

    public boolean isChildTaskCreated() {
        return childTaskCreated;
    }

    public void setChildTaskCreated() {
        childTaskCreated = true;
    }
}
