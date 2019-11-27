package com.raoulvdberge.refinedstorage.render.resourcepack;

public class RSResourcePackSection {
    public static final RSResourcePackSectionDeserializer DESERIALIZER = new RSResourcePackSectionDeserializer();

    private final int primaryColor;
    private final int secondaryColor;

    public RSResourcePackSection(int primaryColor, int secondaryColor) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
    }

    public int getPrimaryColor() {
        return primaryColor;
    }

    public int getSecondaryColor() {
        return secondaryColor;
    }
}
