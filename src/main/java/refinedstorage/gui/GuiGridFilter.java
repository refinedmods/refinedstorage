package refinedstorage.gui;

import net.minecraftforge.fml.client.config.GuiCheckBox;
import refinedstorage.container.ContainerGridFilter;

public class GuiGridFilter extends GuiBase {
    private GuiCheckBox compareDamage;
    private GuiCheckBox compareNBT;

    public GuiGridFilter(ContainerGridFilter container) {
        super(container, 176, 152);
    }

    @Override
    public void init(int x, int y) {
        compareDamage = addCheckBox(x + 7, y + 41, t("gui.refinedstorage:grid_filter.compare_damage"), false);
        compareNBT = addCheckBox(x + 7 + compareDamage.getButtonWidth() + 4, y + 41, t("gui.refinedstorage:grid_filter.compare_nbt"), false);
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
        drawString(7, 58, t("container.inventory"));
    }
}
