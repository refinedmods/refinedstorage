package refinedstorage.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class ItemHandlerFluid extends ItemHandlerBasic {
    private FluidStack[] fluids;

    public ItemHandlerFluid(int size, TileEntity tile) {
        super(size, tile, (IItemValidator) s -> s.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) && s.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).drain(Fluid.BUCKET_VOLUME, false) != null);

        this.fluids = new FluidStack[size];
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        ItemStack stack = getStackInSlot(slot);

        if (stack == null) {
            fluids[slot] = null;
        } else {
            fluids[slot] = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).drain(Fluid.BUCKET_VOLUME, false);
        }
    }

    public FluidStack[] getFluids() {
        return fluids;
    }
}
