package com.raoulvdberge.refinedstorage.api.network.grid.wireless;

import javax.annotation.Nullable;

public interface IWirelessGridRegistry {
    int add(IWirelessGridFactory factory);

    @Nullable
    IWirelessGridFactory get(int id);
}
