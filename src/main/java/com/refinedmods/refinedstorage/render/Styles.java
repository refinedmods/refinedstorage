package com.refinedmods.refinedstorage.render;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public final class Styles {
    public static final Style WHITE = Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.WHITE));
    public static final Style GRAY = Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.GRAY));
    public static final Style YELLOW = Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.YELLOW));
    public static final Style RED = Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.RED));
    public static final Style BLUE = Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.BLUE));
    public static final Style AQUA = Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.AQUA));

    private Styles() {
    }
}
