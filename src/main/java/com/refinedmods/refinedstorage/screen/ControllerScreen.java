package com.refinedmods.refinedstorage.screen;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.apiimpl.network.Network;
import com.refinedmods.refinedstorage.container.ControllerContainerMenu;
import com.refinedmods.refinedstorage.screen.widget.ScrollbarWidget;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.blockentity.ClientNode;
import com.refinedmods.refinedstorage.blockentity.ControllerBlockEntity;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class ControllerScreen extends BaseScreen<ControllerContainerMenu> {
    private static final int VISIBLE_ROWS = 2;

    private static final int ENERGY_BAR_X = 8;
    private static final int ENERGY_BAR_Y = 20;
    private static final int ENERGY_BAR_WIDTH = 16;
    private static final int ENERGY_BAR_HEIGHT = 59;

    private final ScrollbarWidget scrollbar;

    public ControllerScreen(ControllerContainerMenu containerMenu, Inventory inventory, Component title) {
        super(containerMenu, 176, 181, inventory, title);

        this.scrollbar = new ScrollbarWidget(this, 157, 20, 12, 59);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, ControllerBlockEntity.REDSTONE_MODE));
    }

    @Override
    public void tick(int x, int y) {
        scrollbar.setEnabled(getRows() > VISIBLE_ROWS);
        scrollbar.setMaxOffset(getRows() - VISIBLE_ROWS);
    }

    @Override
    public void renderBackground(PoseStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/controller.png");

        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);

        int energyBarHeightNew = Network.getEnergyScaled(ControllerBlockEntity.ENERGY_STORED.getValue(), ControllerBlockEntity.ENERGY_CAPACITY.getValue(), ENERGY_BAR_HEIGHT);

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
    public void renderForeground(PoseStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, title.getString());
        renderString(matrixStack, 7, 87, I18n.get("container.inventory"));

        int x = 33;
        int y = 26;

        int slot = scrollbar.getOffset() * 2;

        Lighting.setupFor3DItems();

        List<ClientNode> nodes = ControllerBlockEntity.NODES.getValue();

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
            renderTooltip(matrixStack, mouseX, mouseY, I18n.get("misc.refinedstorage.energy_usage", ControllerBlockEntity.ENERGY_USAGE.getValue()) + "\n" + I18n.get("misc.refinedstorage.energy_stored", ControllerBlockEntity.ENERGY_STORED.getValue(), ControllerBlockEntity.ENERGY_CAPACITY.getValue()));
        }
    }

    private int getRows() {
        return Math.max(0, (int) Math.ceil((float) ControllerBlockEntity.NODES.getValue().size() / 2F));
    }

    private String trimNameIfNeeded(boolean scaled, String name) {
        int max = scaled ? 20 : 13;
        if (name.length() > max) {
            name = name.substring(0, max) + "...";
        }
        return name;
    }
}
