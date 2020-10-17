package com.refinedmods.refinedstorage.apiimpl.network.item;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.INetworkNodeGraphEntry;
import com.refinedmods.refinedstorage.api.network.IWirelessTransmitter;
import com.refinedmods.refinedstorage.api.network.item.INetworkItem;
import com.refinedmods.refinedstorage.api.network.item.INetworkItemManager;
import com.refinedmods.refinedstorage.api.network.item.INetworkItemProvider;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkItemManager implements INetworkItemManager {
    private final INetwork network;
    private final Map<PlayerEntity, INetworkItem> items = new ConcurrentHashMap<>();

    public NetworkItemManager(INetwork network) {
        this.network = network;
    }

    @Override
    public void open(PlayerEntity player, ItemStack stack, int slotId) {
        boolean inRange = false;

        for (INetworkNodeGraphEntry entry : network.getNodeGraph().all()) {
            INetworkNode node = entry.getNode();

            if (node instanceof IWirelessTransmitter &&
                network.canRun() &&
                node.isActive() &&
                ((IWirelessTransmitter) node).getDimension() == player.getEntityWorld().func_234923_W_()) {
                IWirelessTransmitter transmitter = (IWirelessTransmitter) node;

                Vector3d pos = player.getPositionVec();

                double distance = Math.sqrt(Math.pow(transmitter.getOrigin().getX() - pos.getX(), 2) + Math.pow(transmitter.getOrigin().getY() - pos.getY(), 2) + Math.pow(transmitter.getOrigin().getZ() - pos.getZ(), 2));

                if (distance < transmitter.getRange()) {
                    inRange = true;

                    break;
                }
            }
        }

        if (!inRange) {
            player.sendMessage(new TranslationTextComponent("misc.refinedstorage.network_item.out_of_range"), player.getUniqueID());

            return;
        }

        INetworkItem item = ((INetworkItemProvider) stack.getItem()).provide(this, player, stack, slotId);

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
