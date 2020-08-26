package com.refinedmods.refinedstorage.api.autocrafting.task


/**
 * Gets thrown from [ICraftingTaskFactory.createFromNbt].
 */
class CraftingTaskReadException
/**
 * @param message the message
 */
(message: String?) : Exception(message)