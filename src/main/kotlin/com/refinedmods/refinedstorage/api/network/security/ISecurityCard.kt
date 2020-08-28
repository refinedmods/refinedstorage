package com.refinedmods.refinedstorage.api.network.security

import java.util.*


/**
 * Represents a security card.
 */
interface ISecurityCard {
    /**
     * @return the owner of this card, or null if this is a global card
     */
    val owner: UUID?

    /**
     * @param permission the permission to check for
     * @return true if the bound player has the given permission, false otherwise
     */
    fun hasPermission(permission: Permission?): Boolean
}