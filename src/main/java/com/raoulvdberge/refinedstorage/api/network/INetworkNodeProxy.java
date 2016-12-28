package com.raoulvdberge.refinedstorage.api.network;

import javax.annotation.Nonnull;

public interface INetworkNodeProxy<T extends INetworkNode> {
    @Nonnull
    T getNode();
}
