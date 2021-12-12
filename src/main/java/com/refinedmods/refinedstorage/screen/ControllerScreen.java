package com.refinedmods.refinedstorage.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.apiimpl.network.Network;
import com.refinedmods.refinedstorage.container.ControllerContainer;
import com.refinedmods.refinedstorage.screen.widget.ScrollbarWidget;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.tile.ClientNode;
import com.refinedmods.refinedstorage.tile.ControllerTile;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class ControllerScreen extends BaseScreen<ControllerContainer> {
    private static final int VISIBLE_ROWS = 2;

    private static final int ENERGY_BAR_X = 8;
    private static final int ENERGY_BAR_Y = 20;
    private static final int ENERGY_BAR_WIDTH = 16;
    private static final int ENERGY_BAR_HEIGHT = 59;

    private final ScrollbarWidget scrollbar;

    public ControllerScreen(ControllerContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, 176, 181, inventory, title);

        this.scrollbar = new ScrollbarWidget(this, 157, 20, 12, 59);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, ControllerTile.REDSTONE_MODE));
    }

    @Override
    public void tick(int x, int y) {
        scrollbar.setEnabled(getRows() > VISIBLE_ROWS);
        scrollbar.setMaxOffset(getRows() - VISIBLE_ROWS);
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/controller.png");

        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);

        int energyBarHeightNew = Network.getEnergyScaled(ControllerTile.ENERGY_STORED.getValue(), ControllerTile.ENERGY_CAPACITY.getValue(), ENERGY_BAR_HEIGHT);

        blit(matrixStack, x + ENERGY_BAR_X, y + ENERGY_BAR_Y + ENERGY_BAR_HEIGHT - energyBarHeightNew, 178, ENERGY_BAR_HEIGHT - energyBarHeightNew, ENERGY_BAR_WIDTH, energyBarHeightNew);

        scrollbar.render(matrixStack);
    }

    @Override
    public void mouseMoved(double mx, double my) {
        scrollbar.mouseMoved(mx, my);

        super.mouseMoved(mx, my);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        return scrollbar.mouseClicked(mx, my, button) || super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        return scrollbar.mouseReleased(mx, my, button) || super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        return this.scrollbar.mouseScrolled(x, y, delta) || super.mouseScrolled(x, y, delta);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, title.getString());
        renderString(matrixStack, 7, 87, I18n.get("container.inventory"));

        int x = 33;
        int y = 26;

        int slot = scrollbar.getOffset() * 2;

        RenderHelper.setupFor3DItems();

        List<ClientNode> nodes = ControllerTile.NODES.getValue();

        ClientNode hoveringNode = null;

        for (int i = 0; i < 4; ++i) {
            if (slot < nodes.size()) {
                ClientNode node = nodes.get(slot);

                renderItem(matrixStack, x, y + 5, node.getStack());

                float scale = minecraft.isEnforceUnicode() ? 1F : 0.5F;

                matrixStack.pushPose();
                matrixStack.scale(scale, scale, 1);

                renderString(
                    matrixStack,
                    RenderUtils.getOffsetOnScale(x + 1, scale),
                    RenderUtils.getOffsetOnScale(y - 2, scale),
                    trimNameIfNeeded(!minecraft.isEnforceUnicode(), node.getStack().getHoverName().getString())
                );
                renderString(matrixStack, RenderUtils.getOffsetOnScale(x + 21, scale), RenderUtils.getOffsetOnScale(y + 10, scale), node.getAmount() + "x");

                matrixStack.popPose();

                if (RenderUtils.inBounds(x, y, 16, 16, mouseX, mouseY)) {
                    hoveringNode = node;
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

        if (hoveringNode != null) {
            renderTooltip(matrixStack, mouseX, mouseY, I18n.get("misc.refinedstorage.energy_usage_minimal", hoveringNode.getEnergyUsage()));
        }

        if (RenderUtils.inBounds(ENERGY_BAR_X, ENERGY_BAR_Y, ENERGY_BAR_WIDTH, ENERGY_BAR_HEIGHT, mouseX, mouseY)) {
            renderTooltip(matrixStack, mouseX, mouseY, I18n.get("misc.refinedstorage.energy_usage", ControllerTile.ENERGY_USAGE.getValue()) + "\n" + I18n.get("misc.refinedstorage.energy_stored", ControllerTile.ENERGY_STORED.getValue(), ControllerTile.ENERGY_CAPACITY.getValue()));
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
