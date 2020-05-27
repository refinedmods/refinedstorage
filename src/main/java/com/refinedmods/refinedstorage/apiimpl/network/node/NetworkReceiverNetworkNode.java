package com.refinedmods.refinedstorage.apiimpl.network.node;

import com.refinedmods.refinedstorage.RS;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetworkReceiverNetworkNode extends NetworkNode {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "network_receiver");

    public NetworkReceiverNetworkNode(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.SERVER_CONFIG.getNetworkReceiver().getUsage();
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}
