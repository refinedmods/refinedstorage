package com.raoulvdberge.refinedstorage.api.network.security;

import java.util.List;

/**
 * Represents a network node that contains security cards.
 */
public interface ISecurityCardContainer {
    /**
     * @return the security cards in this container
     */
    List<ISecurityCard> getCards();
}
