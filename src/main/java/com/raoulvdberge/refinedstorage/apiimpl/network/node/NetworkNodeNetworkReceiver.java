package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetworkNodeNetworkReceiver extends NetworkNode {
    public static final String ID = "network_receiver";

    public NetworkNodeNetworkReceiver(World world, BlockPos pos) {
        super(world, pos);
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
