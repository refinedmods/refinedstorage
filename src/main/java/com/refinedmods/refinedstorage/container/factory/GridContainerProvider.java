package com.refinedmods.refinedstorage.container.factory;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.container.GridContainer;
import com.refinedmods.refinedstorage.screen.EmptyScreenInfoProvider;
import com.refinedmods.refinedstorage.tile.BaseTile;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

public class GridContainerProvider implements MenuProvider {
    private final IGrid grid;
    private final BlockEntity tile;

    public GridContainerProvider(IGrid grid, BlockEntity tile) {
        this.grid = grid;
        this.tile = tile;
    }

    @Override
    public Component getDisplayName() {
        return grid.getTitle();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
        GridContainer c = new GridContainer(grid, tile instanceof BaseTile ? (BaseTile) tile : null, player, windowId);

        c.setScreenInfoProvider(new EmptyScreenInfoProvider());
        c.initSlots();

        return c;
    }
}
