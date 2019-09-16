package com.raoulvdberge.refinedstorage.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class CheckBoxWidget extends GuiCheckBox {
    private IPressable onPress;

    public CheckBoxWidget(int xPos, int yPos, String displayString, boolean isChecked, IPressable onPress) {
        super(xPos, yPos, displayString, isChecked);

        this.onPress = onPress;
        this.width = Minecraft.getInstance().fontRenderer.getStringWidth(displayString) + 2 + 11 + 20;
    }

    @Override
    public void onPress() {
        super.onPress();

        onPress.onPress(this);
    }
}
