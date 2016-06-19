package refinedstorage.api.storagenet;

import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class StorageNetworkRegistry {
    // @todo: handle multiple dims
    public static final Map<BlockPos, StorageNetwork> NETWORKS = new HashMap<BlockPos, StorageNetwork>();

    public static void addStorageNetwork(StorageNetwork network) {
        NETWORKS.put(network.getPos(), network);
    }
}
