package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.container.ContainerSecurityManager;

public class GuiSecurityManager extends GuiBase {
    public GuiSecurityManager(ContainerSecurityManager container) {
        super(container, 176, 209);
    }

    @Override
    public void init(int x, int y) {
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/security_manager.png");

        drawTexture(x, y, 0, 0, screenWidth, screenHeight);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:security_manager"));
        drawString(7, 59, t("gui.refinedstorage:security_manager.configure"));
        drawString(7, 115, t("container.inventory"));
    }
}
