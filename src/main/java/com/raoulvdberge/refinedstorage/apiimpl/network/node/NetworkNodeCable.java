package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.block.BlockCable;
import com.raoulvdberge.refinedstorage.integration.mcmp.IntegrationMCMP;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.Collections;

public class NetworkNodeCable extends NetworkNode {
    public static final String ID = "cable";

    public NetworkNodeCable(INetworkNodeHolder holder) {
        super(holder);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.cableUsage;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean canConduct(@Nullable EnumFacing direction) {
        return !IntegrationMCMP.isLoaded() || IntegrationMCMP.hasConnectionWith(holder.world(), holder.pos(), Collections.singletonList(BlockCable.directionToAABB(direction)));
    }
}
