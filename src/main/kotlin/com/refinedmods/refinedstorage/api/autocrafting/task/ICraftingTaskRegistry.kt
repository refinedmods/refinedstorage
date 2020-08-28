package com.refinedmods.refinedstorage.api.autocrafting.task

import net.minecraft.util.Identifier


/**
 * A registry that stores crafting task factories.
 * Implement this to handle the loading of custom crafting tasks.
 */
interface ICraftingTaskRegistry {
    /**
     * Adds a crafting task factory to the registry.
     * The id is used for identifying tasks when they are read from disk.
     *
     * @param id      the id of the factory
     * @param factory the factory
     */
    fun add(id: Identifier, factory: ICraftingTaskFactory)

    /**
     * Returns the crafting task factory by factory id.
     *
     * @param id the factory id
     * @return the factory, or null if there is no factory
     */
    operator fun get(id: Identifier): ICraftingTaskFactory?
}