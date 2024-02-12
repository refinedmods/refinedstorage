package com.refinedmods.refinedstorage.container.factory;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.IContainerFactory;

public class BlockEntityContainerFactory<C extends AbstractContainerMenu, T extends BlockEntity> implements IContainerFactory<C> {
    private final Factory<C, T> factory;

    public BlockEntityContainerFactory(Factory<C, T> factory) {
        this.factory = factory;
    }

    @Override
    public C create(int windowId, Inventory inv, FriendlyByteBuf data) {
        BlockPos pos = data.readBlockPos();

        T blockEntity = (T) inv.player.level().getBlockEntity(pos);

        return factory.create(windowId, inv, blockEntity);
    }

    public interface Factory<C, T> {
        C create(int windowId, Inventory inv, T blockEntity);
    }
}
