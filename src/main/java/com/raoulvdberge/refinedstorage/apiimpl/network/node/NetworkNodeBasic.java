package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.tile.INetworkNodeHolder;

public class NetworkNodeBasic extends NetworkNode {
    private int energyUsage;
    private boolean connectivityState;

    public NetworkNodeBasic(INetworkNodeHolder holder, int energyUsage, boolean connectivityState) {
        super(holder);

        this.energyUsage = energyUsage;
        this.connectivityState = connectivityState;
    }

    @Override
    public int getEnergyUsage() {
        return energyUsage;
    }

    @Override
    public boolean hasConnectivityState() {
        return connectivityState;
    }
}
