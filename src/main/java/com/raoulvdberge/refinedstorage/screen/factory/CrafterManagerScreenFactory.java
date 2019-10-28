package com.raoulvdberge.refinedstorage.screen.factory;

import com.raoulvdberge.refinedstorage.container.CrafterManagerContainer;
import com.raoulvdberge.refinedstorage.screen.CrafterManagerScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class CrafterManagerScreenFactory implements ScreenManager.IScreenFactory<CrafterManagerContainer, CrafterManagerScreen> {
    @Override
    public CrafterManagerScreen create(CrafterManagerContainer container, PlayerInventory playerInventory, ITextComponent title) {
        CrafterManagerScreen screen = new CrafterManagerScreen(container, playerInventory, title);

        container.setScreenInfoProvider(screen);

        return screen;
    }
}
