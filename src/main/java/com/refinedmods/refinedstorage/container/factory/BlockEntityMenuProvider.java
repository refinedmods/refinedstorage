package com.refinedmods.refinedstorage.container.factory;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

public class BlockEntityMenuProvider<T extends BlockEntity> implements MenuProvider {
    private final Component name;
    private final Provider<T> provider;
    private final BlockPos pos;

    public BlockEntityMenuProvider(Component name, Provider<T> provider, BlockPos pos) {
        this.name = name;
        this.provider = provider;
        this.pos = pos;
    }

    @Override
    public Component getDisplayName() {
        return name;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
        T blockEntity = (T) player.level().getBlockEntity(pos);

        return provider.create(blockEntity, windowId, inventory, player);
    }

    public interface Provider<T> {
        AbstractContainerMenu create(T blockEntity, int windowId, Inventory inventory, Player player);
    }
}
