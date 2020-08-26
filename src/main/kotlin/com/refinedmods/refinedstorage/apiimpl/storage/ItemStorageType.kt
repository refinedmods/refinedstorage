package com.refinedmods.refinedstorage.apiimpl.storage

enum class ItemStorageType(val displayName: String, val capacity: Int) {
    ONE_K("1k", 1000),
    FOUR_K("4k", 4000),
    SIXTEEN_K("16k", 16000),
    SIXTY_FOUR_K("64k", 64000),
    CREATIVE("creative", -1);

    override fun toString(): String {
        return displayName
    }
}