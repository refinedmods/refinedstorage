package refinedstorage.api.network;

import net.minecraft.util.math.BlockPos;

import java.util.List;

/**
 * A graph of all the nodes connected to a network.
 */
public interface INetworkNodeGraph {
    /**
     * Rebuilds the node graph.
     *
     * @param start  The starting position to start looking for nodes
     * @param notify Whether to notify nodes of a connection change
     */
    void rebuild(BlockPos start, boolean notify);

    /**
     * @return A list of all connected nodes
     */
    List<INetworkNode> all();

    /**
     * Disconnects and notifies all connected nodes.
     */
    void disconnectAll();
}
