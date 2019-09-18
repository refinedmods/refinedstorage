package com.raoulvdberge.refinedstorage.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.container.ContainerController;
import com.raoulvdberge.refinedstorage.screen.widget.ScrollbarWidget;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.SideButtonRedstoneMode;
import com.raoulvdberge.refinedstorage.tile.ClientNode;
import com.raoulvdberge.refinedstorage.tile.ControllerTile;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;

import java.util.List;

public class GuiController extends BaseScreen<ContainerController> {
    private static final int VISIBLE_ROWS = 2;

    private ControllerTile controller;

    private int barX = 8;
    private int barY = 20;
    private int barWidth = 16;
    private int barHeight = 59;

    private ScrollbarWidget scrollbar;

    public GuiController(ContainerController container, ControllerTile controller, PlayerInventory inventory) {
        super(container, 176, 181, inventory, null);

        this.controller = controller;

        this.scrollbar = new ScrollbarWidget(157, 20, 12, 59);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, ControllerTile.REDSTONE_MODE));
    }

    @Override
    public void tick(int x, int y) {
        if (scrollbar != null) {
            scrollbar.setEnabled(getRows() > VISIBLE_ROWS);
            scrollbar.setMaxOffset(getRows() - VISIBLE_ROWS);
        }
    }

    @Override
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/controller.png");

        blit(x, y, 0, 0, xSize, ySize);

        int barHeightNew = ControllerTile.getEnergyScaled(ControllerTile.ENERGY_STORED.getValue(), ControllerTile.ENERGY_CAPACITY.getValue(), barHeight);

        blit(x + barX, y + barY + barHeight - barHeightNew, 178, barHeight - barHeightNew, barWidth, barHeightNew);
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        // TODO renderString(7, 7, I18n.format("gui.refinedstorage:controller." + controller.getControllerType().getId()));
        renderString(7, 87, I18n.format("container.inventory"));

        int x = 33;
        int y = 26;

        int slot = scrollbar != null ? (scrollbar.getOffset() * 2) : 0;

        RenderHelper.enableGUIStandardItemLighting();

        List<ClientNode> nodes = ControllerTile.NODES.getValue();

        ClientNode nodeHovering = null;

        for (int i = 0; i < 4; ++i) {
            if (slot < nodes.size()) {
                ClientNode node = nodes.get(slot);

                renderItem(x, y + 5, node.getStack());

                float scale = /*TODO fontRenderer.getUnicodeFlag() ? 1F :*/ 0.5F;

                GlStateManager.pushMatrix();
                GlStateManager.scalef(scale, scale, 1);

                renderString(
                    RenderUtils.getOffsetOnScale(x + 1, scale),
                    RenderUtils.getOffsetOnScale(y - 2, scale),
                    trimNameIfNeeded(/*TODO !fontRenderer.getUnicodeFlag()*/false, node.getStack().getDisplayName().getString()) // TODO
                );
                renderString(RenderUtils.getOffsetOnScale(x + 21, scale), RenderUtils.getOffsetOnScale(y + 10, scale), node.getAmount() + "x");

                GlStateManager.popMatrix();

                if (RenderUtils.inBounds(x, y, 16, 16, mouseX, mouseY)) {
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
            renderTooltip(mouseX, mouseY, I18n.format("misc.refinedstorage.energy_usage_minimal", nodeHovering.getEnergyUsage()));
        }

        if (RenderUtils.inBounds(barX, barY, barWidth, barHeight, mouseX, mouseY)) {
            renderTooltip(mouseX, mouseY, I18n.format("misc.refinedstorage.energy_usage", ControllerTile.ENERGY_USAGE.getValue()) + "\n" + I18n.format("misc.refinedstorage.energy_stored", ControllerTile.ENERGY_STORED.getValue(), ControllerTile.ENERGY_CAPACITY.getValue()));
        }
    }

    private int getRows() {
        return Math.max(0, (int) Math.ceil((float) ControllerTile.NODES.getValue().size() / 2F));
    }

    private String trimNameIfNeeded(boolean scaled, String name) {
        int max = scaled ? 20 : 13;
        if (name.length() > max) {
            name = name.substring(0, max) + "...";
        }
        return name;
    }
}
