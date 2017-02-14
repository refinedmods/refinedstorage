package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.tile.config.RedstoneMode;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

public class NetworkNodeRelay extends NetworkNode {
    public static final String ID = "relay";

    public NetworkNodeRelay(INetworkNodeHolder holder) {
        super(holder);

        this.redstoneMode = RedstoneMode.LOW;
    }

    @Override
    public int getEnergyUsage() {
        return getRedstoneMode() == RedstoneMode.IGNORE ? 0 : RS.INSTANCE.config.relayUsage;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean canConduct(@Nullable EnumFacing direction) {
        return canUpdate();
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public boolean shouldRebuildGraphOnChange() {
        return true;
    }
}
