package com.refinedmods.refinedstorage.apiimpl.storage

enum class FluidStorageType(
        val displayName: String,
        val capacity: Int
) {
    SIXTY_FOUR_K("64k", 64000),
    TWO_HUNDRED_FIFTY_SIX_K("256k", 256000),
    THOUSAND_TWENTY_FOUR_K("1024k", 1024000),
    FOUR_THOUSAND_NINETY_SIX_K("4096k", 4096000),
    CREATIVE("creative", -1);

    override fun toString(): String {
        return displayName
    }
}