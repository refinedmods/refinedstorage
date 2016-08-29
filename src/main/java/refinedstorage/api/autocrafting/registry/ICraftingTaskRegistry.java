package refinedstorage.api.autocrafting.registry;

/**
 * A registry that stores the various crafting task types.
 */
public interface ICraftingTaskRegistry {
    /**
     * Adds a crafting task factory to the registry.
     * The id is used for reading and writing the crafting tasks to disk.
     *
     * @param id      The id of the crafting task type
     * @param factory The factory
     */
    void addFactory(String id, ICraftingTaskFactory factory);

    /**
     * Returns the factory of a crafting task type.
     * This is used when reading the storage network from disk to get a factory in order to create a crafting task.
     * It is also used for creating crafting tasks on demand when the player requests it.
     *
     * @param id The id
     * @return The factory
     */
    ICraftingTaskFactory getFactory(String id);
}
