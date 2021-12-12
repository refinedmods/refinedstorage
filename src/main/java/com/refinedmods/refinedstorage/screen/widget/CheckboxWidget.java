package com.refinedmods.refinedstorage.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.function.Consumer;

public class CheckboxWidget extends CheckboxButton {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
    private static final int BOX_WIDTH = 13;

    private final Consumer<CheckboxButton> onPress;
    private boolean shadow = true;

    public CheckboxWidget(int x, int y, ITextComponent text, boolean isChecked, Consumer<CheckboxButton> onPress) {
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
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bind(TEXTURE);
        RenderSystem.enableDepthTest();
        FontRenderer fontRenderer = minecraft.font;
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        float textureX = (this.isFocused() ? 10.0F : 0.0F);
        float textureY = (this.selected ? 10.0F : 0.0F);

        int width = 10;
        int height = 10;

        blit(matrixStack, this.x, this.y, textureX, textureY, width, height, 32, 32);

        this.renderBg(matrixStack, minecraft, mouseX, mouseY);

        int color = 14737632;

        if (!active) {
            color = 10526880;
        } else if (packedFGColor != 0) {
            color = packedFGColor;
        }

        if (shadow) {
            drawString(matrixStack, fontRenderer, this.getMessage(), this.x + 13, this.y + (this.height - 8) / 2, color);
        } else {
            fontRenderer.draw(matrixStack, this.getMessage().getString(), (float) this.x + 13, this.y + (this.height - 8) / 2F, color);
        }
    }
}
