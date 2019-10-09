package com.raoulvdberge.refinedstorage.api.util;

import java.util.UUID;

/**
 * Represents a stack in a stack list.
 *
 * @param <T> the stack type
 */
public class StackListEntry<T> {
    private UUID id;
    private T stack;

    public StackListEntry(T stack) {
        this.stack = stack;
        this.id = UUID.randomUUID();
    }

    public StackListEntry(UUID id, T stack) {
        this.id = id;
        this.stack = stack;
    }

    /**
     * The unique id of the entry.
     * This id is NOT persisted, nor does it hold any relation to the contained stack.
     * It is randomly generated.
     *
     * @return the id
     */
    public UUID getId() {
        return id;
    }

    /**
     * @return the stack
     */
    public T getStack() {
        return stack;
    }
}
