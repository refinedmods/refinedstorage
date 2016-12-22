package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;

public class NetworkNodeCable extends NetworkNode {
    public static final String ID = "cable";

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.cableUsage;
    }

    @Override
    public String getId() {
        return ID;
    }
}
