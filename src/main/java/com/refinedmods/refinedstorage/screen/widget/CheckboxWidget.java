package com.refinedmods.refinedstorage.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
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
            Minecraft.getInstance().fontRenderer.getStringWidth(text.getString()) + BOX_WIDTH,
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
        this.checked = value;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(TEXTURE);
        RenderSystem.enableDepthTest();
        FontRenderer fontrenderer = minecraft.fontRenderer;
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        float textureX = (this.isFocused() ? 10.0F : 0.0F);
        float textureY = (this.checked ? 10.0F : 0.0F);

        int width = 10;
        int height = 10;

        blit(matrixStack, this.x, this.y, textureX, textureY, width, height, 32, 32);

        this.renderBg(matrixStack, minecraft, p_230431_2_, p_230431_3_);

        int color = 14737632;

        if (!active) {
            color = 10526880;
        } else if (packedFGColor != 0) {
            color = packedFGColor;
        }

        if (shadow) {
            super.drawString(matrixStack, fontrenderer, this.getMessage(), this.x + 13, this.y + (this.height - 8) / 2, color);
        } else {
            fontrenderer.drawString(matrixStack, this.getMessage().getString(), this.x + 13, this.y + (this.height - 8) / 2F, color);
        }
    }
}
