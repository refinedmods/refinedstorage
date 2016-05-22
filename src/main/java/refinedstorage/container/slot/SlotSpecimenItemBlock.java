package refinedstorage.container.slot;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

// @TODO: This can probably be removed?
public class SlotSpecimenItemBlock extends SlotSpecimen {
    public SlotSpecimenItemBlock(IItemHandler handler, int id, int x, int y) {
        super(handler, id, x, y, false);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.getItem() instanceof ItemBlock;
    }
}
