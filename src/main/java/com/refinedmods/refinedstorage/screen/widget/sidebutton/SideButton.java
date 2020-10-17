package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;

public abstract class SideButton extends Button {
    private static final Button.IPressable NO_ACTION = btn -> {
    };

    private static final int WIDTH = 18;
    private static final int HEIGHT = 18;

    protected final BaseScreen<?> screen;

    protected SideButton(BaseScreen<?> screen) {
        super(-1, -1, WIDTH, HEIGHT, StringTextComponent.EMPTY, NO_ACTION);

        this.screen = screen;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableAlphaTest();

        isHovered = RenderUtils.inBounds(x, y, width, height, mouseX, mouseY);

        screen.bindTexture(RS.ID, "icons.png");
        screen.blit(matrixStack, x, y, 238, isHovered ? 35 : 16, WIDTH, HEIGHT);

        renderButtonIcon(matrixStack, x + 1, y + 1);

        if (isHovered) {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 0.5f);
            screen.blit(matrixStack, x, y, 238, 54, WIDTH, HEIGHT);
            RenderSystem.disableBlend();
        }
    }

    public int getHeight() {
        return height;
    }

    protected abstract void renderButtonIcon(MatrixStack matrixStack, int x, int y);

    public abstract String getTooltip();
}
