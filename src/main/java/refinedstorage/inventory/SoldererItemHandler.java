package refinedstorage.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class SoldererItemHandler extends BasicItemHandler {
    private EnumFacing side;

    public SoldererItemHandler(BasicItemHandler parent, EnumFacing side) {
        super(parent.getSlots(), parent.getTile(), parent.getValidators());

        this.side = side;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (((side == EnumFacing.NORTH || side == EnumFacing.EAST) && slot == 0) ||
            ((side == EnumFacing.SOUTH || side == EnumFacing.WEST) && slot == 2) ||
            (side == EnumFacing.UP && slot == 1)) {
            return super.insertItem(slot, stack, simulate);
        }

        return stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (side == EnumFacing.DOWN && slot == 3) {
            return super.extractItem(slot, amount, simulate);
        }

        return null;
    }
}
