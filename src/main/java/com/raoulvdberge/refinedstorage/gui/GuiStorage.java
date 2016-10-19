package com.raoulvdberge.refinedstorage.gui;

import com.google.common.primitives.Ints;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.container.ContainerBase;
import com.raoulvdberge.refinedstorage.gui.sidebutton.*;
import com.raoulvdberge.refinedstorage.tile.IStorageGui;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import net.minecraft.client.gui.GuiTextField;

import java.io.IOException;

public class GuiStorage extends GuiBase {
    private IStorageGui gui;
    private String texture;

    private GuiTextField priorityField;

    private int barX = 8;
    private int barY = 54;
    private int barWidth = 16;
    private int barHeight = 58;

    public GuiStorage(ContainerBase container, IStorageGui gui, String texture) {
        super(container, 176, 211);

        this.gui = gui;
        this.texture = texture;
    }

    public GuiStorage(ContainerBase container, IStorageGui gui) {
        this(container, gui, "gui/storage.png");
    }

    @Override
    public void init(int x, int y) {
        if (gui.getRedstoneModeParameter() != null) {
            addSideButton(new SideButtonRedstoneMode(this, gui.getRedstoneModeParameter()));
        }

        if (gui.getTypeParameter() != null) {
            addSideButton(new SideButtonType(this, gui.getTypeParameter()));
        }

        if (gui.getFilterParameter() != null) {
            addSideButton(new SideButtonMode(this, gui.getFilterParameter()));
        }

        if (gui.getCompareParameter() != null) {
            addSideButton(new SideButtonCompare(this, gui.getCompareParameter(), IComparer.COMPARE_DAMAGE));
            addSideButton(new SideButtonCompare(this, gui.getCompareParameter(), IComparer.COMPARE_NBT));
            addSideButton(new SideButtonCompare(this, gui.getCompareParameter(), IComparer.COMPARE_OREDICT));
        }

        if (gui.getVoidExcessParameter() != null) {
            addSideButton(new SideButtonVoidExcess(this, gui.getVoidExcessParameter(), gui.getVoidExcessType()));
        }

        if (gui.getAccessTypeParameter() != null) {
            addSideButton(new SideButtonAccessType(this, gui.getAccessTypeParameter()));
        }

        priorityField = new GuiTextField(0, fontRendererObj, x + 98 + 1, y + 54 + 1, 29, fontRendererObj.FONT_HEIGHT);
        priorityField.setEnableBackgroundDrawing(false);
        priorityField.setVisible(true);
        priorityField.setTextColor(16777215);
        priorityField.setCanLoseFocus(true);
        priorityField.setFocused(false);

        updatePriority(gui.getPriorityParameter().getValue());
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

        priorityField.drawTextBox();
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

        priorityField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        if (checkHotbarKeys(keyCode)) {
            // NO OP
        } else if (priorityField.textboxKeyTyped(character, keyCode)) {
            Integer result = Ints.tryParse(priorityField.getText());

            if (result != null) {
                TileDataManager.setParameter(gui.getPriorityParameter(), result);
            }
        } else {
            super.keyTyped(character, keyCode);
        }
    }

    public void updatePriority(int priority) {
        if (priorityField != null) {
            priorityField.setText(String.valueOf(priority));
        }
    }
}
