package com.refinedmods.refinedstorage.screen.factory;

import com.refinedmods.refinedstorage.container.CrafterManagerContainerMenu;
import com.refinedmods.refinedstorage.screen.CrafterManagerScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CrafterManagerScreenFactory implements MenuScreens.ScreenConstructor<CrafterManagerContainerMenu, CrafterManagerScreen> {
    @Override
    public CrafterManagerScreen create(CrafterManagerContainerMenu container, Inventory playerInventory, Component title) {
        CrafterManagerScreen screen = new CrafterManagerScreen(container, playerInventory, title);

        container.setScreenInfoProvider(screen);

        return screen;
    }
}
