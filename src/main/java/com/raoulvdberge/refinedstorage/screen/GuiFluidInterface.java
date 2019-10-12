package com.raoulvdberge.refinedstorage.screen;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeFluidInterface;
import com.raoulvdberge.refinedstorage.container.FluidInterfaceContainer;
import com.raoulvdberge.refinedstorage.render.FluidRenderer;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.raoulvdberge.refinedstorage.tile.TileFluidInterface;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.TextFormatting;

public class GuiFluidInterface extends BaseScreen<FluidInterfaceContainer> {
    private static final FluidRenderer TANK_RENDERER = new FluidRenderer(NetworkNodeFluidInterface.TANK_CAPACITY, 12, 47);

    public GuiFluidInterface(FluidInterfaceContainer container, PlayerInventory inventory) {
        super(container, 211, 204, inventory, null);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, TileFluidInterface.REDSTONE_MODE));
    }

    @Override
    public void tick(int x, int y) {
    }

    @Override
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/fluid_interface.png");

        blit(x, y, 0, 0, xSize, ySize);

        if (TileFluidInterface.TANK_IN.getValue() != null) {
            TANK_RENDERER.render(x + 46, y + 56, TileFluidInterface.TANK_IN.getValue());
        }

        if (TileFluidInterface.TANK_OUT.getValue() != null) {
            TANK_RENDERER.render(x + 118, y + 56, TileFluidInterface.TANK_OUT.getValue());
        }
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, I18n.format("gui.refinedstorage:fluid_interface"));
        renderString(43 + 4, 20, I18n.format("gui.refinedstorage:fluid_interface.in"));
        renderString(115 + 1, 20, I18n.format("gui.refinedstorage:fluid_interface.out"));
        renderString(7, 111, I18n.format("container.inventory"));

        // TODO getFormattedText
        if (RenderUtils.inBounds(46, 56, 12, 47, mouseX, mouseY) && TileFluidInterface.TANK_IN.getValue() != null) {
            renderTooltip(mouseX, mouseY, TileFluidInterface.TANK_IN.getValue().getDisplayName().getFormattedText() + "\n" + TextFormatting.GRAY + API.instance().getQuantityFormatter().formatInBucketForm(TileFluidInterface.TANK_IN.getValue().getAmount()) + TextFormatting.RESET);
        }

        // TODO getFormattedText
        if (RenderUtils.inBounds(118, 56, 12, 47, mouseX, mouseY) && TileFluidInterface.TANK_OUT.getValue() != null) {
            renderTooltip(mouseX, mouseY, TileFluidInterface.TANK_OUT.getValue().getDisplayName().getFormattedText() + "\n" + TextFormatting.GRAY + API.instance().getQuantityFormatter().formatInBucketForm(TileFluidInterface.TANK_OUT.getValue().getAmount()) + TextFormatting.RESET);
        }
    }
}
