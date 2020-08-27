package com.refinedmods.refinedstorage.api.util

import java.util.*


/**
 * Represents a stack in a stack list.
 *
 * @param <T> the stack type
</T> */
class StackListEntry<T> {
    /**
     * The unique id of the entry.
     * This id is NOT persisted, nor does it hold any relation to the contained stack.
     * It is randomly generated.
     *
     * @return the id
     */
    val id: UUID

    /**
     * @return the stack
     */
    val stack: T

    constructor(stack: T) {
        this.stack = stack
        id = UUID.randomUUID()
    }

    constructor(id: UUID, stack: T) {
        this.id = id
        this.stack = stack
    }
}