package refinedstorage.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class InterfaceItemHandler extends ItemStackHandler {
    private BasicItemHandler importItems;
    private BasicItemHandler exportItems;

    public InterfaceItemHandler(BasicItemHandler importItems, BasicItemHandler exportItems) {
        this.importItems = importItems;
        this.exportItems = exportItems;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return importItems.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return exportItems.extractItem(slot, amount, simulate);
    }
}
