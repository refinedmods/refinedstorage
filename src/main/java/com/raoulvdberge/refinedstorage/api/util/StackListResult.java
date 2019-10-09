package com.raoulvdberge.refinedstorage.api.util;

import java.util.UUID;

public class StackListResult<T> {
    private T stack;
    private UUID id;
    private int change;

    public StackListResult(T stack, UUID id, int change) {
        this.stack = stack;
        this.id = id;
        this.change = change;
    }

    public T getStack() {
        return stack;
    }

    public UUID getId() {
        return id;
    }

    public int getChange() {
        return change;
    }
}
