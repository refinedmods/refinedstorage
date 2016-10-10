package refinedstorage.api.storage;

/**
 * The access type of a storage.
 */
public enum AccessType {
    /**
     * Read and write access.
     */
    READ_WRITE(0),
    /**
     * Only read access.
     */
    READ(1),
    /**
     * Only write access.
     */
    WRITE(2);

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
