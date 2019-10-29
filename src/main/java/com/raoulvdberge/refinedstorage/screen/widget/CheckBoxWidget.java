package com.raoulvdberge.refinedstorage.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiUtils;

// @Volatile: Copied from Forge 1.12. Remove this if GuiCheckBox gets ported over correctly.
public class CheckBoxWidget extends GuiCheckBox {
    private static final int BOX_WIDTH = 13;

    private IPressable onPress;
    private String displayString;

    public CheckBoxWidget(int xPos, int yPos, String displayString, boolean isChecked, IPressable onPress) {
        super(xPos, yPos, displayString, isChecked);

        this.onPress = onPress;
        this.displayString = displayString;
        this.width = Minecraft.getInstance().fontRenderer.getStringWidth(displayString) + BOX_WIDTH + 3;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partial) {
        if (visible) {
            GuiUtils.drawContinuousTexturedBox(WIDGETS_LOCATION, x, y, 0, 46, BOX_WIDTH, height, 200, 20, 2, 3, 2, 2, 0);

            int color = 14737632;

            if (packedFGColor != 0) {
                color = packedFGColor;
            } else if (!active) {
                color = 10526880;
            }

            if (isChecked()) {
                drawCenteredString(Minecraft.getInstance().fontRenderer, "x", x + BOX_WIDTH / 2 + 1, y + 1, 14737632);
            }

            drawString(Minecraft.getInstance().fontRenderer, displayString, x + BOX_WIDTH + 2, y + 2, color);
        }
    }

    @Override
    public void onPress() {
        super.onPress();

        onPress.onPress(this);
    }
}
