package refinedstorage.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.container.ContainerCraftingMonitor;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.network.MessageCraftingMonitorCancel;
import refinedstorage.network.MessageCraftingMonitorSelect;
import refinedstorage.tile.TileCraftingMonitor;

import java.io.IOException;

public class GuiCraftingMonitor extends GuiBase {
    public static final int VISIBLE_ROWS = 3;

    public static final int ITEM_WIDTH = 72;
    public static final int ITEM_HEIGHT = 30;

    private TileCraftingMonitor craftingMonitor;

    private GuiButton cancelButton;
    private GuiButton cancelAllButton;

    private int selectionX = Integer.MAX_VALUE;
    private int selectionY = Integer.MAX_VALUE;

    private Scrollbar scrollbar = new Scrollbar(157, 20, 12, 89);

    public GuiCraftingMonitor(ContainerCraftingMonitor container, TileCraftingMonitor craftingMonitor) {
        super(container, 256, 230);

        this.craftingMonitor = craftingMonitor;
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(craftingMonitor));

        String cancel = t("gui.cancel");
        String cancelAll = t("misc.refinedstorage:cancel_all");

        int cancelButtonWidth = 14 + fontRendererObj.getStringWidth(cancel);
        int cancelAllButtonWidth = 14 + fontRendererObj.getStringWidth(cancelAll);

        cancelButton = addButton(x + 7, y + 113, cancelButtonWidth, 20, cancel, false);
        cancelAllButton = addButton(x + 7 + cancelButtonWidth + 4, y + 113, cancelAllButtonWidth, 20, cancelAll, false);
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

        cancelButton.enabled = craftingMonitor.hasSelection();
        cancelAllButton.enabled = craftingMonitor.getTasks().size() > 0;
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/crafting_monitor.png");

        drawTexture(x, y, 0, 0, width, height);

        scrollbar.draw(this);

        if (craftingMonitor.hasSelection()) {
            drawRect(x + selectionX, y + selectionY, x + selectionX + ITEM_WIDTH - 1, y + selectionY + ITEM_HEIGHT - 1, 0xFFCCCCCC);
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:crafting_monitor"));
        drawString(7, 137, t("container.inventory"));

        int x = 8;
        int y = 20;

        int id = getOffset() * 2;

        RenderHelper.enableGUIStandardItemLighting();

        for (int i = 0; i < 6; ++i) {
            if (id < craftingMonitor.getTasks().size()) {
                if (id == craftingMonitor.getSelected()) {
                    selectionX = x;
                    selectionY = y;
                }

                TileCraftingMonitor.ClientSideCraftingTask task = craftingMonitor.getTasks().get(i);

                drawItem(x + 4, y + 11, task.output);

                float scale = 0.5f;

                GlStateManager.pushMatrix();
                GlStateManager.scale(scale, scale, 1);

                drawString(
                    RefinedStorageUtils.calculateOffsetOnScale(x + 5, scale),
                    RefinedStorageUtils.calculateOffsetOnScale(y + 4, scale),
                    task.output.getDisplayName()
                );

                GlStateManager.popMatrix();
            }

            if (i == 1 || i == 3) {
                x = 8;
                y += ITEM_HEIGHT;
            } else {
                x += ITEM_WIDTH;
            }

            id++;
        }

        if (craftingMonitor.hasSelection()) {
            float scale = 0.5f;

            x = 179;
            y = 24;

            for (Object item : craftingMonitor.getInfo()) {
                if (item instanceof String) {
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(scale, scale, 1);

                    String text = (String) item;

                    drawString(
                        RefinedStorageUtils.calculateOffsetOnScale(x, scale),
                        RefinedStorageUtils.calculateOffsetOnScale(y, scale),
                        text.startsWith("T=") ? t(text.substring(2)) : text
                    );

                    GlStateManager.popMatrix();
                } else if (item instanceof ItemStack) {
                    drawItem(x, y, (ItemStack) item);

                    GlStateManager.pushMatrix();
                    GlStateManager.scale(scale, scale, 1);

                    drawString(
                        RefinedStorageUtils.calculateOffsetOnScale(x + 20, scale),
                        RefinedStorageUtils.calculateOffsetOnScale(y + 6, scale),
                        ((ItemStack) item).getDisplayName()
                    );

                    GlStateManager.popMatrix();
                }

                y += item instanceof String ? 7 : 16;
            }
        }
    }

    public int getOffset() {
        return (int) (scrollbar.getCurrentScroll() / 89f * (float) getRows());
    }

    private int getRows() {
        int max = (int) Math.ceil((float) craftingMonitor.getTasks().size() / (float) 2);

        return max < 0 ? 0 : max;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button == cancelButton && craftingMonitor.hasSelection()) {
            RefinedStorage.NETWORK.sendToServer(new MessageCraftingMonitorCancel(craftingMonitor, craftingMonitor.getTasks().get(craftingMonitor.getSelected()).id));
        } else if (button == cancelAllButton && craftingMonitor.getTasks().size() > 0) {
            RefinedStorage.NETWORK.sendToServer(new MessageCraftingMonitorCancel(craftingMonitor, -1));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        boolean resetSelection = !inBounds(174, 19, 77, 91, mouseX - guiLeft, mouseY - guiTop);

        if (mouseButton == 0 && inBounds(8, 20, 144, 90, mouseX - guiLeft, mouseY - guiTop)) {
            int id = getOffset() * 2;

            for (int y = 0; y < 3; ++y) {
                for (int x = 0; x < 2; ++x) {
                    int ix = 8 + (x * ITEM_WIDTH);
                    int iy = 20 + (y * ITEM_HEIGHT);

                    if (inBounds(ix, iy, ITEM_WIDTH, ITEM_HEIGHT, mouseX - guiLeft, mouseY - guiTop) && id < craftingMonitor.getTasks().size()) {
                        RefinedStorage.NETWORK.sendToServer(new MessageCraftingMonitorSelect(craftingMonitor, craftingMonitor.getTasks().get(id).id));

                        craftingMonitor.setSelected(id);

                        resetSelection = false;
                    }

                    id++;
                }
            }
        }

        if (resetSelection) {
            RefinedStorage.NETWORK.sendToServer(new MessageCraftingMonitorSelect(craftingMonitor, -1));

            craftingMonitor.setSelected(-1);
        }
    }
}
