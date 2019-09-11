package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeFluidInterface;
import com.raoulvdberge.refinedstorage.container.ContainerFluidInterface;
import com.raoulvdberge.refinedstorage.gui.control.SideButtonRedstoneMode;
import com.raoulvdberge.refinedstorage.tile.TileFluidInterface;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.TextFormatting;

public class GuiFluidInterface extends GuiBase<ContainerFluidInterface> {
    private static final RenderUtils.FluidRenderer TANK_RENDERER = new RenderUtils.FluidRenderer(NetworkNodeFluidInterface.TANK_CAPACITY, 12, 47);

    public GuiFluidInterface(ContainerFluidInterface container, PlayerInventory inventory) {
        super(container, 211, 204, inventory, null);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileFluidInterface.REDSTONE_MODE));
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/fluid_interface.png");

        drawTexture(x, y, 0, 0, screenWidth, screenHeight);

        if (TileFluidInterface.TANK_IN.getValue() != null) {
            TANK_RENDERER.draw(minecraft, x + 46, y + 56, TileFluidInterface.TANK_IN.getValue());
        }

        if (TileFluidInterface.TANK_OUT.getValue() != null) {
            TANK_RENDERER.draw(minecraft, x + 118, y + 56, TileFluidInterface.TANK_OUT.getValue());
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:fluid_interface"));
        drawString(43 + 4, 20, t("gui.refinedstorage:fluid_interface.in"));
        drawString(115 + 1, 20, t("gui.refinedstorage:fluid_interface.out"));
        drawString(7, 111, t("container.inventory"));

        // TODO getFormattedText
        if (inBounds(46, 56, 12, 47, mouseX, mouseY) && TileFluidInterface.TANK_IN.getValue() != null) {
            drawTooltip(mouseX, mouseY, TileFluidInterface.TANK_IN.getValue().getDisplayName().getFormattedText() + "\n" + TextFormatting.GRAY + API.instance().getQuantityFormatter().formatInBucketForm(TileFluidInterface.TANK_IN.getValue().getAmount()) + TextFormatting.RESET);
        }

        // TODO getFormattedText
        if (inBounds(118, 56, 12, 47, mouseX, mouseY) && TileFluidInterface.TANK_OUT.getValue() != null) {
            drawTooltip(mouseX, mouseY, TileFluidInterface.TANK_OUT.getValue().getDisplayName().getFormattedText() + "\n" + TextFormatting.GRAY + API.instance().getQuantityFormatter().formatInBucketForm(TileFluidInterface.TANK_OUT.getValue().getAmount()) + TextFormatting.RESET);
        }
    }
}
