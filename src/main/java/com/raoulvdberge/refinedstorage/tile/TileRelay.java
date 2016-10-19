package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.tile.config.RedstoneMode;
import net.minecraft.util.EnumFacing;

public class TileRelay extends TileNode {
    public TileRelay() {
        setRedstoneMode(RedstoneMode.LOW);

        rebuildOnUpdateChange = true;
    }

    @Override
    public int getEnergyUsage() {
        return getRedstoneMode() == RedstoneMode.IGNORE ? 0 : RS.INSTANCE.config.relayUsage;
    }

    @Override
    public void updateNode() {
    }

    @Override
    public boolean canConduct(EnumFacing direction) {
        return canUpdate();
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }
}
