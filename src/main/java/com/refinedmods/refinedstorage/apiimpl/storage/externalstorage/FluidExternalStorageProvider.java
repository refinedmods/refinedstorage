package com.refinedmods.refinedstorage.apiimpl.storage.externalstorage;

import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorage;
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorageContext;
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorageProvider;
import com.refinedmods.refinedstorage.tile.FluidInterfaceTile;
import com.refinedmods.refinedstorage.util.WorldUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class FluidExternalStorageProvider implements IExternalStorageProvider<FluidStack> {
    @Override
    public boolean canProvide(TileEntity tile, Direction direction) {
        return WorldUtils.getFluidHandler(tile, direction.getOpposite()) != null;
    }

    @Nonnull
    @Override
    public IExternalStorage<FluidStack> provide(IExternalStorageContext context, TileEntity tile, Direction direction) {
        return new FluidExternalStorage(context, () -> {
            if (!tile.getLevel().isLoaded(tile.getBlockPos())) {
                return null;
            }

            return WorldUtils.getFluidHandler(tile, direction.getOpposite());
        }, tile instanceof FluidInterfaceTile);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
