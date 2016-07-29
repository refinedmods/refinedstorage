package refinedstorage.inventory;

import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageItems;

import java.util.List;

public class ItemHandlerGridFilterInGrid extends ItemHandlerBasic {
    private List<ItemStack> filteredItems;

    public ItemHandlerGridFilterInGrid(List<ItemStack> filteredItems) {
        super(1, new ItemValidatorBasic(RefinedStorageItems.GRID_FILTER));

        this.filteredItems = filteredItems;
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        filteredItems.clear();

        ItemStack stack = getStackInSlot(slot);

        if (stack != null) {
            ItemHandlerGridFilter items = new ItemHandlerGridFilter(stack);

            for (ItemStack item : items.getFilteredItems()) {
                if (item != null) {
                    filteredItems.add(item);
                }
            }
        }
    }
}
