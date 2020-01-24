package com.raoulvdberge.refinedstorage.screen.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public class CheckboxWidget extends CheckboxButton {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
    private static final int BOX_WIDTH = 13;

    private final Consumer<CheckboxButton> onPress;
    private boolean shadow = true;

    public CheckboxWidget(int x, int y, String text, boolean isChecked, Consumer<CheckboxButton> onPress) {
        super(
            x,
            y,
            Minecraft.getInstance().fontRenderer.getStringWidth(text) + BOX_WIDTH,
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
    public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        Minecraft mc = Minecraft.getInstance();
        mc.getTextureManager().bindTexture(TEXTURE);
        RenderSystem.enableDepthTest();
        FontRenderer fontRenderer = mc.fontRenderer;
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        blit(this.x, this.y, 0.0F, this.checked ? 10.0F : 0.0F, 10, this.height, 16, 32);
        this.renderBg(mc, p_renderButton_1_, p_renderButton_2_);

        int color = 14737632;

        if (!active) {
            color = 10526880;
        } else if (packedFGColor != 0) {
            color = packedFGColor;
        }

        if (shadow) {
            super.drawString(fontRenderer, this.getMessage(), this.x + 13, this.y + (this.height - 8) / 2, color);
        } else {
            fontRenderer.drawString(this.getMessage(), this.x + 13, this.y + (this.height - 8) / 2F, color);
        }
    }
}
