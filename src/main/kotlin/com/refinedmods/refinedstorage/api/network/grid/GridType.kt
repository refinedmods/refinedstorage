package com.refinedmods.refinedstorage.api.network.grid

import net.minecraft.util.StringIdentifiable


/**
 * Represents a grid type.
 * Used in [IGrid] to determine grid GUI rendering.
 */
enum class GridType(private val id: Int, val string: String) : StringIdentifiable {
    /**
     * A regular grid.
     */
    NORMAL(0, "normal"),

    /**
     * A crafting grid.
     */
    CRAFTING(1, "crafting"),

    /**
     * A pattern grid.
     */
    PATTERN(2, "pattern"),

    /**
     * A fluid grid.
     */
    FLUID(3, "fluid");

    override fun asString(): String {
        return string
    }
}