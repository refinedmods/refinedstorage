package com.refinedmods.refinedstorage.render.resourcepack

import com.google.gson.JsonObject
import net.minecraft.resources.data.IMetadataSectionSerializer

class RSResourcePackSectionDeserializer : IMetadataSectionSerializer<RSResourcePackSection?> {
    val sectionName: String
        get() = "refinedstorage"

    fun deserialize(json: JsonObject): RSResourcePackSection {
        var primaryColor = -1
        var secondaryColor = -1
        if (json.has("primary_color")) {
            primaryColor = json.getAsJsonPrimitive("primary_color").asInt
        }
        if (json.has("secondary_color")) {
            secondaryColor = json.getAsJsonPrimitive("secondary_color").asInt
        }
        return RSResourcePackSection(primaryColor, secondaryColor)
    }
}