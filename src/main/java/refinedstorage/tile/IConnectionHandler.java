package refinedstorage.tile;

import refinedstorage.api.network.INetworkMaster;

public interface IConnectionHandler {
    void onConnected(INetworkMaster network);

    void onDisconnected(INetworkMaster network);
}
