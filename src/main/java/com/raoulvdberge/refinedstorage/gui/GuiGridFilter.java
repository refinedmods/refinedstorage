package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.container.ContainerGridFilter;
import com.raoulvdberge.refinedstorage.item.ItemGridFilter;
import com.raoulvdberge.refinedstorage.network.MessageGridFilterUpdate;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.GuiCheckBox;

import java.io.IOException;

public class GuiGridFilter extends GuiBase {
    private int compare;
    private int mode;
    private boolean modFilter;

    private GuiCheckBox compareDamage;
    private GuiCheckBox compareNBT;
    private GuiCheckBox compareOredict;
    private GuiCheckBox toggleModFilter;
    private GuiButton toggleMode;

    public GuiGridFilter(ContainerGridFilter container) {
        super(container, 176, 208);

        this.compare = ItemGridFilter.getCompare(container.getStack());
        this.mode = ItemGridFilter.getMode(container.getStack());
        this.modFilter = ItemGridFilter.isModFilter(container.getStack());
    }

    @Override
    public void init(int x, int y) {
        compareDamage = addCheckBox(x + 7, y + 77, t("gui.refinedstorage:grid_filter.compare_damage"), (compare & IComparer.COMPARE_DAMAGE) == IComparer.COMPARE_DAMAGE);
        compareNBT = addCheckBox(x + 7 + compareDamage.getButtonWidth() + 4, y + 77, t("gui.refinedstorage:grid_filter.compare_nbt"), (compare & IComparer.COMPARE_NBT) == IComparer.COMPARE_NBT);
        compareOredict = addCheckBox(x + 7 + compareDamage.getButtonWidth() + 4 + compareNBT.getButtonWidth() + 4, y + 77, t("gui.refinedstorage:grid_filter.compare_oredict"), (compare & IComparer.COMPARE_OREDICT) == IComparer.COMPARE_OREDICT);
        toggleModFilter = addCheckBox(0, y + 71 + 25, t("gui.refinedstorage:grid_filter.mod_filter"), modFilter);
        toggleMode = addButton(x + 7, y + 71 + 21, 0, 20, "");
        updateModeButton(mode);
    }

    private void updateModeButton(int mode) {
        String text = mode == ItemGridFilter.MODE_WHITELIST ? t("sidebutton.refinedstorage:mode.whitelist") : t("sidebutton.refinedstorage:mode.blacklist");
        toggleMode.setWidth(fontRendererObj.getStringWidth(text) + 12);
        toggleMode.displayString = text;
        toggleModFilter.xPosition = toggleMode.xPosition + toggleMode.getButtonWidth() + 4;
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/grid_filter.png");

        drawTexture(x, y, 0, 0, width, height);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:grid_filter"));
        drawString(7, 115, t("container.inventory"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button == compareDamage) {
            compare ^= IComparer.COMPARE_DAMAGE;
        } else if (button == compareNBT) {
            compare ^= IComparer.COMPARE_NBT;
        } else if (button == compareOredict) {
            compare ^= IComparer.COMPARE_OREDICT;
        } else if (button == toggleMode) {
            mode = mode == ItemGridFilter.MODE_WHITELIST ? ItemGridFilter.MODE_BLACKLIST : ItemGridFilter.MODE_WHITELIST;
            updateModeButton(mode);
        } else if (button == toggleModFilter) {
            modFilter = !modFilter;
        }

        RS.INSTANCE.network.sendToServer(new MessageGridFilterUpdate(compare, mode, modFilter));
    }
}
