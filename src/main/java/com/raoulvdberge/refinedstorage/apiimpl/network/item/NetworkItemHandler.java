package com.raoulvdberge.refinedstorage.apiimpl.network.item;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.IWirelessTransmitter;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItem;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemHandler;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemProvider;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkItemHandler implements INetworkItemHandler {
    private INetwork network;

    private Map<PlayerEntity, INetworkItem> items = new ConcurrentHashMap<>();

    public NetworkItemHandler(INetwork network) {
        this.network = network;
    }

    @Override
    public void open(PlayerEntity player, ItemStack stack) {
        boolean inRange = false;

        for (INetworkNode node : network.getNodeGraph().all()) {
            if (node instanceof IWirelessTransmitter && node.canUpdate() && ((IWirelessTransmitter) node).getDimension() == player.dimension.getId()) { // TODO does that work?
                IWirelessTransmitter transmitter = (IWirelessTransmitter) node;

                double distance = Math.sqrt(Math.pow(transmitter.getOrigin().getX() - player.posX, 2) + Math.pow(transmitter.getOrigin().getY() - player.posY, 2) + Math.pow(transmitter.getOrigin().getZ() - player.posZ, 2));

                if (distance < transmitter.getRange()) {
                    inRange = true;

                    break;
                }
            }
        }

        if (!inRange) {
            player.sendMessage(new TranslationTextComponent("misc.refinedstorage:network_item.out_of_range"));

            return;
        }

        INetworkItem item = ((INetworkItemProvider) stack.getItem()).provide(this, player, stack);

        if (item.onOpen(network)) {
            items.put(player, item);
        }
    }

    @Override
    public void close(PlayerEntity player) {
        items.remove(player);
    }

    @Override
    public INetworkItem getItem(PlayerEntity player) {
        return items.get(player);
    }

    @Override
    public void drainEnergy(PlayerEntity player, int energy) {
        INetworkItem item = getItem(player);

        if (item != null) {
            item.drainEnergy(energy);
        }
    }
}
