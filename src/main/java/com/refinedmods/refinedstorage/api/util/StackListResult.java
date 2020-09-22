package com.refinedmods.refinedstorage.api.util;

import java.util.UUID;

/**
 * Contains the result of a stack list manipulation.
 *
 * @param <T> the stack type
 */
public class StackListResult<T> {
    private final T stack;
    private final UUID id;
    private final int change;

    public StackListResult(T stack, UUID id, int change) {
        this.stack = stack;
        this.id = id;
        this.change = change;
    }

    /**
     * @return the stack
     */
    public T getStack() {
        return stack;
    }

    /**
     * @return the id of the {@link StackListEntry}
     */
    public UUID getId() {
        return id;
    }

    /**
     * @return the change/delta value, is positive if this was a stack addition, or negative if it's a stack removal
     */
    public int getChange() {
        return change;
    }
}
