package com.raoulvdberge.refinedstorage.api.storage;

/**
 * The access type of a storage.
 */
public enum AccessType {
    /**
     * Insert and extract ability.
     */
    INSERT_EXTRACT(0),
    /**
     * Only extract ability.
     */
    EXTRACT(1),
    /**
     * Only insert ability.
     */
    INSERT(2);

    private int id;

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
