package refinedstorage.inventory;

import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageItems;
import refinedstorage.tile.TileBase;

import java.util.List;

public class GridFilterInGridItemHandler extends BasicItemHandler {
    private List<ItemStack> filteredItems;

    public GridFilterInGridItemHandler(List<ItemStack> filteredItems) {
        super(1, new BasicItemValidator(RefinedStorageItems.GRID_FILTER));

        this.filteredItems = filteredItems;
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        filteredItems.clear();

        ItemStack stack = getStackInSlot(slot);

        if (stack != null && stack.hasTagCompound()) {
            BasicItemHandler items = new BasicItemHandler(9 * 3);

            TileBase.readItems(items, 0, stack.getTagCompound());

            for (int i = 0; i < items.getSlots(); ++i) {
                ItemStack item = items.getStackInSlot(i);

                if (item != null) {
                    filteredItems.add(item);
                }
            }
        }
    }
}
