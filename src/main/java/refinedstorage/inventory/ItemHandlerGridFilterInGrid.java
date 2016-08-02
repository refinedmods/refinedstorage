package refinedstorage.inventory;

import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageItems;

import java.util.List;

public class ItemHandlerGridFilterInGrid extends ItemHandlerBasic {
    private List<ItemStack> filteredItems;

    public ItemHandlerGridFilterInGrid(List<ItemStack> filteredItems) {
        super(4, new ItemValidatorBasic(RefinedStorageItems.GRID_FILTER));

        this.filteredItems = filteredItems;
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        filteredItems.clear();

        for (int i = 0; i < getSlots(); ++i) {
            ItemStack stack = getStackInSlot(i);

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
}
