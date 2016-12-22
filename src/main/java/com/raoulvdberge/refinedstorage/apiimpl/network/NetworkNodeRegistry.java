package com.raoulvdberge.refinedstorage.apiimpl.network;

import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeRegistry;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class NetworkNodeRegistry implements INetworkNodeRegistry {
    private Map<String, Function<NBTTagCompound, INetworkNode>> factories = new HashMap<>();

    @Override
    public void add(String id, Function<NBTTagCompound, INetworkNode> factory) {
        factories.put(id, factory);
    }

    @Override
    @Nullable
    public Function<NBTTagCompound, INetworkNode> get(String id) {
        return factories.get(id);
    }
}
