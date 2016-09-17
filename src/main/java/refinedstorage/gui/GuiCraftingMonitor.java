package refinedstorage.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.text.TextFormatting;
import refinedstorage.RefinedStorage;
import refinedstorage.container.ContainerCraftingMonitor;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.network.MessageCraftingMonitorCancel;
import refinedstorage.tile.ClientCraftingTask;
import refinedstorage.tile.TileCraftingMonitor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GuiCraftingMonitor extends GuiBase {
    private static final int VISIBLE_ROWS = 5;

    private static final int ITEM_WIDTH = 143;
    private static final int ITEM_HEIGHT = 18;

    private TileCraftingMonitor craftingMonitor;

    private GuiButton cancelButton;
    private GuiButton cancelAllButton;

    private int itemSelected = -1;

    private int itemSelectedX = -1;
    private int itemSelectedY = -1;

    public GuiCraftingMonitor(ContainerCraftingMonitor container, TileCraftingMonitor craftingMonitor) {
        super(container, 176, 230);

        this.craftingMonitor = craftingMonitor;

        this.scrollbar = new Scrollbar(157, 20, 12, 89);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(TileCraftingMonitor.REDSTONE_MODE));

        String cancel = t("gui.cancel");
        String cancelAll = t("misc.refinedstorage:cancel_all");

        int cancelButtonWidth = 14 + fontRendererObj.getStringWidth(cancel);
        int cancelAllButtonWidth = 14 + fontRendererObj.getStringWidth(cancelAll);

        cancelButton = addButton(x + 7, y + 114, cancelButtonWidth, 20, cancel, false);
        cancelAllButton = addButton(x + 7 + cancelButtonWidth + 4, y + 114, cancelAllButtonWidth, 20, cancelAll, false);
    }

    @Override
    public void update(int x, int y) {
        scrollbar.setEnabled(getRows() > VISIBLE_ROWS);
        scrollbar.setMaxOffset(getRows() - VISIBLE_ROWS);

        if (itemSelected >= getTasks().size()) {
            itemSelected = -1;
        }

        cancelButton.enabled = itemSelected != -1;
        cancelAllButton.enabled = getTasks().size() > 0;
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/crafting_monitor.png");

        drawTexture(x, y, 0, 0, width, height);

        if (itemSelectedX != -1 && itemSelectedY != -1) {
            drawTexture(x + itemSelectedX, y + itemSelectedY, 0, 232, ITEM_WIDTH, ITEM_HEIGHT);
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:crafting_monitor"));
        drawString(7, 137, t("container.inventory"));

        int item = scrollbar.getOffset();

        RenderHelper.enableGUIStandardItemLighting();

        String[] lines = null;

        int ox = 8;
        int x = ox;
        int y = 20;

        itemSelectedX = -1;
        itemSelectedY = -1;

        for (int i = 0; i < VISIBLE_ROWS; ++i) {
            if (item < getTasks().size()) {
                ClientCraftingTask task = getTasks().get(item);

                if (item == itemSelected) {
                    itemSelectedX = x;
                    itemSelectedY = y;
                }

                if (task.getDepth() > 0) {
                    x += 16F - ((float) (task.getChildren() - task.getDepth()) / (float) task.getChildren() * 16F);
                }

                drawItem(x + 2, y + 1, task.getOutput());

                float scale = 0.5f;

                GlStateManager.pushMatrix();
                GlStateManager.scale(scale, scale, 1);

                drawString(calculateOffsetOnScale(x + 21, scale), calculateOffsetOnScale(y + 7, scale), task.getOutput().getDisplayName());

                if (task.getProgress() != -1) {
                    drawString(calculateOffsetOnScale(ox + ITEM_WIDTH - 15, scale), calculateOffsetOnScale(y + 7, scale), task.getProgress() + "%");
                }

                GlStateManager.popMatrix();

                if (inBounds(x + 2, y + 1, 16, 16, mouseX, mouseY) && !task.getStatus().trim().equals("")) {
                    lines = task.getStatus().split("\n");

                    for (int j = 0; j < lines.length; ++j) {
                        String line = lines[j];

                        if (line.startsWith("T=")) {
                            line = t(line.substring(2));
                        } else if (line.startsWith("I=")) {
                            line = TextFormatting.YELLOW + t(line.substring(2));
                        }

                        lines[j] = line;
                    }
                }

                x = ox;
                y += ITEM_HEIGHT;
            }

            item++;
        }

        if (lines != null) {
            drawTooltip(mouseX, mouseY, Arrays.asList(lines));
        }
    }

    private int getRows() {
        return getTasks().size();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button == cancelButton && itemSelected != -1) {
            ClientCraftingTask task = getTasks().get(itemSelected);

            RefinedStorage.INSTANCE.network.sendToServer(new MessageCraftingMonitorCancel(craftingMonitor, task.getId(), task.getDepth()));
        } else if (button == cancelAllButton && getTasks().size() > 0) {
            RefinedStorage.INSTANCE.network.sendToServer(new MessageCraftingMonitorCancel(craftingMonitor, -1, 0));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        itemSelected = -1;

        if (mouseButton == 0 && inBounds(8, 20, 144, 90, mouseX - guiLeft, mouseY - guiTop)) {
            int item = scrollbar.getOffset();

            for (int i = 0; i < VISIBLE_ROWS; ++i) {
                int ix = 8;
                int iy = 20 + (i * ITEM_HEIGHT);

                if (inBounds(ix, iy, ITEM_WIDTH, ITEM_HEIGHT, mouseX - guiLeft, mouseY - guiTop) && (item + i) < getTasks().size()) {
                    itemSelected = item + i;
                }
            }
        }
    }

    private List<ClientCraftingTask> getTasks() {
        return TileCraftingMonitor.TASKS.getValue();
    }
}
