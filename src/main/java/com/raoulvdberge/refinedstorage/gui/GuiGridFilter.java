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

    private GuiCheckBox compareDamage;
    private GuiCheckBox compareNBT;

    public GuiGridFilter(ContainerGridFilter container) {
        super(container, 176, 152);

        this.compare = ItemGridFilter.getCompare(container.getStack());
    }

    @Override
    public void init(int x, int y) {
        compareDamage = addCheckBox(x + 7, y + 41, t("gui.refinedstorage:grid_filter.compare_damage"), (compare & IComparer.COMPARE_DAMAGE) == IComparer.COMPARE_DAMAGE);
        compareNBT = addCheckBox(x + 7 + compareDamage.getButtonWidth() + 4, y + 41, t("gui.refinedstorage:grid_filter.compare_nbt"), (compare & IComparer.COMPARE_NBT) == IComparer.COMPARE_NBT);
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/grid_filter.png");

        drawTexture(x, y, 0, 0, screenWidth, screenHeight);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:grid_filter"));
        drawString(7, 58, t("container.inventory"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button == compareDamage) {
            compare ^= IComparer.COMPARE_DAMAGE;
        } else if (button == compareNBT) {
            compare ^= IComparer.COMPARE_NBT;
        }

        RS.INSTANCE.network.sendToServer(new MessageGridFilterUpdate(compare));
    }
}
