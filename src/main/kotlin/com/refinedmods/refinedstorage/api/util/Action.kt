package com.refinedmods.refinedstorage.api.util



/**
 * Defines how an action is performed.
 */
enum class Action {
    /**
     * Performs the action.
     */
    PERFORM,

    /**
     * Gives back the same return as called with PERFORM, but doesn't mutate the underlying structure.
     */
    SIMULATE
}