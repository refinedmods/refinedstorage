package com.refinedmods.refinedstorage.render;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public final class Styles {
    public static final Style WHITE = Style.EMPTY.withColor(Color.fromLegacyFormat(TextFormatting.WHITE));
    public static final Style GRAY = Style.EMPTY.withColor(Color.fromLegacyFormat(TextFormatting.GRAY));
    public static final Style YELLOW = Style.EMPTY.withColor(Color.fromLegacyFormat(TextFormatting.YELLOW));
    public static final Style RED = Style.EMPTY.withColor(Color.fromLegacyFormat(TextFormatting.RED));
    public static final Style BLUE = Style.EMPTY.withColor(Color.fromLegacyFormat(TextFormatting.BLUE));
    public static final Style AQUA = Style.EMPTY.withColor(Color.fromLegacyFormat(TextFormatting.AQUA));

    private Styles() {
    }
}
