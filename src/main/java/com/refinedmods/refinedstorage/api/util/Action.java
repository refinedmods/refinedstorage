package com.refinedmods.refinedstorage.api.util;

/**
 * Defines how an action is performed.
 */
public enum Action {
    /**
     * Performs the action.
     */
    PERFORM,
    /**
     * Gives back the same return as called with PERFORM, but doesn't mutate the underlying structure.
     */
    SIMULATE
}
