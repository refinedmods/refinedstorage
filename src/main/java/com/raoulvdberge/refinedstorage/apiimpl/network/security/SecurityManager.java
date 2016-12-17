package com.raoulvdberge.refinedstorage.apiimpl.network.security;

import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.security.ISecurityCard;
import com.raoulvdberge.refinedstorage.api.network.security.ISecurityCardContainer;
import com.raoulvdberge.refinedstorage.api.network.security.ISecurityManager;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SecurityManager implements ISecurityManager {
    private INetworkMaster network;
    private Map<UUID, ISecurityCard> cards = new HashMap<>();

    public SecurityManager(INetworkMaster network) {
        this.network = network;
    }

    @Override
    public boolean hasPermission(Permission permission, EntityPlayer player) {
        if (cards.isEmpty()) {
            return true;
        }

        UUID uuid = player.getGameProfile().getId();

        if (!cards.containsKey(uuid)) {
            return false;
        }

        return cards.get(uuid).hasPermission(permission);
    }

    @Override
    public void rebuild() {
        cards.clear();

        for (INetworkNode node : network.getNodeGraph().all()) {
            if (node instanceof ISecurityCardContainer && node.canUpdate()) {
                for (ISecurityCard card : ((ISecurityCardContainer) node).getCards()) {
                    cards.put(card.getOwner(), card);
                }
            }
        }
    }
}
