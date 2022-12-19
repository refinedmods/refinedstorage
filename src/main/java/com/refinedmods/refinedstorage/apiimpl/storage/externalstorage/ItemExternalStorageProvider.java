package com.refinedmods.refinedstorage.apiimpl.storage.externalstorage;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.storage.IStorageProvider;
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorage;
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorageContext;
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorageProvider;
import com.refinedmods.refinedstorage.blockentity.InterfaceBlockEntity;
import com.refinedmods.refinedstorage.util.LevelUtils;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;

public class ItemExternalStorageProvider implements IExternalStorageProvider<ItemStack> {
    @Override
    public boolean canProvide(BlockEntity blockEntity, Direction direction) {
        INetworkNode node = NetworkUtils.getNodeFromBlockEntity(blockEntity);

        if (node instanceof IStorageProvider) {
            return false;
        }

        return LevelUtils.getItemHandler(blockEntity, direction.getOpposite()) != null;
    }

    @Nonnull
    @Override
    public IExternalStorage<ItemStack> provide(IExternalStorageContext context, BlockEntity blockEntity, Direction direction) {
        return new ItemExternalStorage(context, () -> {
            Level level = blockEntity.getLevel();

            if (level == null) {
                return null;
            }

            BlockPos blockPos = blockEntity.getBlockPos();

            if (!level.isLoaded(blockPos)) {
                return null;
            }

            BlockEntity currentBlockEntity = level.getBlockEntity(blockPos);

            return LevelUtils.getItemHandler(currentBlockEntity, direction.getOpposite());
        }, blockEntity instanceof InterfaceBlockEntity);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
