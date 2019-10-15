package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CableNetworkNode extends NetworkNode {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "cable");

    public CableNetworkNode(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.SERVER_CONFIG.getCable().getUsage();
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}
