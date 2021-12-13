package com.refinedmods.refinedstorage.apiimpl.network.node;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.blockentity.config.RedstoneMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class RelayNetworkNode extends NetworkNode {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "relay");

    public RelayNetworkNode(Level level, BlockPos pos) {
        super(level, pos);
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
    public boolean canReceive(Direction direction) {
        return true;
    }

    @Override
    public boolean shouldRebuildGraphOnChange() {
        return true;
    }
}
