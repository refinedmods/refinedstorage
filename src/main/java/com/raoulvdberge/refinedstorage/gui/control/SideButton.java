package com.raoulvdberge.refinedstorage.gui.control;

import com.mojang.blaze3d.platform.GlStateManager;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import net.minecraft.client.gui.widget.button.Button;
import org.lwjgl.opengl.GL11;

public abstract class SideButton extends Button {
    public static final int WIDTH = 18;
    public static final int HEIGHT = 18;

    protected GuiBase gui;

    public SideButton(GuiBase gui) {
        super(-1, -1, 18, 18, "", (btn) -> {
            // TODO: call ActionPerformed
        });

        this.gui = gui;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableAlphaTest();

        isHovered = gui.inBounds(x, y, width, height, mouseX, mouseY);

        gui.bindTexture("icons.png");
        gui.drawTexture(x, y, 238, isHovered ? 35 : 16, 18, 18);

        drawButtonIcon(x + 1, y + 1);

        if (isHovered) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 0.5f);
            gui.drawTexture(x, y, 238, 54, 18, 18);
            GlStateManager.disableBlend();
        }
    }

    protected abstract void drawButtonIcon(int x, int y);

    public abstract String getTooltip();

    public abstract void actionPerformed();
}
