package com.refinedmods.refinedstorage.render.resourcepack;

import com.google.gson.JsonObject;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;

public class RSResourcePackSectionDeserializer implements MetadataSectionSerializer<RSResourcePackSection> {
    @Override
    public String getMetadataSectionName() {
        return "refinedstorage";
    }

    @Override
    public RSResourcePackSection fromJson(JsonObject json) {
        int primaryColor = -1;
        int secondaryColor = -1;

        if (json.has("primary_color")) {
            primaryColor = json.getAsJsonPrimitive("primary_color").getAsInt();
        }

        if (json.has("secondary_color")) {
            secondaryColor = json.getAsJsonPrimitive("secondary_color").getAsInt();
        }

        return new RSResourcePackSection(primaryColor, secondaryColor);
    }
}
