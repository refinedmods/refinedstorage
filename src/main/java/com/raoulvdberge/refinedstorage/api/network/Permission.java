package com.raoulvdberge.refinedstorage.api.network;

/**
 * The various permissions a player can have in a network.
 */
public enum Permission {
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
    AUTOCRAFT(2),
    /**
     * Whether the player can open network GUIs and can place or break network blocks.
     */
    MODIFY(3),
    /**
     * Whether the player can manage the security options for a network.
     */
    SECURITY(4);

    private final int id;

    Permission(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
