package com.refinedmods.refinedstorage.api.util

import java.util.*


/**
 * Contains the result of a stack list manipulation.
 *
 * @param <T> the stack type
</T> */
class StackListResult<T>(
        /**
         * @return the stack
         */
        val stack: T,
        /**
         * @return the id of the [StackListEntry]
         */
        val id: UUID,
        /**
         * @return the change/delta value, is positive if this was a stack addition, or negative if it's a stack removal
         */
        val change: Int)