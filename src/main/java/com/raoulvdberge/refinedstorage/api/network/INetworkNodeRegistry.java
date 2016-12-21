package com.raoulvdberge.refinedstorage.api.network;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public interface INetworkNodeRegistry {
    @Nullable
    INetworkNode getNode(BlockPos pos);

    void removeNode(BlockPos pos);

    void setNode(BlockPos pos, INetworkNode node);
}
