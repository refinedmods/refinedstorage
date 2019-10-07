package com.raoulvdberge.refinedstorage.screen.factory;

import com.raoulvdberge.refinedstorage.container.GridContainer;
import com.raoulvdberge.refinedstorage.screen.grid.GridScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class GridScreenFactory implements ScreenManager.IScreenFactory<GridContainer, GridScreen> {
    @Override
    public GridScreen create(GridContainer container, PlayerInventory inv, ITextComponent title) {
        GridScreen grid = new GridScreen(container, container.getGrid(), inv, title);

        container.setScreenInfoProvider(grid);

        return grid;
    }
}
