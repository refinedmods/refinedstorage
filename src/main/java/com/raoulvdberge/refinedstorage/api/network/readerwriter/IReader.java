package com.raoulvdberge.refinedstorage.api.network.readerwriter;

import com.raoulvdberge.refinedstorage.api.network.INetworkNode;

public interface IReader extends INetworkNode {
    int getRedstoneStrength();
}
