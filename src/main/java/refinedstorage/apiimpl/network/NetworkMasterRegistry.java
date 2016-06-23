package refinedstorage.apiimpl.network;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import refinedstorage.api.network.INetworkMaster;

import java.util.HashMap;
import java.util.Map;

public class NetworkMasterRegistry {
    public static final Map<Integer, Map<BlockPos, INetworkMaster>> NETWORKS = new HashMap<Integer, Map<BlockPos, INetworkMaster>>();

    public static void add(World world, INetworkMaster network) {
        add(world.provider.getDimension(), network);

        NetworkMasterSavedData.getOrLoad(world).markDirty();
    }

    public static void add(int dim, INetworkMaster network) {
        if (NETWORKS.get(dim) == null) {
            NETWORKS.put(dim, new HashMap<BlockPos, INetworkMaster>());
        }

        NETWORKS.get(dim).put(network.getPosition(), network);
    }

    public static void remove(World world, BlockPos pos) {
        if (get(world) != null) {
            INetworkMaster network = get(world).get(pos);

            if (network != null) {
                get(world).remove(pos);

                NetworkMasterSavedData.getOrLoad(world).markDirty();
            }
        }
    }

    public static INetworkMaster get(World world, BlockPos pos) {
        return get(world) == null ? null : get(world).get(pos);
    }

    public static Map<BlockPos, INetworkMaster> get(World world) {
        return NETWORKS.get(world.provider.getDimension());
    }

    public static void clear() {
        NETWORKS.clear();
    }
}
