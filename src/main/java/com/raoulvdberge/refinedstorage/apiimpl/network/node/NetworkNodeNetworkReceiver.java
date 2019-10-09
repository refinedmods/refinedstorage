package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetworkNodeNetworkReceiver extends NetworkNode {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "network_receiver");

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
    public ResourceLocation getId() {
        return ID;
    }
}
