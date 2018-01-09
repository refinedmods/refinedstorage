package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.IGuiStorage;
import com.raoulvdberge.refinedstorage.container.ContainerBase;
import com.raoulvdberge.refinedstorage.gui.sidebutton.*;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.IOException;

public class GuiStorage extends GuiBase {
    private IGuiStorage gui;
    private String texture;

    private GuiButton priorityButton;

    private int barX = 8;
    private int barY = 54;
    private int barWidth = 16;
    private int barHeight = 70;

    public GuiStorage(ContainerBase container, IGuiStorage gui, String texture) {
        super(container, 176, 223);

        this.gui = gui;
        this.texture = texture;
    }

    public GuiStorage(ContainerBase container, IGuiStorage gui) {
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

        int buttonWidth = 10 + fontRenderer.getStringWidth(t("misc.refinedstorage:priority"));

        priorityButton = addButton(x + 169 - buttonWidth, y + 41, buttonWidth, 20, t("misc.refinedstorage:priority"));
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(texture);

        drawTexture(x, y, 0, 0, screenWidth, screenHeight);

        int barHeightNew = (int) ((float) gui.getStored() / (float) gui.getCapacity() * (float) barHeight);

        drawTexture(x + barX, y + barY + barHeight - barHeightNew, 179, barHeight - barHeightNew, barWidth, barHeightNew);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t(gui.getGuiTitle()));
        drawString(7, 42, gui.getCapacity() == -1 ?
            t("misc.refinedstorage:storage.stored_minimal", API.instance().getQuantityFormatter().formatWithUnits(gui.getStored())) :
            t("misc.refinedstorage:storage.stored_capacity_minimal", API.instance().getQuantityFormatter().formatWithUnits(gui.getStored()), API.instance().getQuantityFormatter().formatWithUnits(gui.getCapacity()))
        );

        if (texture.contains("disk_drive")) { // HACK!
            drawString(79, 42, t("gui.refinedstorage:disk_drive.disks"));
        }

        drawString(7, 129, t("container.inventory"));

        if (inBounds(barX, barY, barWidth, barHeight, mouseX, mouseY)) {
            int full = 0;

            if (gui.getCapacity() >= 0) {
                full = (int) ((float) gui.getStored() / (float) gui.getCapacity() * 100f);
            }

            drawTooltip(mouseX, mouseY, (gui.getCapacity() == -1 ?
                t("misc.refinedstorage:storage.stored_minimal", API.instance().getQuantityFormatter().format(gui.getStored())) :
                t("misc.refinedstorage:storage.stored_capacity_minimal", API.instance().getQuantityFormatter().format(gui.getStored()), API.instance().getQuantityFormatter().format(gui.getCapacity()))
            ) + "\n" + TextFormatting.GRAY + t("misc.refinedstorage:storage.full", full));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button == priorityButton) {
            FMLCommonHandler.instance().showGuiScreen(new GuiPriority(this, gui.getPriorityParameter()));
        }
    }
}
