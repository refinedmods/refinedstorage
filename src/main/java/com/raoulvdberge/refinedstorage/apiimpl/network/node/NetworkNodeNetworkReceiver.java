package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.tile.INetworkNodeContainer;

public class NetworkNodeNetworkReceiver extends NetworkNode {
    public static final String ID = "network_receiver";

    public NetworkNodeNetworkReceiver(INetworkNodeContainer container) {
        super(container);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.networkReceiverUsage;
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public String getId() {
        return ID;
    }
}
