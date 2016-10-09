package refinedstorage.apiimpl.storage.fluid;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import refinedstorage.api.network.INetworkMaster;

// @TODO: Move to RSUtils
public final class FluidUtils {
    public static final ItemStack EMPTY_BUCKET = new ItemStack(Items.BUCKET);

    public static FluidStack copyStackWithSize(FluidStack stack, int size) {
        FluidStack copy = stack.copy();
        copy.amount = size;
        return copy;
    }

    public static FluidStack copy(FluidStack stack) {
        return stack == null ? null : stack.copy();
    }

    public static ItemStack extractBucket(INetworkMaster network) {
        return network.extractItem(EMPTY_BUCKET, 1);
    }
}
