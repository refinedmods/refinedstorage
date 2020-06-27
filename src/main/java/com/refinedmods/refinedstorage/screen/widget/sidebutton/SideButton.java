package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.gui.widget.button.Button;
import org.lwjgl.opengl.GL11;

public abstract class SideButton extends Button {
    public static final int WIDTH = 18;
    public static final int HEIGHT = 18;

    protected final BaseScreen<?> screen;

    public SideButton(BaseScreen<?> screen) {
        super(-1, -1, 18, 18, "", btn -> {
        });

        this.screen = screen;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableAlphaTest();

        isHovered = RenderUtils.inBounds(x, y, width, height, mouseX, mouseY);

        screen.bindTexture(RS.ID, "icons.png");
        screen.blit(x, y, 238, isHovered ? 35 : 16, 18, 18);

        renderButtonIcon(x + 1, y + 1);

        if (isHovered) {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 0.5f);
            screen.blit(x, y, 238, 54, 18, 18);
            RenderSystem.disableBlend();
        }
    }

    protected abstract void renderButtonIcon(int x, int y);

    public abstract String getTooltip();
}
