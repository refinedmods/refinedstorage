package com.refinedmods.refinedstorage.api.network.security



/**
 * Represents a network node that contains security cards.
 */
interface ISecurityCardContainer {
    /**
     * @return the security cards in this container, [ISecurityCard.getOwner] CANNOT be null for any card in this list!
     */
    val cards: List<ISecurityCard?>?

    /**
     * @return the global security card in this container, or null if there is none, [ISecurityCard.getOwner]) can be null!
     */
    val globalCard: ISecurityCard?
}