package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.tile.config.RedstoneMode;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RelayNetworkNode extends NetworkNode {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "relay");

    public RelayNetworkNode(World world, BlockPos pos) {
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
        return RS.SERVER_CONFIG.getRelay().getUsage();
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public boolean canConduct(@Nullable Direction direction) {
        return canUpdate();
    }

    @Override
    public boolean shouldRebuildGraphOnChange() {
        return true;
    }
}
