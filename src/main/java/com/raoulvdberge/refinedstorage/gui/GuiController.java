package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.container.ContainerController;
import com.raoulvdberge.refinedstorage.gui.control.Scrollbar;
import com.raoulvdberge.refinedstorage.gui.control.SideButtonRedstoneMode;
import com.raoulvdberge.refinedstorage.tile.ClientNode;
import com.raoulvdberge.refinedstorage.tile.TileController;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;

import java.util.List;

public class GuiController extends GuiBase {
    private static final int VISIBLE_ROWS = 2;

    private TileController controller;

    private int barX = 8;
    private int barY = 20;
    private int barWidth = 16;
    private int barHeight = 59;

    public GuiController(ContainerController container, TileController controller) {
        super(container, 176, 181);

        this.controller = controller;

        this.scrollbar = new Scrollbar(157, 20, 12, 59);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileController.REDSTONE_MODE));
    }

    @Override
    public void update(int x, int y) {
        if (scrollbar != null) {
            scrollbar.setEnabled(getRows() > VISIBLE_ROWS);
            scrollbar.setMaxOffset(getRows() - VISIBLE_ROWS);
        }
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/controller.png");

        drawTexture(x, y, 0, 0, screenWidth, screenHeight);

        int barHeightNew = TileController.getEnergyScaled(TileController.ENERGY_STORED.getValue(), TileController.ENERGY_CAPACITY.getValue(), barHeight);

        drawTexture(x + barX, y + barY + barHeight - barHeightNew, 178, barHeight - barHeightNew, barWidth, barHeightNew);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:controller." + controller.getType().getId()));
        drawString(7, 87, t("container.inventory"));

        int x = 33;
        int y = 26;

        int slot = scrollbar != null ? (scrollbar.getOffset() * 2) : 0;

        RenderHelper.enableGUIStandardItemLighting();

        List<ClientNode> nodes = TileController.NODES.getValue();

        ClientNode nodeHovering = null;

        for (int i = 0; i < 4; ++i) {
            if (slot < nodes.size()) {
                ClientNode node = nodes.get(slot);

                drawItem(x, y + 5, node.getStack());

                float scale = fontRenderer.getUnicodeFlag() ? 1F : 0.5F;

                GlStateManager.pushMatrix();
                GlStateManager.scale(scale, scale, 1);

                drawString(RenderUtils.getOffsetOnScale(x + 1, scale), RenderUtils.getOffsetOnScale(y - 2, scale), trimNameIfNeeded(!fontRenderer.getUnicodeFlag(), node.getStack().getDisplayName()));
                drawString(RenderUtils.getOffsetOnScale(x + 21, scale), RenderUtils.getOffsetOnScale(y + 10, scale), node.getAmount() + "x");

                GlStateManager.popMatrix();

                if (inBounds(x, y, 16, 16, mouseX, mouseY)) {
                    nodeHovering = node;
                }
            }

            if (i == 1) {
                x = 33;
                y += 30;
            } else {
                x += 60;
            }

            slot++;
        }

        if (nodeHovering != null) {
            drawTooltip(mouseX, mouseY, t("misc.refinedstorage:energy_usage_minimal", nodeHovering.getEnergyUsage()));
        }

        if (inBounds(barX, barY, barWidth, barHeight, mouseX, mouseY)) {
            drawTooltip(mouseX, mouseY, t("misc.refinedstorage:energy_usage", TileController.ENERGY_USAGE.getValue()) + "\n" + t("misc.refinedstorage:energy_stored", TileController.ENERGY_STORED.getValue(), TileController.ENERGY_CAPACITY.getValue()));
        }
    }

    private int getRows() {
        return Math.max(0, (int) Math.ceil((float) TileController.NODES.getValue().size() / 2F));
    }

    private String trimNameIfNeeded(boolean scaled, String name) {
        int max = scaled ? 20 : 13;
        if (name.length() > max) {
            name = name.substring(0, max) + "...";
        }
        return name;
    }
}
