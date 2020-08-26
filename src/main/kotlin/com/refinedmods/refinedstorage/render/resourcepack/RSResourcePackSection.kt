package com.refinedmods.refinedstorage.render.resourcepack

class RSResourcePackSection(val primaryColor: Int, val secondaryColor: Int) {

    companion object {
        val DESERIALIZER = RSResourcePackSectionDeserializer()
    }
}