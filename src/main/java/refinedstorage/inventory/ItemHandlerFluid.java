package refinedstorage.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import refinedstorage.apiimpl.storage.fluid.FluidUtils;

public class ItemHandlerFluid extends ItemHandlerBasic {
    private FluidStack[] fluids;

    public ItemHandlerFluid(int size, TileEntity tile) {
        super(size, tile, s -> FluidUtils.getFluidFromStack(s, true) != null);

        this.fluids = new FluidStack[size];
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        ItemStack stack = getStackInSlot(slot);

        if (stack == null) {
            fluids[slot] = null;
        } else {
            fluids[slot] = FluidUtils.getFluidFromStack(stack, false);
        }
    }

    public FluidStack[] getFluids() {
        return fluids;
    }
}
