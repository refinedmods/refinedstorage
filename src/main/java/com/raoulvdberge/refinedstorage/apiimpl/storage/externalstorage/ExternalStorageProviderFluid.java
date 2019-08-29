package com.raoulvdberge.refinedstorage.apiimpl.storage.externalstorage;

import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IExternalStorageContext;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IExternalStorageProvider;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IStorageExternal;
import com.raoulvdberge.refinedstorage.tile.TileFluidInterface;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class ExternalStorageProviderFluid implements IExternalStorageProvider<FluidStack> {
    @Override
    public boolean canProvide(TileEntity tile, Direction direction) {
        return WorldUtils.getFluidHandler(tile, direction.getOpposite()) != null;
    }

    @Nonnull
    @Override
    public IStorageExternal<FluidStack> provide(IExternalStorageContext context, Supplier<TileEntity> tile, Direction direction) {
        return new StorageExternalFluid(context, () -> WorldUtils.getFluidHandler(tile.get(), direction.getOpposite()), tile.get() instanceof TileFluidInterface);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
