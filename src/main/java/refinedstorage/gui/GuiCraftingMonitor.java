package refinedstorage.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.text.TextFormatting;
import refinedstorage.RefinedStorage;
import refinedstorage.container.ContainerCraftingMonitor;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.network.MessageCraftingMonitorCancel;
import refinedstorage.tile.TileCraftingMonitor;

import java.io.IOException;
import java.util.Arrays;

// @TODO: Make it work
public class GuiCraftingMonitor extends GuiBase {
    private static final int VISIBLE_ROWS = 3;

    private static final int ITEM_WIDTH = 72;
    private static final int ITEM_HEIGHT = 30;

    private TileCraftingMonitor craftingMonitor;

    private GuiButton cancelButton;
    private GuiButton cancelAllButton;

    private int itemSelected = -1;

    private boolean renderItemSelection;
    private int renderItemSelectionX;
    private int renderItemSelectionY;

    public GuiCraftingMonitor(ContainerCraftingMonitor container, TileCraftingMonitor craftingMonitor) {
        super(container, 176, 230);

        setScrollbar(new Scrollbar(157, 20, 12, 89));

        this.craftingMonitor = craftingMonitor;
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(TileCraftingMonitor.REDSTONE_MODE));

        String cancel = t("gui.cancel");
        String cancelAll = t("misc.refinedstorage:cancel_all");

        int cancelButtonWidth = 14 + fontRendererObj.getStringWidth(cancel);
        int cancelAllButtonWidth = 14 + fontRendererObj.getStringWidth(cancelAll);

        cancelButton = addButton(x + 7, y + 113, cancelButtonWidth, 20, cancel, false);
        cancelAllButton = addButton(x + 7 + cancelButtonWidth + 4, y + 113, cancelAllButtonWidth, 20, cancelAll, false);
    }

    @Override
    public void update(int x, int y) {
        getScrollbar().setCanScroll(getRows() > VISIBLE_ROWS);
        getScrollbar().setScrollDelta((float) getScrollbar().getScrollbarHeight() / (float) getRows());

        if (itemSelected >= craftingMonitor.getTasks().size()) {
            itemSelected = -1;
        }

        cancelButton.enabled = itemSelected != -1;
        cancelAllButton.enabled = craftingMonitor.getTasks().size() > 0;
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/crafting_monitor.png");

        drawTexture(x, y, 0, 0, width, height);

        if (renderItemSelection) {
            drawTexture(x + renderItemSelectionX, y + renderItemSelectionY, 178, 0, ITEM_WIDTH, ITEM_HEIGHT);
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:crafting_monitor"));
        drawString(7, 137, t("container.inventory"));

        int x = 8;
        int y = 20;

        int item = getOffset() * 2;

        RenderHelper.enableGUIStandardItemLighting();

        String[] lines = null;

        renderItemSelection = false;

        for (int i = 0; i < 6; ++i) {
            if (item < craftingMonitor.getTasks().size()) {
                if (item == itemSelected) {
                    renderItemSelection = true;
                    renderItemSelectionX = x;
                    renderItemSelectionY = y;
                }

                TileCraftingMonitor.ClientSideCraftingTask task = craftingMonitor.getTasks().get(i);

                drawItem(x + 4, y + 11, task.output);

                float scale = 0.5f;

                GlStateManager.pushMatrix();
                GlStateManager.scale(scale, scale, 1);

                drawString(calculateOffsetOnScale(x + 5, scale), calculateOffsetOnScale(y + 4, scale), task.output.getDisplayName());

                GlStateManager.popMatrix();

                if (inBounds(x + 5, y + 10, 16, 16, mouseX, mouseY) && !task.info.trim().equals("")) {
                    lines = task.info.split("\n");

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
            }

            if (i == 1 || i == 3) {
                x = 8;
                y += ITEM_HEIGHT;
            } else {
                x += ITEM_WIDTH;
            }

            item++;
        }

        if (lines != null) {
            drawTooltip(mouseX, mouseY, Arrays.asList(lines));
        }
    }

    public int getOffset() {
        return (int) Math.ceil(getScrollbar().getCurrentScroll() / 89f * (float) getRows());
    }

    private int getRows() {
        int max = (int) Math.ceil((float) craftingMonitor.getTasks().size() / (float) 2);

        return max < 0 ? 0 : max;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button == cancelButton && itemSelected != -1) {
            RefinedStorage.INSTANCE.network.sendToServer(new MessageCraftingMonitorCancel(craftingMonitor, craftingMonitor.getTasks().get(itemSelected).id));
        } else if (button == cancelAllButton && craftingMonitor.getTasks().size() > 0) {
            RefinedStorage.INSTANCE.network.sendToServer(new MessageCraftingMonitorCancel(craftingMonitor, -1));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 0 && inBounds(8, 20, 144, 90, mouseX - guiLeft, mouseY - guiTop)) {
            itemSelected = -1;

            int item = getOffset() * 2;

            for (int y = 0; y < 3; ++y) {
                for (int x = 0; x < 2; ++x) {
                    int ix = 8 + (x * ITEM_WIDTH);
                    int iy = 20 + (y * ITEM_HEIGHT);

                    if (inBounds(ix, iy, ITEM_WIDTH, ITEM_HEIGHT, mouseX - guiLeft, mouseY - guiTop) && item < craftingMonitor.getTasks().size()) {
                        itemSelected = item;
                    }

                    item++;
                }
            }
        }
    }
}
