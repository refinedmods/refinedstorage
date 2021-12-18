package com.refinedmods.refinedstorage.apiimpl.storage.externalstorage;

import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorage;
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorageContext;
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorageProvider;
import com.refinedmods.refinedstorage.blockentity.FluidInterfaceBlockEntity;
import com.refinedmods.refinedstorage.util.LevelUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class FluidExternalStorageProvider implements IExternalStorageProvider<FluidStack> {
    @Override
    public boolean canProvide(BlockEntity blockEntity, Direction direction) {
        return LevelUtils.getFluidHandler(blockEntity, direction.getOpposite()) != null;
    }

    @Nonnull
    @Override
    public IExternalStorage<FluidStack> provide(IExternalStorageContext context, BlockEntity blockEntity, Direction direction) {
        return new FluidExternalStorage(context, () -> {
            if (!blockEntity.getLevel().isLoaded(blockEntity.getBlockPos())) {
                return null;
            }

            return LevelUtils.getFluidHandler(blockEntity, direction.getOpposite());
        }, blockEntity instanceof FluidInterfaceBlockEntity);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
