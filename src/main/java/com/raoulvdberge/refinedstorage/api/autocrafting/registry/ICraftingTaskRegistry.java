package com.raoulvdberge.refinedstorage.api.autocrafting.registry;

import javax.annotation.Nullable;

/**
 * A registry that stores crafting task factories.
 * Implement this to handle the loading of custom crafting tasks.
 */
public interface ICraftingTaskRegistry {
    /**
     * Adds a crafting task factory to the registry.
     * The id is used for identifying tasks when they are read from disk.
     *
     * @param id      the id of the factory
     * @param factory the factory
     */
    void add(String id, ICraftingTaskFactory factory);

    /**
     * Returns the crafting task factory by factory id.
     *
     * @param id the factory id
     * @return the factory, or null if there is no factory
     */
    @Nullable
    ICraftingTaskFactory get(String id);
}
