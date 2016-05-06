package refinedstorage.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorage;
import refinedstorage.container.ContainerCraftingMonitor;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.network.MessageCraftingMonitorCancel;
import refinedstorage.tile.autocrafting.TileCraftingMonitor;
import scala.actors.threadpool.Arrays;

import java.io.IOException;
import java.util.List;

public class GuiCraftingMonitor extends GuiBase {
    public static final int VISIBLE_ROWS = 3;

    public static final int ITEM_WIDTH = 72;
    public static final int ITEM_HEIGHT = 30;

    private TileCraftingMonitor craftingMonitor;

    private GuiButton cancelButton;

    private int itemSelected = -1;
    private int itemSelectedX;
    private int itemSelectedY;

    private Scrollbar scrollbar = new Scrollbar(157, 20, 12, 89);

    public GuiCraftingMonitor(ContainerCraftingMonitor container, TileCraftingMonitor craftingMonitor) {
        super(container, 176, 230);

        this.craftingMonitor = craftingMonitor;
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(craftingMonitor));

        cancelButton = addButton(x + 7, y + 113, 50, 20, t("misc.refinedstorage:cancel"));
    }

    @Override
    public void update(int x, int y) {
        scrollbar.setCanScroll(getRows() > VISIBLE_ROWS);
        scrollbar.setScrollDelta((float) scrollbar.getScrollbarHeight() / (float) getRows());

        if (itemSelected >= craftingMonitor.getTasks().size()) {
            itemSelected = -1;
        }

        cancelButton.enabled = itemSelected != -1;
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/crafting_monitor.png");

        drawTexture(x, y, 0, 0, width, height);

        if (itemSelected != -1) {
            drawTexture(x + itemSelectedX, y + itemSelectedY, 178, 0, ITEM_WIDTH, ITEM_HEIGHT);
        }

        scrollbar.draw(this);
    }

    private int calculateOffsetOnScale(int pos, float scale) {
        float multiplier = (pos / scale);
        return (int) multiplier;
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        scrollbar.update(this, mouseX, mouseY);

        drawString(7, 7, t("gui.refinedstorage:crafting_monitor"));
        drawString(7, 137, t("container.inventory"));

        int ox = 11;
        int x = ox;
        int y = 26;

        int item = getOffset() * 2;

        RenderHelper.enableGUIStandardItemLighting();

        List<ItemStack> tasks = craftingMonitor.getTasks();

        List<String> infoLines = null;

        for (int i = 0; i < 6; ++i) {
            if (item < tasks.size() && item < craftingMonitor.getInfo().length) {
                ItemStack task = tasks.get(item);

                drawItem(x, y + 5, task);

                GlStateManager.pushMatrix();

                float scale = 0.5f;
                GlStateManager.scale(scale, scale, 1);
                drawString(calculateOffsetOnScale(x + 1, scale), calculateOffsetOnScale(y - 3, scale), task.getDisplayName());

                GlStateManager.popMatrix();

                if (inBounds(x, y + 5, 16, 16, mouseX, mouseY)) {
                    infoLines = Arrays.asList(craftingMonitor.getInfo()[item].split("\n"));

                    for (int j = 0; j < infoLines.size(); ++j) {
                        String line = infoLines.get(j);

                        infoLines.set(j, line
                            .replace("{missing_items}", t("gui.refinedstorage:crafting_monitor.missing_items"))
                            .replace("{items_crafting}", t("gui.refinedstorage:crafting_monitor.items_crafting"))
                            .replace("{items_processing}", t("gui.refinedstorage:crafting_monitor.items_processing"))
                            .replace("{none}", t("misc.refinedstorage:none")));
                    }
                }
            }

            if (i == 1 || i == 3) {
                x = ox;
                y += 30;
            } else {
                x += 75;
            }

            item++;
        }

        if (infoLines != null) {
            drawTooltip(mouseX, mouseY, infoLines);
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

        if (button == cancelButton && itemSelected != -1) {
            RefinedStorage.NETWORK.sendToServer(new MessageCraftingMonitorCancel(craftingMonitor, itemSelected));
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
                        itemSelectedX = ix;
                        itemSelectedY = iy;
                    }

                    item++;
                }
            }
        }
    }
}
