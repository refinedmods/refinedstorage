package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeCable;
import com.raoulvdberge.refinedstorage.integration.mcmp.PartCableTile;
import mcmultipart.api.ref.MCMPCapabilities;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileCable extends TileNode<NetworkNodeCable> {
    private PartCableTile part = new PartCableTile(this);

    @Override
    @Nonnull
    public NetworkNodeCable createNode() {
        return new NetworkNodeCable(this);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side) {
        return super.hasCapability(capability, side) || capability == MCMPCapabilities.MULTIPART_TILE;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side) {
        if (capability == MCMPCapabilities.MULTIPART_TILE) {
            return (T) part;
        }

        return super.getCapability(capability, side);
    }
}
