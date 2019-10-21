package com.raoulvdberge.refinedstorage.screen;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.FluidInterfaceNetworkNode;
import com.raoulvdberge.refinedstorage.container.FluidInterfaceContainer;
import com.raoulvdberge.refinedstorage.render.FluidRenderer;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.raoulvdberge.refinedstorage.tile.FluidInterfaceTile;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
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
        addSideButton(new RedstoneModeSideButton(this, FluidInterfaceTile.REDSTONE_MODE));
    }

    @Override
    public void tick(int x, int y) {
    }

    @Override
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/fluid_interface.png");

        blit(x, y, 0, 0, xSize, ySize);

        if (!FluidInterfaceTile.TANK_IN.getValue().isEmpty()) {
            TANK_RENDERER.render(x + 46, y + 56, FluidInterfaceTile.TANK_IN.getValue());
        }

        if (!FluidInterfaceTile.TANK_OUT.getValue().isEmpty()) {
            TANK_RENDERER.render(x + 118, y + 56, FluidInterfaceTile.TANK_OUT.getValue());
        }
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, title.getFormattedText());
        renderString(43 + 4, 20, I18n.format("gui.refinedstorage.fluid_interface.in"));
        renderString(115 + 1, 20, I18n.format("gui.refinedstorage.fluid_interface.out"));
        renderString(7, 111, I18n.format("container.inventory"));

        if (RenderUtils.inBounds(46, 56, 12, 47, mouseX, mouseY) && !FluidInterfaceTile.TANK_IN.getValue().isEmpty()) {
            renderTooltip(mouseX, mouseY, FluidInterfaceTile.TANK_IN.getValue().getDisplayName().getFormattedText() + "\n" + TextFormatting.GRAY + API.instance().getQuantityFormatter().formatInBucketForm(FluidInterfaceTile.TANK_IN.getValue().getAmount()) + TextFormatting.RESET);
        }

        if (RenderUtils.inBounds(118, 56, 12, 47, mouseX, mouseY) && !FluidInterfaceTile.TANK_OUT.getValue().isEmpty()) {
            renderTooltip(mouseX, mouseY, FluidInterfaceTile.TANK_OUT.getValue().getDisplayName().getFormattedText() + "\n" + TextFormatting.GRAY + API.instance().getQuantityFormatter().formatInBucketForm(FluidInterfaceTile.TANK_OUT.getValue().getAmount()) + TextFormatting.RESET);
        }
    }
}
