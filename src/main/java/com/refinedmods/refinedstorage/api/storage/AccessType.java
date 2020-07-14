package com.refinedmods.refinedstorage.api.storage;

/**
 * The access type of a storage.
 */
public enum AccessType {
    /**
     * Insert and extract ability.
     */
    INSERT_EXTRACT(0),
    /**
     * Only insert ability.
     */
    INSERT(1),
    /**
     * Only extract ability.
     */
    EXTRACT(2);

    private final int id;

    /**
     * @param id the id of this access type
     */
    AccessType(int id) {
        this.id = id;
    }

    /**
     * @return the id of this access type
     */
    public int getId() {
        return id;
    }
}
