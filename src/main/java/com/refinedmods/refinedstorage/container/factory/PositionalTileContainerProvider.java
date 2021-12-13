package com.refinedmods.refinedstorage.container.factory;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

public class PositionalTileContainerProvider<T extends BlockEntity> implements MenuProvider {
    private final Component name;
    private final Provider<T> provider;
    private final BlockPos pos;

    public PositionalTileContainerProvider(Component name, Provider<T> provider, BlockPos pos) {
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
        T tile = (T) player.level.getBlockEntity(pos);

        return provider.create(tile, windowId, inventory, player);
    }

    public interface Provider<T> {
        AbstractContainerMenu create(T tile, int windowId, Inventory inventory, Player player);
    }
}
