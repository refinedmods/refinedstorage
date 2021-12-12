package com.refinedmods.refinedstorage.container.factory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public class PositionalTileContainerProvider<T extends TileEntity> implements INamedContainerProvider {
    public interface Provider<T> {
        Container create(T tile, int windowId, PlayerInventory inventory, PlayerEntity player);
    }

    private final ITextComponent name;
    private final Provider<T> provider;
    private final BlockPos pos;

    public PositionalTileContainerProvider(ITextComponent name, Provider<T> provider, BlockPos pos) {
        this.name = name;
        this.provider = provider;
        this.pos = pos;
    }

    @Override
    public ITextComponent getDisplayName() {
        return name;
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
        T tile = (T) player.level.getBlockEntity(pos);

        return provider.create(tile, windowId, inventory, player);
    }
}
