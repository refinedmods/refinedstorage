package com.refinedmods.refinedstorage.container.factory;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.container.GridContainer;
import com.refinedmods.refinedstorage.screen.EmptyScreenInfoProvider;
import com.refinedmods.refinedstorage.tile.BaseTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public class GridContainerProvider implements INamedContainerProvider {
    private final IGrid grid;
    private final TileEntity tile;

    public GridContainerProvider(IGrid grid, TileEntity tile) {
        this.grid = grid;
        this.tile = tile;
    }

    @Override
    public ITextComponent getDisplayName() {
        return grid.getTitle();
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
        GridContainer c = new GridContainer(grid, tile instanceof BaseTile ? (BaseTile) tile : null, player, windowId);

        c.setScreenInfoProvider(new EmptyScreenInfoProvider());
        c.initSlots();

        return c;
    }
}
