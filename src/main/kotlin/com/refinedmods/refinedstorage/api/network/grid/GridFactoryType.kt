package com.refinedmods.refinedstorage.api.network.grid



/**
 * The type of grid factory.
 */
enum class GridFactoryType {
    /**
     * A grid factory for item stacks.
     */
    STACK,

    /**
     * A grid factory for blocks with a position.
     */
    BLOCK
}