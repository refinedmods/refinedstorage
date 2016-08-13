package refinedstorage.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class ItemHandlerFluidFilter extends ItemHandlerBasic {
    private FluidStack[] filters = new FluidStack[9];

    public ItemHandlerFluidFilter(TileEntity tile) {
        super(9, tile, (IItemValidator) s -> s.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) && s.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).drain(Fluid.BUCKET_VOLUME, false) != null);
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        ItemStack stack = getStackInSlot(slot);

        if (stack == null) {
            filters[slot] = null;
        } else {
            filters[slot] = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).drain(Fluid.BUCKET_VOLUME, false);
        }
    }

    public FluidStack[] getFilters() {
        return filters;
    }
}
