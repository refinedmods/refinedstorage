package com.raoulvdberge.refinedstorage.screen;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.container.ConstructorContainer;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.ConstructorDropSideButton;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.ExactModeSideButton;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.TypeSideButton;
import com.raoulvdberge.refinedstorage.tile.ConstructorTile;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ConstructorScreen extends BaseScreen<ConstructorContainer> {
    public ConstructorScreen(ConstructorContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, 211, 137, inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, ConstructorTile.REDSTONE_MODE));

        addSideButton(new TypeSideButton(this, ConstructorTile.TYPE));

        addSideButton(new ExactModeSideButton(this, ConstructorTile.COMPARE));
        addSideButton(new ConstructorDropSideButton(this));
    }

    @Override
    public void tick(int x, int y) {
    }

    @Override
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/constructor.png");

        blit(x, y, 0, 0, xSize, ySize);
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, title.getFormattedText());
        renderString(7, 43, I18n.format("container.inventory"));
    }
}
