package com.refinedmods.refinedstorage.api.network.security;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Represents a security card.
 */
public interface ISecurityCard {
    /**
     * @return the owner of this card, or null if this is a global card
     */
    @Nullable
    UUID getOwner();

    /**
     * @param permission the permission to check for
     * @return true if the bound player has the given permission, false otherwise
     */
    boolean hasPermission(Permission permission);
}
