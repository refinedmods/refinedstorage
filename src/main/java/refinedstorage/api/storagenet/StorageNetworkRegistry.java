package refinedstorage.api.storagenet;

import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class StorageNetworkRegistry {
    public static final Map<Integer, Map<BlockPos, StorageNetwork>> NETWORKS = new HashMap<Integer, Map<BlockPos, StorageNetwork>>();

    public static void add(StorageNetwork network, int dimension) {
        if (NETWORKS.get(dimension) == null) {
            NETWORKS.put(dimension, new HashMap<BlockPos, StorageNetwork>());
        }

        NETWORKS.get(dimension).put(network.getPos(), network);
    }

    public static void remove(BlockPos pos, int dimension) {
        if (get(dimension) != null) {
            get(dimension).get(pos).onRemoved();
            get(dimension).remove(pos);
        }
    }

    public static StorageNetwork get(BlockPos pos, int dimension) {
        return get(dimension) == null ? null : get(dimension).get(pos);
    }

    public static Map<BlockPos, StorageNetwork> get(int dimension) {
        return NETWORKS.get(dimension);
    }
}
