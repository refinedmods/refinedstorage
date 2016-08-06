package refinedstorage.apiimpl.network;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import refinedstorage.api.network.INetworkNode;
import refinedstorage.api.network.INetworkNodeGraph;
import refinedstorage.tile.TileController;
import refinedstorage.tile.TileNetworkTransmitter;

import java.util.*;

public class NetworkNodeGraph implements INetworkNodeGraph {
    private TileController controller;

    private List<INetworkNode> nodes = new ArrayList<>();
    private Set<Integer> nodeHashes = new HashSet<>();

    public NetworkNodeGraph(TileController controller) {
        this.controller = controller;
    }

    private int hashNode(World world, INetworkNode node) {
        int result = node.getPosition().hashCode();
        result = 31 * result + world.provider.getDimension();
        return result;
    }

    @Override
    public void rebuild(BlockPos start, boolean notify) {
        if (!controller.canRun()) {
            if (!nodes.isEmpty()) {
                disconnectAll();
            }

            return;
        }

        World world = getWorld();

        List<INetworkNode> newNodes = new ArrayList<>();
        Set<Integer> newNodeHashes = new HashSet<>();

        Set<BlockPos> checked = new HashSet<>();
        Queue<BlockPos> toCheck = new ArrayDeque<>();

        checked.add(start);
        toCheck.add(start);

        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos pos = start.offset(facing);

            checked.add(pos);
            toCheck.add(pos);
        }

        BlockPos currentPos;
        while ((currentPos = toCheck.poll()) != null) {
            TileEntity tile = world.getTileEntity(currentPos);

            if (tile instanceof TileController && !controller.getPos().equals(tile.getPos())) {
                world.createExplosion(null, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), 4.5f, true);
            }

            if (!(tile instanceof INetworkNode)) {
                continue;
            }

            INetworkNode node = (INetworkNode) tile;

            newNodes.add(node);
            newNodeHashes.add(hashNode(world, node));

            if (tile instanceof TileNetworkTransmitter) {
                final TileNetworkTransmitter transmitter = (TileNetworkTransmitter) tile;

                if (transmitter.canTransmit()) {
                    if (!transmitter.isSameDimension()) {
                        NetworkNodeGraph dimensionGraph = new NetworkNodeGraph(controller) {
                            @Override
                            public World getWorld() {
                                return DimensionManager.getWorld(transmitter.getReceiverDimension());
                            }
                        };

                        dimensionGraph.rebuild(transmitter.getReceiver(), false);

                        newNodes.addAll(dimensionGraph.all());
                        newNodeHashes.addAll(dimensionGraph.allHashes());
                    } else {
                        BlockPos receiver = transmitter.getReceiver();

                        if (checked.add(receiver)) {
                            toCheck.add(receiver);
                        }
                    }
                }
            }

            for (EnumFacing facing : EnumFacing.VALUES) {
                if (node.canConduct(facing)) {
                    BlockPos pos = currentPos.offset(facing);

                    if (checked.add(pos)) {
                        toCheck.add(pos);
                    }
                }
            }
        }

        List<INetworkNode> oldNodes = new ArrayList<>(nodes);
        Set<Integer> oldNodeHashes = new HashSet<>(nodeHashes);

        this.nodes = newNodes;
        this.nodeHashes = newNodeHashes;

        if (notify) {
            for (INetworkNode newNode : nodes) {
                if (!oldNodeHashes.contains(hashNode(newNode.getNodeWorld(), newNode))) {
                    newNode.onConnected(controller);
                }
            }

            for (INetworkNode oldNode : oldNodes) {
                if (!nodeHashes.contains(hashNode(oldNode.getNodeWorld(), oldNode))) {
                    oldNode.onDisconnected(controller);
                }
            }
        }
    }

    @Override
    public List<INetworkNode> all() {
        return nodes;
    }

    @Override
    public Set<Integer> allHashes() {
        return nodeHashes;
    }

    @Override
    public void disconnectAll() {
        for (INetworkNode node : nodes) {
            if (node.isConnected()) {
                node.onDisconnected(controller);
            }
        }

        nodes.clear();
        nodeHashes.clear();
    }

    public World getWorld() {
        return controller.getWorld();
    }
}
