package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.tile.config.RedstoneMode;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class NetworkNodeRelay extends NetworkNode {
    public static final String ID = "relay";

    public NetworkNodeRelay(World world, BlockPos pos) {
        super(world, pos);

        this.redstoneMode = RedstoneMode.LOW;
    }

    @Override
    protected int getUpdateThrottleInactiveToActive() {
        return 0;
    }

    @Override
    protected int getUpdateThrottleActiveToInactive() {
        return 0;
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
