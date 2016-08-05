package refinedstorage.gui;

import com.google.common.primitives.Ints;
import net.minecraft.client.gui.GuiTextField;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.container.ContainerStorage;
import refinedstorage.gui.sidebutton.SideButtonCompare;
import refinedstorage.gui.sidebutton.SideButtonMode;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.tile.IStorageGui;
import refinedstorage.tile.data.TileDataManager;

import java.io.IOException;

public class GuiStorage extends GuiBase {
    private IStorageGui gui;
    private String texture;

    public static GuiTextField PRIORITY;

    private int barX = 8;
    private int barY = 54;
    private int barWidth = 16;
    private int barHeight = 58;

    public GuiStorage(ContainerStorage container, IStorageGui gui, String texture) {
        super(container, 176, 211);

        this.gui = gui;
        this.texture = texture;
    }

    public GuiStorage(ContainerStorage container, IStorageGui gui) {
        this(container, gui, "gui/storage.png");
    }

    @Override
    public void init(int x, int y) {
        if (gui.getRedstoneModeParameter() != null) {
            addSideButton(new SideButtonRedstoneMode(gui.getRedstoneModeParameter()));
        }

        if (gui.getFilterParameter() != null) {
            addSideButton(new SideButtonMode(gui.getFilterParameter()));
        }

        if (gui.getCompareParameter() != null) {
            addSideButton(new SideButtonCompare(gui.getCompareParameter(), CompareUtils.COMPARE_DAMAGE));
            addSideButton(new SideButtonCompare(gui.getCompareParameter(), CompareUtils.COMPARE_NBT));
        }

        PRIORITY = new GuiTextField(0, fontRendererObj, x + 98 + 1, y + 54 + 1, 25, fontRendererObj.FONT_HEIGHT);
        PRIORITY.setText(String.valueOf(gui.getPriorityParameter().getValue()));
        PRIORITY.setEnableBackgroundDrawing(false);
        PRIORITY.setVisible(true);
        PRIORITY.setTextColor(16777215);
        PRIORITY.setCanLoseFocus(true);
        PRIORITY.setFocused(false);
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(texture);

        drawTexture(x, y, 0, 0, width, height);

        int barHeightNew = (int) ((float) gui.getStored() / (float) gui.getCapacity() * (float) barHeight);

        drawTexture(x + barX, y + barY + barHeight - barHeightNew, 179, barHeight - barHeightNew, barWidth, barHeightNew);

        PRIORITY.drawTextBox();
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t(gui.getGuiTitle()));
        drawString(7, 42, gui.getCapacity() == -1 ? t("misc.refinedstorage:storage.stored_minimal", gui.getStored()) : t("misc.refinedstorage:storage.stored_capacity_minimal", gui.getStored(), gui.getCapacity()));
        drawString(97, 42, t("misc.refinedstorage:priority"));
        drawString(7, 117, t("container.inventory"));

        if (inBounds(barX, barY, barWidth, barHeight, mouseX, mouseY)) {
            int full = 0;

            if (gui.getCapacity() >= 0) {
                full = (int) ((float) gui.getStored() / (float) gui.getCapacity() * 100f);
            }

            drawTooltip(mouseX, mouseY, t("misc.refinedstorage:storage.full", full));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        PRIORITY.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        if (!checkHotbarKeys(keyCode) && PRIORITY.textboxKeyTyped(character, keyCode)) {
            Integer result = Ints.tryParse(PRIORITY.getText());

            if (result != null) {
                TileDataManager.setParameter(gui.getPriorityParameter(), result);
            }
        } else {
            super.keyTyped(character, keyCode);
        }
    }
}
