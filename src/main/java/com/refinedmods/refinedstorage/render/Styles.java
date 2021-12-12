package com.refinedmods.refinedstorage.render;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public final class Styles {
    public static final Style WHITE = Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.WHITE));
    public static final Style GRAY = Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.GRAY));
    public static final Style YELLOW = Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.YELLOW));
    public static final Style RED = Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED));
    public static final Style BLUE = Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE));
    public static final Style AQUA = Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA));

    private Styles() {
    }
}
