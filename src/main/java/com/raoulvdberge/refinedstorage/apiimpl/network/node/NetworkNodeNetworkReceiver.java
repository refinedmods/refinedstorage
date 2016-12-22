package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.tile.INetworkNodeHolder;

public class NetworkNodeNetworkReceiver extends NetworkNode {
    public static final String ID = "network_receiver";

    public NetworkNodeNetworkReceiver(INetworkNodeHolder holder) {
        super(holder);
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
