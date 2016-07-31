package refinedstorage.apiimpl.network;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import refinedstorage.api.network.INetworkNode;
import refinedstorage.api.network.INetworkNodeGraph;
import refinedstorage.tile.TileNetworkTransmitter;
import refinedstorage.tile.controller.TileController;

import java.util.*;

public class NetworkNodeGraph implements INetworkNodeGraph {
    private TileController controller;

    private List<INetworkNode> nodes = new ArrayList<INetworkNode>();
    private Set<BlockPos> nodesPos = new HashSet<BlockPos>();

    public NetworkNodeGraph(TileController controller) {
        this.controller = controller;
    }

    @Override
    public void rebuild(BlockPos start) {
        if (!controller.canRun()) {
            if (!nodes.isEmpty()) {
                disconnectAll();
            }

            return;
        }

        World world = getWorld();

        List<INetworkNode> newNodes = new ArrayList<INetworkNode>();
        List<INetworkNode> interDimensionalNodes = new ArrayList<INetworkNode>();
        Set<BlockPos> newNodesPos = new HashSet<BlockPos>();

        Set<BlockPos> checked = new HashSet<BlockPos>();
        Queue<BlockPos> toCheck = new ArrayDeque<BlockPos>();

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
            newNodesPos.add(node.getPosition());

            if (tile instanceof TileNetworkTransmitter) {
                final TileNetworkTransmitter transmitter = (TileNetworkTransmitter) tile;

                if (transmitter.canTransmit()) {
                    if (!transmitter.isInSameDimension()) {
                        NetworkNodeGraph dimensionGraph = new NetworkNodeGraph(controller) {
                            @Override
                            public World getWorld() {
                                return DimensionManager.getWorld(transmitter.getReceiverDimension());
                            }
                        };

                        dimensionGraph.rebuild(transmitter.getReceiver());

                        interDimensionalNodes.addAll(dimensionGraph.all());
                    } else {
                        BlockPos receiver = transmitter.getReceiver();

                        if (checked.add(receiver)) {
                            toCheck.add(receiver);
                        }
                    }
                }
            }

            if (node.canConduct()) {
                for (EnumFacing facing : EnumFacing.VALUES) {
                    BlockPos pos = currentPos.offset(facing);

                    if (checked.add(pos)) {
                        toCheck.add(pos);
                    }
                }
            }
        }

        List<INetworkNode> oldNodes = new ArrayList<INetworkNode>(nodes);
        Set<BlockPos> oldNodesPos = new HashSet<BlockPos>(nodesPos);

        this.nodes = newNodes;
        this.nodesPos = newNodesPos;

        for (INetworkNode newNode : nodes) {
            if (!oldNodesPos.contains(newNode.getPosition())) {
                newNode.onConnected(controller);
            }
        }

        for (INetworkNode oldNode : oldNodes) {
            if (!nodesPos.contains(oldNode.getPosition())) {
                oldNode.onDisconnected(controller);
            }
        }

        this.nodes.addAll(interDimensionalNodes);
    }

    @Override
    public List<INetworkNode> all() {
        return nodes;
    }

    @Override
    public void disconnectAll() {
        for (INetworkNode node : nodes) {
            if (node.isConnected()) {
                node.onDisconnected(controller);
            }
        }

        nodes.clear();
        nodesPos.clear();
    }

    public World getWorld() {
        return controller.getWorld();
    }
}
