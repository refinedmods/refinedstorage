package refinedstorage.tile.externalstorage;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.api.storage.fluid.IFluidStorage;
import refinedstorage.apiimpl.storage.fluid.FluidUtils;
import refinedstorage.tile.config.IFilterable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class FluidStorageExternal implements IFluidStorage {
    private FluidStack cache;

    private TileExternalStorage externalStorage;
    private IFluidHandler handler;

    public FluidStorageExternal(TileExternalStorage externalStorage, IFluidHandler handler, IFluidTankProperties properties) {
        this.externalStorage = externalStorage;
        this.handler = handler;
    }

    public IFluidTankProperties getProperties() {
        return handler.getTankProperties().length != 0 ? handler.getTankProperties()[0] : null;
    }

    public FluidStack getContents() {
        return getProperties() == null ? null : getProperties().getContents();
    }

    @Override
    public List<FluidStack> getStacks() {
        return getContents() == null ? Collections.emptyList() : Collections.singletonList(getContents().copy());
    }

    @Nullable
    @Override
    public FluidStack insertFluid(@Nonnull FluidStack stack, int size, boolean simulate) {
        if (getProperties() != null && IFilterable.canTakeFluids(externalStorage.getFluidFilters(), externalStorage.getMode(), externalStorage.getCompare(), stack) && getProperties().canFillFluidType(stack)) {
            int filled = handler.fill(FluidUtils.copyStackWithSize(stack, size), !simulate);

            if (filled == size) {
                return null;
            }

            return FluidUtils.copyStackWithSize(stack, size - filled);
        }

        return FluidUtils.copyStackWithSize(stack, size);
    }

    @Nullable
    @Override
    public FluidStack extractFluid(@Nonnull FluidStack stack, int size, int flags) {
        FluidStack toDrain = FluidUtils.copyStackWithSize(stack, size);

        if (CompareUtils.compareStack(getContents(), toDrain, flags)) {
            return handler.drain(toDrain, true);
        }

        return null;
    }

    @Override
    public int getStored() {
        return getContents() != null ? getContents().amount : 0;
    }

    public int getCapacity() {
        return getProperties() != null ? getProperties().getCapacity() : 0;
    }

    @Override
    public int getPriority() {
        return externalStorage.getPriority();
    }

    public boolean updateCache() {
        FluidStack stack = getContents();

        if (cache == null) {
            cache = FluidUtils.copy(stack);
        } else if (!CompareUtils.compareStack(stack, cache, CompareUtils.COMPARE_NBT | CompareUtils.COMPARE_QUANTITY)) {
            cache = FluidUtils.copy(stack);

            return true;
        }

        return false;
    }

    public void updateCacheForcefully() {
        cache = FluidUtils.copy(getContents());
    }
}
