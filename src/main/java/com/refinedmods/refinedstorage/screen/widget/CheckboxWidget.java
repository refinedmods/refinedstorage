package com.refinedmods.refinedstorage.screen.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class CheckboxWidget extends Checkbox {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
    private static final int BOX_WIDTH = 13;

    private final Consumer<Checkbox> onPress;
    private boolean shadow = true;

    public CheckboxWidget(int x, int y, Component text, boolean isChecked, Consumer<Checkbox> onPress) {
        super(
            x,
            y,
            Minecraft.getInstance().font.width(text.getString()) + BOX_WIDTH,
            10,
            text,
            isChecked
        );

        this.onPress = onPress;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    @Override
    public void onPress() {
        super.onPress();

        this.onPress.accept(this);
    }

    public void setChecked(boolean value) {
        this.selected = value;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.enableDepthTest();
        Font font = minecraft.font;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        float textureX = (this.isFocused() ? 10.0F : 0.0F);
        float textureY = (this.selected ? 10.0F : 0.0F);

        int width = 10;
        int height = 10;

        graphics.blit(TEXTURE, this.getX(), this.getY(), textureX, textureY, width, height, 32, 32);

        int color = 14737632;

        if (!active) {
            color = 10526880;
        } else if (packedFGColor != 0) {
            color = packedFGColor;
        }

        graphics.drawString(font, this.getMessage(), this.getX() + 13, this.getY() + (this.height - 8) / 2, color, shadow);
    }
}
