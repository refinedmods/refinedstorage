package com.raoulvdberge.refinedstorage.apiimpl.network.grid.wireless;

import com.raoulvdberge.refinedstorage.api.network.grid.wireless.IWirelessGridFactory;
import com.raoulvdberge.refinedstorage.api.network.grid.wireless.IWirelessGridRegistry;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class WirelessGridRegistry implements IWirelessGridRegistry {
    private int lastId = 0;
    private Map<Integer, IWirelessGridFactory> factories = new HashMap<>();

    @Override
    public int add(IWirelessGridFactory factory) {
        factories.put(lastId, factory);

        return lastId++;
    }

    @Nullable
    @Override
    public IWirelessGridFactory get(int id) {
        return factories.get(id);
    }
}
