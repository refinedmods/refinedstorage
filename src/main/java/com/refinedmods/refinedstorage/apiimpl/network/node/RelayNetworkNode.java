package com.refinedmods.refinedstorage.apiimpl.network.node;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.tile.config.RedstoneMode;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
    public boolean canConduct(Direction direction) {
        return canUpdate();
    }

    @Override
    public boolean shouldRebuildGraphOnChange() {
        return true;
    }
}
