package refinedstorage.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class InterfaceItemHandler extends ProxyItemHandler {
    private EnumFacing side;

    public InterfaceItemHandler(BasicItemHandler interfaceHandler, EnumFacing side) {
        super(interfaceHandler);

        this.side = side;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (side != EnumFacing.DOWN && slot >= 0 && slot <= 8) {
            return super.insertItem(slot, stack, simulate);
        }

        return stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (side == EnumFacing.DOWN && slot >= 18 && slot <= 26) {
            return super.extractItem(slot, amount, simulate);
        }

        return null;
    }
}
