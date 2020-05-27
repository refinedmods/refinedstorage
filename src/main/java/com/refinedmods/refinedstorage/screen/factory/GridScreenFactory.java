package com.refinedmods.refinedstorage.screen.factory;

import com.refinedmods.refinedstorage.container.GridContainer;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class GridScreenFactory implements ScreenManager.IScreenFactory<GridContainer, GridScreen> {
    @Override
    public GridScreen create(GridContainer container, PlayerInventory inv, ITextComponent title) {
        GridScreen screen = new GridScreen(container, container.getGrid(), inv, title);

        container.setScreenInfoProvider(screen);

        return screen;
    }
}
