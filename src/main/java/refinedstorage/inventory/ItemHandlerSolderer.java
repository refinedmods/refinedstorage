package refinedstorage.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import refinedstorage.tile.TileSolderer;

public class ItemHandlerSolderer extends ItemHandlerProxy {
    private TileSolderer solderer;
    private EnumFacing side;

    public ItemHandlerSolderer(TileSolderer solderer, EnumFacing side) {
        super(solderer.getItems());

        this.solderer = solderer;
        this.side = side;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (solderer.getDirection() == EnumFacing.DOWN || solderer.getDirection() == EnumFacing.UP) {
            if (((side == EnumFacing.WEST || side == EnumFacing.SOUTH) && slot == 0) || ((side == EnumFacing.NORTH || side == EnumFacing.EAST) && slot == 2) || (side == EnumFacing.UP && slot == 1)) {
                return super.insertItem(slot, stack, simulate);
            }
        } else {
            if ((side == solderer.getDirection().rotateY() && slot == 0) || (side == solderer.getDirection().rotateYCCW() && slot == 2) || (side == EnumFacing.UP && slot == 1)) {
                return super.insertItem(slot, stack, simulate);
            }
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
