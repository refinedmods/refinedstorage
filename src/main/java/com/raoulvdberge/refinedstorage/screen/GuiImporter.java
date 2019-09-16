package com.raoulvdberge.refinedstorage.screen;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.container.ContainerImporter;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.SideButtonCompare;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.SideButtonMode;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.SideButtonRedstoneMode;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.SideButtonType;
import com.raoulvdberge.refinedstorage.tile.TileImporter;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;

public class GuiImporter extends BaseScreen<ContainerImporter> {
    public GuiImporter(ContainerImporter container, PlayerInventory inventory) {
        super(container, 211, 137, inventory, null);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileImporter.REDSTONE_MODE));

        addSideButton(new SideButtonType(this, TileImporter.TYPE));

        addSideButton(new SideButtonMode(this, TileImporter.MODE));

        addSideButton(new SideButtonCompare(this, TileImporter.COMPARE, IComparer.COMPARE_NBT));
    }

    @Override
    public void tick(int x, int y) {
    }

    @Override
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/importer.png");

        blit(x, y, 0, 0, xSize, ySize);
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, I18n.format("gui.refinedstorage:importer"));
        renderString(7, 43, I18n.format("container.inventory"));
    }
}
