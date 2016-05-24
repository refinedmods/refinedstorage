package refinedstorage.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import refinedstorage.container.ContainerController;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.tile.controller.ClientSideMachine;
import refinedstorage.tile.controller.TileController;

import java.util.List;

public class GuiController extends GuiBase {
    public static final int VISIBLE_ROWS = 2;

    private TileController controller;

    private int barX = 8;
    private int barY = 20;
    private int barWidth = 16;
    private int barHeight = 59;

    private Scrollbar scrollbar = new Scrollbar(157, 20, 12, 59);

    public GuiController(ContainerController container, TileController controller) {
        super(container, 176, 181);

        this.controller = controller;
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(controller));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        scrollbar.update(this, mouseX - guiLeft, mouseY - guiTop);
    }

    @Override
    public void update(int x, int y) {
        scrollbar.setCanScroll(getRows() > VISIBLE_ROWS);
        scrollbar.setScrollDelta((float) scrollbar.getScrollbarHeight() / (float) getRows());
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/controller.png");

        drawTexture(x, y, 0, 0, width, height);

        int barHeightNew = (int) ((float) controller.getEnergyStored(null) / (float) controller.getMaxEnergyStored(null) * (float) barHeight);

        drawTexture(x + barX, y + barY + barHeight - barHeightNew, 178, barHeight - barHeightNew, barWidth, barHeightNew);

        scrollbar.draw(this);
    }

    private int calculateOffsetOnScale(int pos, float scale) {
        float multiplier = (pos / scale);
        return (int) multiplier;
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:controller." + controller.getType().getId()));
        drawString(7, 87, t("container.inventory"));

        int x = 33;
        int y = 26;

        int slot = getOffset() * 2;

        RenderHelper.enableGUIStandardItemLighting();

        List<ClientSideMachine> machines = controller.getClientSideMachines();

        ClientSideMachine machineHovering = null;

        for (int i = 0; i < 4; ++i) {
            if (slot < machines.size()) {
                ClientSideMachine machine = machines.get(slot);

                drawItem(x, y + 5, machine.stack);

                GlStateManager.pushMatrix();

                float scale = 0.5f;
                GlStateManager.scale(scale, scale, 1);
                drawString(calculateOffsetOnScale(x + 1, scale), calculateOffsetOnScale(y - 2, scale), machine.stack.getDisplayName());
                drawString(calculateOffsetOnScale(x + 21, scale), calculateOffsetOnScale(y + 10, scale), t("gui.refinedstorage:controller.machine_amount", machine.amount));

                GlStateManager.popMatrix();

                if (inBounds(x, y, 16, 16, mouseX, mouseY)) {
                    machineHovering = machine;
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

        if (machineHovering != null) {
            drawTooltip(mouseX, mouseY, t("misc.refinedstorage:energy_usage_minimal", machineHovering.energyUsage));
        }

        if (inBounds(barX, barY, barWidth, barHeight, mouseX, mouseY)) {
            String message = t("misc.refinedstorage:energy_usage", controller.getEnergyUsage());
            message += "\n" + t("misc.refinedstorage:energy_stored", controller.getEnergyStored(null), controller.getMaxEnergyStored(null));

            drawTooltip(mouseX, mouseY, message);
        }
    }

    public int getOffset() {
        return (int) (scrollbar.getCurrentScroll() / 59f * (float) getRows());
    }

    private int getRows() {
        int max = (int) Math.ceil((float) controller.getClientSideMachines().size() / (float) 2);

        return max < 0 ? 0 : max;
    }
}
