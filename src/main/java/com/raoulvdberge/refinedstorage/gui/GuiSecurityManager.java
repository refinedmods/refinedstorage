package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.api.network.Permission;
import com.raoulvdberge.refinedstorage.container.ContainerSecurityManager;
import com.raoulvdberge.refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import com.raoulvdberge.refinedstorage.tile.TileSecurityManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiSecurityManager extends GuiBase {
    private GuiCheckBox[] permissions = new GuiCheckBox[Permission.values().length];

    public GuiSecurityManager(ContainerSecurityManager container) {
        super(container, 176, 234);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileSecurityManager.REDSTONE_MODE));

        int padding = 15;

        permissions[0] = addCheckBox(x + 7, y + 93, I18n.format("gui.refinedstorage:security_manager.permission.0"), false);
        permissions[1] = addCheckBox(permissions[0].xPosition, permissions[0].yPosition + padding, I18n.format("gui.refinedstorage:security_manager.permission.1"), false);
        permissions[2] = addCheckBox(permissions[1].xPosition, permissions[1].yPosition + padding, I18n.format("gui.refinedstorage:security_manager.permission.2"), false);
        permissions[3] = addCheckBox(permissions[0].xPosition + 80, permissions[0].yPosition, I18n.format("gui.refinedstorage:security_manager.permission.3"), false);
        permissions[4] = addCheckBox(permissions[3].xPosition, permissions[3].yPosition + padding, I18n.format("gui.refinedstorage:security_manager.permission.4"), false);
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
        drawString(7, 140, t("container.inventory"));

        for (int i = 0; i < permissions.length; ++i) {
            GuiCheckBox permission = permissions[i];

            if (inBounds(permission.xPosition - guiLeft, permission.yPosition - guiTop, permission.width, permission.height, mouseX, mouseY)) {
                drawTooltip(mouseX, mouseY, I18n.format("gui.refinedstorage:security_manager.permission." + i + ".tooltip"));
            }
        }
    }
}
