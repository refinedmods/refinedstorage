package com.raoulvdberge.refinedstorage.api.network.security;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a network node that contains security cards.
 */
public interface ISecurityCardContainer {
    /**
     * @return the security cards in this container, {@link ISecurityCard#getOwner()} CANNOT be null for any card in this list!
     */
    List<ISecurityCard> getCards();

    /**
     * @return the global security card in this container, or null if there is none, {@link ISecurityCard#getOwner()}) can be null!
     */
    @Nullable
    ISecurityCard getGlobalCard();
}
