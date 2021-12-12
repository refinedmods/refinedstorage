package com.refinedmods.refinedstorage.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.FluidInterfaceNetworkNode;
import com.refinedmods.refinedstorage.container.FluidInterfaceContainer;
import com.refinedmods.refinedstorage.render.FluidRenderer;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.tile.FluidInterfaceTile;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class FluidInterfaceScreen extends BaseScreen<FluidInterfaceContainer> {
    private static final FluidRenderer TANK_RENDERER = new FluidRenderer(FluidInterfaceNetworkNode.TANK_CAPACITY, 12, 47, 1);

    public FluidInterfaceScreen(FluidInterfaceContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, 211, 204, inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, NetworkNodeTile.REDSTONE_MODE));
    }

    @Override
    public void tick(int x, int y) {
        // NO OP
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/fluid_interface.png");

        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);

        if (!FluidInterfaceTile.TANK_IN.getValue().isEmpty()) {
            TANK_RENDERER.render(matrixStack, x + 46, y + 56, FluidInterfaceTile.TANK_IN.getValue());
        }

        if (!FluidInterfaceTile.TANK_OUT.getValue().isEmpty()) {
            TANK_RENDERER.render(matrixStack, x + 118, y + 56, FluidInterfaceTile.TANK_OUT.getValue());
        }
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, title.getString());
        renderString(matrixStack, 43 + 4, 20, I18n.get("gui.refinedstorage.fluid_interface.in"));
        renderString(matrixStack, 115 + 1, 20, I18n.get("gui.refinedstorage.fluid_interface.out"));
        renderString(matrixStack, 7, 111, I18n.get("container.inventory"));

        if (RenderUtils.inBounds(46, 56, 12, 47, mouseX, mouseY) && !FluidInterfaceTile.TANK_IN.getValue().isEmpty()) {
            renderTooltip(matrixStack, mouseX, mouseY, FluidInterfaceTile.TANK_IN.getValue().getDisplayName().getString() + "\n" + TextFormatting.GRAY + API.instance().getQuantityFormatter().formatInBucketForm(FluidInterfaceTile.TANK_IN.getValue().getAmount()) + TextFormatting.RESET);
        }

        if (RenderUtils.inBounds(118, 56, 12, 47, mouseX, mouseY) && !FluidInterfaceTile.TANK_OUT.getValue().isEmpty()) {
            renderTooltip(matrixStack, mouseX, mouseY, FluidInterfaceTile.TANK_OUT.getValue().getDisplayName().getString() + "\n" + TextFormatting.GRAY + API.instance().getQuantityFormatter().formatInBucketForm(FluidInterfaceTile.TANK_OUT.getValue().getAmount()) + TextFormatting.RESET);
        }
    }
}
