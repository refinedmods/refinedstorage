package com.raoulvdberge.refinedstorage.apiimpl.network;

import javax.annotation.Nullable;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeFactory;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeRegistry;

import java.util.HashMap;
import java.util.Map;

public class NetworkNodeRegistry implements INetworkNodeRegistry {
    private Map<String, INetworkNodeFactory> factories = new HashMap<>();

    @Override
    public void add(String id, INetworkNodeFactory factory) {
        factories.put(id, factory);
    }

    @Override
    @Nullable
    public INetworkNodeFactory get(String id) {
        return factories.get(id);
    }
}
