package refinedstorage.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.RSUtils;

public class ItemHandlerFluid extends ItemHandlerBasic {
    private FluidStack[] fluids;

    public ItemHandlerFluid(int size, TileEntity tile) {
        super(size, tile, s -> RSUtils.getFluidFromStack(ItemHandlerHelper.copyStackWithSize(s, 1), true) != null);

        this.fluids = new FluidStack[size];
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        ItemStack stack = getStackInSlot(slot);

        if (stack == null) {
            fluids[slot] = null;
        } else {
            fluids[slot] = RSUtils.getFluidFromStack(ItemHandlerHelper.copyStackWithSize(stack, 1), true);
        }
    }

    public FluidStack getFluidStackInSlot(int slot) {
        return fluids[slot];
    }

    public FluidStack[] getFluids() {
        return fluids;
    }
}
