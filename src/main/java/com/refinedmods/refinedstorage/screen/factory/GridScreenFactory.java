package com.refinedmods.refinedstorage.screen.factory;

import com.refinedmods.refinedstorage.container.GridContainerMenu;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GridScreenFactory implements MenuScreens.ScreenConstructor<GridContainerMenu, GridScreen> {
    @Override
    public GridScreen create(GridContainerMenu container, Inventory inv, Component title) {
        GridScreen screen = new GridScreen(container, container.getGrid(), inv, title);

        container.setScreenInfoProvider(screen);

        return screen;
    }
}
