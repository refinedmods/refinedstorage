package refinedstorage.apiimpl.storage.fluid;

import net.minecraftforge.fluids.FluidStack;

public final class FluidUtils {
    public static FluidStack copyStackWithSize(FluidStack stack, int size) {
        FluidStack copy = stack.copy();
        copy.amount = size;
        return copy;
    }
}
