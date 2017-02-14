package com.raoulvdberge.refinedstorage.apiimpl.network;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeManager;
import com.raoulvdberge.refinedstorage.network.MessageNodeRemove;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NetworkNodeManager implements INetworkNodeManager {
    private Map<BlockPos, INetworkNode> nodes = new HashMap<>();

    private int dimension;

    public NetworkNodeManager(int dimension) {
        this.dimension = dimension;
    }

    @Override
    @Nullable
    public INetworkNode getNode(BlockPos pos) {
        return nodes.get(pos);
    }

    @Override
    public void removeNode(BlockPos pos, boolean notifyClient) {
        nodes.remove(pos);

        if (notifyClient) {
            RS.INSTANCE.network.sendToAll(new MessageNodeRemove(dimension, pos));
        }
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
