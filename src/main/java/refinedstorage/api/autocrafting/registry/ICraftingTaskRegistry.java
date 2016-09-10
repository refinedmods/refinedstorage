package refinedstorage.api.autocrafting.registry;

import javax.annotation.Nullable;

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
     * Returns the factory of a crafting task type id.
     *
     * @param id The id
     * @return The factory
     */
    @Nullable
    ICraftingTaskFactory getFactory(String id);
}
