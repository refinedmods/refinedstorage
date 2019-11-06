package com.raoulvdberge.refinedstorage.screen;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.container.InterfaceContainer;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.ExactModeSideButton;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.raoulvdberge.refinedstorage.tile.InterfaceTile;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class InterfaceScreen extends BaseScreen<InterfaceContainer> {
    public InterfaceScreen(InterfaceContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, 211, 217, inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, InterfaceTile.REDSTONE_MODE));

        addSideButton(new ExactModeSideButton(this, InterfaceTile.COMPARE));
    }

    @Override
    public void tick(int x, int y) {
    }

    @Override
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/interface.png");

        blit(x, y, 0, 0, xSize, ySize);
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, I18n.format("gui.refinedstorage.interface.import"));
        renderString(7, 42, I18n.format("gui.refinedstorage.interface.export"));
        renderString(7, 122, I18n.format("container.inventory"));
    }
}
