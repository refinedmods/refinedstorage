package com.raoulvdberge.refinedstorage.apiimpl.network;

import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeProvider;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NetworkNodeProvider implements INetworkNodeProvider {
    private Map<BlockPos, INetworkNode> nodes = new HashMap<>();

    @Override
    @Nullable
    public INetworkNode getNode(BlockPos pos) {
        return nodes.get(pos);
    }

    @Override
    public void removeNode(BlockPos pos) {
        nodes.remove(pos);
    }

    @Override
    public void setNode(BlockPos pos, INetworkNode node) {
        nodes.put(pos, node);
    }

    @Override
    public Collection<INetworkNode> all() {
        return nodes.values();
    }

    @Override
    public void clear() {
        nodes.clear();
    }
}
