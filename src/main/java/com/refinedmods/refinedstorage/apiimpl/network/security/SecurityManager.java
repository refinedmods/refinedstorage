package com.refinedmods.refinedstorage.apiimpl.network.security;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.security.ISecurityCard;
import com.refinedmods.refinedstorage.api.network.security.ISecurityCardContainer;
import com.refinedmods.refinedstorage.api.network.security.ISecurityManager;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.management.OpList;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SecurityManager implements ISecurityManager {
    private final INetwork network;
    private final Map<UUID, ISecurityCard> cards = new HashMap<>();
    private ISecurityCard globalCard;

    public SecurityManager(INetwork network) {
        this.network = network;
    }

    @Override
    public boolean hasPermission(Permission permission, PlayerEntity player) {
        OpList ops = player.getServer().getPlayerList().getOppedPlayers();

        if (ops.getEntry(player.getGameProfile()) != null) {
            return true;
        }

        UUID uuid = player.getGameProfile().getId();

        if (!cards.containsKey(uuid)) {
            if (globalCard != null) {
                return globalCard.hasPermission(permission);
            }

            return true;
        }

        return cards.get(uuid).hasPermission(permission);
    }

    @Override
    public void invalidate() {
        this.cards.clear();
        this.globalCard = null;

        for (INetworkNode node : network.getNodeGraph().all()) {
            if (node instanceof ISecurityCardContainer && node.isActive()) {
                ISecurityCardContainer container = (ISecurityCardContainer) node;

                for (ISecurityCard card : container.getCards()) {
                    if (card.getOwner() == null) {
                        throw new IllegalStateException("Owner in #getCards() cannot be null!");
                    }

                    this.cards.put(card.getOwner(), card);
                }

                if (container.getGlobalCard() != null) {
                    this.globalCard = container.getGlobalCard();
                }
            }
        }
    }
}
