package refinedstorage.api.network;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Set;

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
     * @return A set of hashes of all connected nodes, see {@link NetworkUtils#getNodeHashCode(World, INetworkNode)}
     */
    Set<Integer> allHashes();

    /**
     * Disconnects and notifies all connected nodes.
     */
    void disconnectAll();
}
