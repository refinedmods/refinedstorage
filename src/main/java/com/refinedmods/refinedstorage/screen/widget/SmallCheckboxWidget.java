package com.refinedmods.refinedstorage.screen.widget;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SmallCheckboxWidget extends AbstractButton {
    private static final ResourceLocation CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE =
        new ResourceLocation("widget/checkbox_selected_highlighted");
    private static final ResourceLocation CHECKBOX_SELECTED_SPRITE = new ResourceLocation("widget/checkbox_selected");
    private static final ResourceLocation CHECKBOX_HIGHLIGHTED_SPRITE =
        new ResourceLocation("widget/checkbox_highlighted");
    private static final ResourceLocation CHECKBOX_SPRITE = new ResourceLocation("widget/checkbox");

    private static final int BOX_WIDTH = 13;

    private final Consumer<SmallCheckboxWidget> onPress;
    private boolean shadow = true;
    private boolean selected;

    public SmallCheckboxWidget(int x, int y, Component text, boolean isSelected, Consumer<SmallCheckboxWidget> onPress) {
        super(
            x,
            y,
            Minecraft.getInstance().font.width(text.getString()) + BOX_WIDTH + 4,
            10,
            text
        );
        this.selected = isSelected;
        this.onPress = onPress;
    }

    public void setSelected(boolean value) {
        this.selected = value;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    @Override
    public void onPress() {
        this.selected = !selected;
        this.onPress.accept(this);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        ResourceLocation texture;
        if (this.selected) {
            texture = isFocused() ? CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE : CHECKBOX_SELECTED_SPRITE;
        } else {
            texture = isFocused() ? CHECKBOX_HIGHLIGHTED_SPRITE : CHECKBOX_SPRITE;
        }

        int color = 14737632;

        if (!active) {
            color = 10526880;
        } else if (packedFGColor != 0) {
            color = packedFGColor;
        }

        int i = BOX_WIDTH;
        int j = this.getX() + i + 4;
        int k = this.getY() + (this.height - 8) / 2;
        graphics.blitSprite(texture, this.getX(), this.getY(), i, i);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.drawString(Minecraft.getInstance().font, this.getMessage(), j, k, color, shadow);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, this.createNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                output.add(NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.focused"));
            } else {
                output.add(NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.hovered"));
            }
        }
    }
}
