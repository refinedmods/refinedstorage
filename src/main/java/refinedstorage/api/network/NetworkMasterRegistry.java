package refinedstorage.api.network;

import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class NetworkMasterRegistry {
    public static final Map<Integer, Map<BlockPos, NetworkMaster>> NETWORKS = new HashMap<Integer, Map<BlockPos, NetworkMaster>>();

    public static void add(NetworkMaster network, int dimension) {
        if (NETWORKS.get(dimension) == null) {
            NETWORKS.put(dimension, new HashMap<BlockPos, NetworkMaster>());
        }

        NETWORKS.get(dimension).put(network.getPosition(), network);
    }

    public static void remove(BlockPos pos, int dimension) {
        if (get(dimension) != null) {
            NetworkMaster network = get(dimension).get(pos);

            if (network != null) {
                network.onRemoved();

                get(dimension).remove(pos);
            }
        }
    }

    public static NetworkMaster get(BlockPos pos, int dimension) {
        return get(dimension) == null ? null : get(dimension).get(pos);
    }

    public static Map<BlockPos, NetworkMaster> get(int dimension) {
        return NETWORKS.get(dimension);
    }

    public static void clear() {
        NETWORKS.clear();
    }
}
