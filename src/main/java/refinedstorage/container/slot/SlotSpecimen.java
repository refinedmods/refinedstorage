package refinedstorage.container.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotSpecimen extends SlotItemHandler {
    private boolean sizeAllowed;

    public SlotSpecimen(IItemHandler handler, int id, int x, int y, boolean allowSize) {
        super(handler, id, x, y);

        this.sizeAllowed = allowSize;
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        return false;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return true;
    }

    @Override
    public void putStack(ItemStack stack) {
        if (stack != null && !sizeAllowed) {
            stack.stackSize = 1;
        }

        super.putStack(stack);
    }

    public boolean isSizeAllowed() {
        return sizeAllowed;
    }
}
