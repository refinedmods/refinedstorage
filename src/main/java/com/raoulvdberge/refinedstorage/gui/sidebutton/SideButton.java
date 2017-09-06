package com.raoulvdberge.refinedstorage.gui.sidebutton;

import com.raoulvdberge.refinedstorage.gui.GuiBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public abstract class SideButton extends GuiButton {
    public static final int WIDTH = 18;
    public static final int HEIGHT = 18;

    protected GuiBase gui;

    public SideButton(GuiBase gui) {
        super(-1, -1, -1, 18, 18, "");

        this.gui = gui;
    }

    public boolean isHovered() {
        return hovered;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableAlpha();

        hovered = gui.inBounds(x, y, width, height, mouseX, mouseY);

        gui.bindTexture("icons.png");
        gui.drawTexture(x, y, 238, hovered ? 35 : 16, 18, 18);

        drawButtonIcon(x + 1, y + 1);

        if (hovered) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 0.5f);
            gui.drawTexture(x, y, 238, 54, 18, 18);
            GlStateManager.disableBlend();
        }
    }

    protected abstract void drawButtonIcon(int x, int y);

    public abstract String getTooltip();

    public abstract void actionPerformed();
}
