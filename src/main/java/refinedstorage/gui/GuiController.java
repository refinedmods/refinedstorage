package refinedstorage.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.container.ContainerController;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.tile.controller.ClientSlave;
import refinedstorage.tile.controller.TileController;

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

        setScrollbar(new Scrollbar(157, 20, 12, 59));

        this.controller = controller;
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(controller));
    }

    @Override
    public void update(int x, int y) {
        getScrollbar().setCanScroll(getRows() > VISIBLE_ROWS);
        getScrollbar().setScrollDelta((float) getScrollbar().getScrollbarHeight() / (float) getRows());
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/controller.png");

        drawTexture(x, y, 0, 0, width, height);

        int barHeightNew = controller.getEnergyScaled(barHeight);

        drawTexture(x + barX, y + barY + barHeight - barHeightNew, 178, barHeight - barHeightNew, barWidth, barHeightNew);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:controller." + controller.getType().getId()));
        drawString(7, 87, t("container.inventory"));

        int x = 33;
        int y = 26;

        int slot = getOffset() * 2;

        RenderHelper.enableGUIStandardItemLighting();

        List<ClientSlave> slaves = controller.getClientSlaves();

        ClientSlave slaveHovering = null;

        for (int i = 0; i < 4; ++i) {
            if (slot < slaves.size()) {
                ClientSlave slave = slaves.get(slot);

                drawItem(x, y + 5, slave.stack);

                float scale = 0.5f;

                GlStateManager.pushMatrix();
                GlStateManager.scale(scale, scale, 1);

                drawString(RefinedStorageUtils.calculateOffsetOnScale(x + 1, scale), RefinedStorageUtils.calculateOffsetOnScale(y - 2, scale), slave.stack.getDisplayName());
                drawString(RefinedStorageUtils.calculateOffsetOnScale(x + 21, scale), RefinedStorageUtils.calculateOffsetOnScale(y + 10, scale), t("gui.refinedstorage:controller.machine_amount", slave.amount));

                GlStateManager.popMatrix();

                if (inBounds(x, y, 16, 16, mouseX, mouseY)) {
                    slaveHovering = slave;
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

        if (slaveHovering != null) {
            drawTooltip(mouseX, mouseY, t("misc.refinedstorage:energy_usage_minimal", slaveHovering.energyUsage));
        }

        if (inBounds(barX, barY, barWidth, barHeight, mouseX, mouseY)) {
            drawTooltip(mouseX, mouseY, t("misc.refinedstorage:energy_usage", controller.getEnergyUsage()) + "\n" + t("misc.refinedstorage:energy_stored", controller.getEnergy().getEnergyStored(), controller.getEnergy().getMaxEnergyStored()));
        }
    }

    public int getOffset() {
        return (int) Math.ceil(getScrollbar().getCurrentScroll() / 59f * (float) getRows());
    }

    private int getRows() {
        int max = (int) Math.ceil((float) controller.getClientSlaves().size() / (float) 2);

        return max < 0 ? 0 : max;
    }
}
