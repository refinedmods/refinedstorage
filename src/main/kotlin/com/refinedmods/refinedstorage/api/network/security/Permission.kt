package com.refinedmods.refinedstorage.api.network.security



/**
 * The various permissions a player can have in a network.
 */
enum class Permission(val id: Int) {
    /**
     * Whether the player can insert items in a network.
     */
    INSERT(0),

    /**
     * Whether the player can extract items from a network.
     */
    EXTRACT(1),

    /**
     * Whether the player can start, cancel or view an autocrafting task.
     */
    AUTOCRAFTING(2),

    /**
     * Whether the player can open network GUIs.
     */
    MODIFY(3),

    /**
     * Whether the player can add or remove network blocks.
     */
    BUILD(4),

    /**
     * Whether the player can manage the security options for a network.
     */
    SECURITY(5);

}