package refinedstorage.api.network;

import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface INetworkNodeGraph {
    void rebuild(BlockPos start);

    List<INetworkNode> all();

    void disconnectAll();
}
