package com.raoulvdberge.refinedstorage.api.network.security;

import java.util.List;

/**
 * A tile that contains security cards.
 */
public interface ISecurityCardContainer {
    /**
     * @return the security cards in this container
     */
    List<ISecurityCard> getCards();
}
