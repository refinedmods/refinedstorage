package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.container.ContainerBase;
import com.raoulvdberge.refinedstorage.gui.sidebutton.*;
import com.raoulvdberge.refinedstorage.tile.IStorageGui;

public class GuiStorage extends GuiBase {
    private IStorageGui gui;
    private String texture;

    private int barX = 8;
    private int barY = 54;
    private int barWidth = 16;
    private int barHeight = 70;

    public GuiStorage(ContainerBase container, IStorageGui gui, String texture) {
        super(container, 176, 223);

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

        String txt = "Priority"; // @TODO: I18n
        int bw = 10 + fontRendererObj.getStringWidth(txt);
        addButton(x + 169 - bw, y + 41, bw, 20, txt);
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
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t(gui.getGuiTitle()));
        drawString(7, 42, gui.getCapacity() == -1 ?
            t("misc.refinedstorage:storage.stored_minimal", RSUtils.formatQuantity(gui.getStored())) :
            t("misc.refinedstorage:storage.stored_capacity_minimal", RSUtils.formatQuantity(gui.getStored()), RSUtils.formatQuantity(gui.getCapacity())));

        // @TODO: I18n
        if (texture.contains("disk_drive")) { // HACK!
            drawString(70, 42, "Disks");
        }

        drawString(7, 129, t("container.inventory"));

        if (inBounds(barX, barY, barWidth, barHeight, mouseX, mouseY)) {
            int full = 0;

            if (gui.getCapacity() >= 0) {
                full = (int) ((float) gui.getStored() / (float) gui.getCapacity() * 100f);
            }

            drawTooltip(mouseX, mouseY, t("misc.refinedstorage:storage.full", full));
        }
    }

    // @TODO: Remove
    public void updatePriority(int priority) {
    }
}
