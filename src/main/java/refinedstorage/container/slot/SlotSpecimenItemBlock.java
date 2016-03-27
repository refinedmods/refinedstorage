package refinedstorage.container.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class SlotSpecimenItemBlock extends SlotSpecimen {
    public SlotSpecimenItemBlock(IInventory inventory, int id, int x, int y) {
        super(inventory, id, x, y, false);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.getItem() instanceof ItemBlock;
    }
}
