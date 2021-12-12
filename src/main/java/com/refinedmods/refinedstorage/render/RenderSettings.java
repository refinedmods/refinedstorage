package com.refinedmods.refinedstorage.render;

import net.minecraft.ChatFormatting;

public class RenderSettings {
    public static final RenderSettings INSTANCE = new RenderSettings();

    private int primaryColor;
    private int secondaryColor;

    public RenderSettings() {
        setColors(-1, -1);
    }

    public void setColors(int primaryColor, int secondaryColor) {
        if (primaryColor == -1) {
            this.primaryColor = 4210752;
        } else {
            this.primaryColor = primaryColor;
        }

        if (secondaryColor == -1) {
            this.secondaryColor = ChatFormatting.WHITE.getColor();
        } else {
            this.secondaryColor = secondaryColor;
        }
    }

    public int getPrimaryColor() {
        return primaryColor;
    }

    public int getSecondaryColor() {
        return secondaryColor;
    }
}
