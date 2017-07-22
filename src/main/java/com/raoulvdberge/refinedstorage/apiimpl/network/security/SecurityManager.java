package com.raoulvdberge.refinedstorage.apiimpl.network.security;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.management.UserListOps;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.security.ISecurityCard;
import com.raoulvdberge.refinedstorage.api.network.security.ISecurityCardContainer;
import com.raoulvdberge.refinedstorage.api.network.security.ISecurityManager;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;

public class SecurityManager implements ISecurityManager {
    private INetwork network;
    private Map<UUID, ISecurityCard> cards = new HashMap<>();

    public SecurityManager(INetwork network) {
        this.network = network;
    }

    @Override
    public boolean hasPermission(Permission permission, EntityPlayer player) {
        UserListOps ops = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getOppedPlayers();

        if (cards.isEmpty() || ops.getEntry(player.getGameProfile()) != null) {
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
