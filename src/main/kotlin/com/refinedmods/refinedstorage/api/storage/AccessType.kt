package com.refinedmods.refinedstorage.api.storage



/**
 * The access type of a storage.
 */
enum class AccessType
/**
 * @param id the id of this access type
 */(private val id: Int) {
    /**
     * Insert and extract ability.
     */
    INSERT_EXTRACT(0),

    /**
     * Only insert ability.
     */
    INSERT(1),

    /**
     * Only extract ability.
     */
    EXTRACT(2);

    /**
     * @return the id of this access type
     */
    fun getId(): Int {
        return id
    }
}