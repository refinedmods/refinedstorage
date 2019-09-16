package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.IGuiStorage;
import com.raoulvdberge.refinedstorage.container.ContainerBase;
import com.raoulvdberge.refinedstorage.gui.widget.sidebutton.*;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.TextFormatting;

public class GuiStorage extends GuiBase<ContainerBase> {
    private IGuiStorage gui;
    private String texture;

    private Button priorityButton;

    private int barX = 8;
    private int barY = 54;
    private int barWidth = 16;
    private int barHeight = 70;

    public GuiStorage(ContainerBase container, IGuiStorage gui, String texture, PlayerInventory inventory) {
        super(container, 176, 223, inventory, null);

        this.gui = gui;
        this.texture = texture;
    }

    public GuiStorage(ContainerBase container, IGuiStorage gui, PlayerInventory inventory) {
        this(container, gui, "gui/storage.png", inventory);
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
            addSideButton(new SideButtonCompare(this, gui.getCompareParameter(), IComparer.COMPARE_NBT));
        }

        if (gui.getAccessTypeParameter() != null) {
            addSideButton(new SideButtonAccessType(this, gui.getAccessTypeParameter()));
        }

        int buttonWidth = 10 + font.getStringWidth(I18n.format("misc.refinedstorage:priority"));

        priorityButton = addButton(x + 169 - buttonWidth, y + 41, buttonWidth, 20, I18n.format("misc.refinedstorage:priority"), true, true, btn -> {
        });
    }

    @Override
    public void tick(int x, int y) {
    }

    @Override
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(texture);

        blit(x, y, 0, 0, xSize, ySize);

        int barHeightNew = (int) ((float) gui.getStored() / (float) gui.getCapacity() * (float) barHeight);

        blit(x + barX, y + barY + barHeight - barHeightNew, 179, barHeight - barHeightNew, barWidth, barHeightNew);
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, I18n.format(gui.getGuiTitle()));
        renderString(7, 42, gui.getCapacity() == -1 ?
            I18n.format("misc.refinedstorage.storage.stored_minimal", API.instance().getQuantityFormatter().formatWithUnits(gui.getStored())) :
            I18n.format("misc.refinedstorage.storage.stored_capacity_minimal", API.instance().getQuantityFormatter().formatWithUnits(gui.getStored()), API.instance().getQuantityFormatter().formatWithUnits(gui.getCapacity()))
        );

        if (texture.contains("disk_drive")) { // HACK!
            renderString(79, 42, I18n.format("gui.refinedstorage:disk_drive.disks"));
        }

        renderString(7, 129, I18n.format("container.inventory"));

        if (inBounds(barX, barY, barWidth, barHeight, mouseX, mouseY)) {
            int full = 0;

            if (gui.getCapacity() >= 0) {
                full = (int) ((float) gui.getStored() / (float) gui.getCapacity() * 100f);
            }

            renderTooltip(mouseX, mouseY, (gui.getCapacity() == -1 ?
                I18n.format("misc.refinedstorage.storage.stored_minimal", API.instance().getQuantityFormatter().format(gui.getStored())) :
                I18n.format("misc.refinedstorage.storage.stored_capacity_minimal", API.instance().getQuantityFormatter().format(gui.getStored()), API.instance().getQuantityFormatter().format(gui.getCapacity()))
            ) + "\n" + TextFormatting.GRAY + I18n.format("misc.refinedstorage.storage.full", full));
        }
    }

    /* TODO
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button == priorityButton) {
            FMLCommonHandler.instance().showGuiScreen(new GuiPriority(this, gui.getPriorityParameter()));
        }
    }*/
}
