package com.raoulvdberge.refinedstorage.api.network;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collection;

public interface INetworkNodeProvider {
    @Nullable
    INetworkNode getNode(BlockPos pos);

    void removeNode(BlockPos pos);

    void setNode(BlockPos pos, INetworkNode node);

    Collection<INetworkNode> all();

    void clear();
}
