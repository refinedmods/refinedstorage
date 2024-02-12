package com.refinedmods.refinedstorage.apiimpl.storage.externalstorage;

import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorage;
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorageContext;
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorageProvider;
import com.refinedmods.refinedstorage.blockentity.FluidInterfaceBlockEntity;
import com.refinedmods.refinedstorage.util.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import javax.annotation.Nonnull;

public class FluidExternalStorageProvider implements IExternalStorageProvider<FluidStack> {
    @Override
    public boolean canProvide(Level level, BlockPos pos, Direction direction) {
        return LevelUtils.getFluidHandler(level, pos, direction.getOpposite()) != null;
    }

    @Nonnull
    @Override
    public IExternalStorage<FluidStack> provide(IExternalStorageContext context, BlockEntity blockEntity, Direction direction) {
        return new FluidExternalStorage(context, () -> {
            Level level = blockEntity.getLevel();
            if (level == null) {
                return null;
            }
            return LevelUtils.getFluidHandler(level, blockEntity.getBlockPos(), direction.getOpposite());
        }, blockEntity instanceof FluidInterfaceBlockEntity);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
