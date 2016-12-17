package com.raoulvdberge.refinedstorage.api.network.security;

import java.util.UUID;

/**
 * Represents a security card.
 */
public interface ISecurityCard {
    /**
     * @return the owner of this card
     */
    UUID getOwner();

    /**
     * @param permission the permission to check for
     * @return whether the bound player has the given permission
     */
    boolean hasPermission(Permission permission);
}
