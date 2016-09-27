package refinedstorage.gui.sidebutton;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import refinedstorage.gui.GuiBase;

public abstract class SideButton extends GuiButton {
    public static final int WIDTH = 18;
    public static final int HEIGHT = 18;

    protected GuiBase gui;

    public SideButton(GuiBase gui) {
        super(-1, -1, -1, 18, 18, "");

        this.gui = gui;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        gui.bindTexture("icons.png");
        gui.drawTexture(xPosition, yPosition, 238, 16, 18, 18);
    }

    public abstract String getTooltip(GuiBase gui);

    public abstract void actionPerformed();
}
