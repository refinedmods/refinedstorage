package com.raoulvdberge.refinedstorage.apiimpl.network.item;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.IWirelessTransmitter;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItem;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemHandler;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemProvider;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkItemHandler implements INetworkItemHandler {
    private INetwork network;

    private Map<EntityPlayer, INetworkItem> items = new ConcurrentHashMap<>();

    public NetworkItemHandler(INetwork network) {
        this.network = network;
    }

    @Override
    public void onOpen(EntityPlayer player, EnumHand hand) {
        boolean inRange = false;

        for (INetworkNode node : network.getNodeGraph().all()) {
            if (node instanceof IWirelessTransmitter && node.canUpdate() && ((IWirelessTransmitter) node).getDimension() == player.dimension) {
                IWirelessTransmitter transmitter = (IWirelessTransmitter) node;

                double distance = Math.sqrt(Math.pow(transmitter.getOrigin().getX() - player.posX, 2) + Math.pow(transmitter.getOrigin().getY() - player.posY, 2) + Math.pow(transmitter.getOrigin().getZ() - player.posZ, 2));

                if (distance < transmitter.getRange()) {
                    inRange = true;

                    break;
                }
            }
        }

        if (!inRange) {
            player.sendMessage(new TextComponentTranslation("misc.refinedstorage:network_item.out_of_range"));

            return;
        }

        INetworkItem item = ((INetworkItemProvider) player.getHeldItem(hand).getItem()).provide(this, player, player.getHeldItem(hand));

        if (item.onOpen(network, player, hand)) {
            items.put(player, item);
        }
    }

    @Override
    public void onClose(EntityPlayer player) {
        items.remove(player);
    }

    @Override
    public INetworkItem getItem(EntityPlayer player) {
        return items.get(player);
    }
}
