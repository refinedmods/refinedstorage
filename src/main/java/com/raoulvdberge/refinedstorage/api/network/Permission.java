package com.raoulvdberge.refinedstorage.api.network;

/**
 * The various permissions a player can have in a network.
 */
public enum Permission {
    /**
     * Whether the player can insert items in a network.
     */
    INSERT,
    /**
     * Whether the player can extract items from a network.
     */
    EXTRACT,
    /**
     * Whether the player can start, cancel or view an autocrafting task.
     */
    AUTOCRAFT,
    /**
     * Whether the player can open network GUIs and can place or break network blocks.
     */
    MODIFY,
    /**
     * Whether the player can manage the security options for a network.
     */
    SECURITY
}
