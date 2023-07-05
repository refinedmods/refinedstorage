package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.lwjgl.opengl.GL11;

public abstract class SideButton extends Button {
    private static final Button.OnPress NO_ACTION = btn -> {
    };

    private static final int WIDTH = 18;
    private static final int HEIGHT = 18;

    protected final BaseScreen<?> screen;

    protected SideButton(BaseScreen<?> screen) {
        super(Button.builder(Component.empty(), NO_ACTION).pos(-1, -1).size(WIDTH, HEIGHT));
        this.screen = screen;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        isHovered = RenderUtils.inBounds(getX(), getY(), width, height, mouseX, mouseY);

        graphics.blit(BaseScreen.ICONS_TEXTURE, getX(), getY(), 238, isHovered ? 35 : 16, WIDTH, HEIGHT);

        renderButtonIcon(graphics, getX() + 1, getY() + 1);

        if (isHovered) {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.5f);
            graphics.blit(BaseScreen.ICONS_TEXTURE, getX(), getY(), 238, 54, WIDTH, HEIGHT);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.disableBlend();
            screen.renderTooltip(graphics, mouseX, mouseY, getSideButtonTooltip());
        }
    }

    protected abstract void renderButtonIcon(GuiGraphics graphics, int x, int y);

    protected abstract String getSideButtonTooltip();
}
