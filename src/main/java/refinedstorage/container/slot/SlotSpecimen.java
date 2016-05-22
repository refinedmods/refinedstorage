package refinedstorage.container.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotSpecimen extends SlotItemHandler {
    public static final int SPECIMEN_SIZE = 1;
    public static final int SPECIMEN_BLOCK = 2;

    private int flags = 0;

    public SlotSpecimen(IItemHandler handler, int id, int x, int y, int flags) {
        super(handler, id, x, y);

        this.flags = flags;
    }

    public SlotSpecimen(IItemHandler handler, int id, int x, int y) {
        this(handler, id, x, y, 0);
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        return false;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return isBlockOnly() ? (stack.getItem() instanceof ItemBlock) : true;
    }

    @Override
    public void putStack(ItemStack stack) {
        if (stack != null && !isWithSize()) {
            stack.stackSize = 1;
        }

        super.putStack(stack);
    }

    public boolean isWithSize() {
        return (flags & SPECIMEN_SIZE) == SPECIMEN_SIZE;
    }

    public boolean isBlockOnly() {
        return (flags & SPECIMEN_BLOCK) == SPECIMEN_BLOCK;
    }
}
