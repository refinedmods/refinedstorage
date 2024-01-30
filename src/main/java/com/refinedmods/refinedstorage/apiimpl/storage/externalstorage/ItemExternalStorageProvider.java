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
    public boolean canProvide(Level level, BlockPos pos, Direction direction) {
        INetworkNode node = NetworkUtils.getNodeFromBlockEntity(level.getBlockEntity(pos));
        if (node instanceof IStorageProvider) {
            return false;
        }
        return LevelUtils.getItemHandler(level, pos, direction.getOpposite()) != null;
    }

    @Nonnull
    @Override
    public IExternalStorage<ItemStack> provide(IExternalStorageContext context, BlockEntity blockEntity, Direction direction) {
        return new ItemExternalStorage(context, () -> {
            Level level = blockEntity.getLevel();
            if (level == null) {
                return null;
            }
            return LevelUtils.getItemHandler(level, blockEntity.getBlockPos(), direction.getOpposite());
        }, blockEntity instanceof InterfaceBlockEntity);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
