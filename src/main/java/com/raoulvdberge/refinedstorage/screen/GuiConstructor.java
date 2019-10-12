package com.raoulvdberge.refinedstorage.screen;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.container.ConstructorContainer;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.CompareSideButton;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.ConstructorDropSideButton;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.TypeSideButton;
import com.raoulvdberge.refinedstorage.tile.TileConstructor;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;

public class GuiConstructor extends BaseScreen<ConstructorContainer> {
    public GuiConstructor(ConstructorContainer container, PlayerInventory inventory) {
        super(container, 211, 137, inventory, null); // TODO TextComponent
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, TileConstructor.REDSTONE_MODE));

        addSideButton(new TypeSideButton(this, TileConstructor.TYPE));

        addSideButton(new CompareSideButton(this, TileConstructor.COMPARE, IComparer.COMPARE_NBT));
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
        renderString(7, 7, I18n.format("gui.refinedstorage:constructor"));
        renderString(7, 43, I18n.format("container.inventory"));
    }
}
