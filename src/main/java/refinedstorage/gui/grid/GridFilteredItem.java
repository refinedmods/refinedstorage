package refinedstorage.gui.grid;

import net.minecraft.item.ItemStack;

public class GridFilteredItem {
    private ItemStack stack;
    private int compare;

    public GridFilteredItem(ItemStack stack, int compare) {
        this.stack = stack;
        this.compare = compare;
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getCompare() {
        return compare;
    }
}
