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

    public NetworkNodeGraph(TileController controller) {
        this.controller = controller;
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

            if (tile instanceof TileController && !controller.getPos().equals(currentPos)) {
                world.createExplosion(null, currentPos.getX(), currentPos.getY(), currentPos.getZ(), 1.5f, true);
            }

            if (!(tile instanceof INetworkNode)) {
                continue;
            }

            INetworkNode node = (INetworkNode) tile;

            newNodes.add(node);

            if (tile instanceof TileNetworkTransmitter) {
                final TileNetworkTransmitter transmitter = (TileNetworkTransmitter) tile;

                if (transmitter.canTransmit()) {
                    if (!transmitter.isSameDimension()) {
                        final World dimensionWorld = DimensionManager.getWorld(transmitter.getReceiverDimension());

                        if (dimensionWorld != null) {
                            NetworkNodeGraph dimensionGraph = new NetworkNodeGraph(controller) {
                                @Override
                                public World getWorld() {
                                    return dimensionWorld;
                                }
                            };

                            dimensionGraph.rebuild(transmitter.getReceiver(), false);

                            newNodes.addAll(dimensionGraph.all());
                        }
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

        boolean changed = false;

        if (notify) {
            for (INetworkNode newNode : newNodes) {
                if (!nodes.contains(newNode)) {
                    newNode.onConnected(controller);

                    changed = true;
                }
            }

            for (INetworkNode oldNode : nodes) {
                if (!newNodes.contains(oldNode)) {
                    oldNode.onDisconnected(controller);

                    changed = true;
                }
            }
        }

        this.nodes = newNodes;

        if (changed) {
            controller.getDataManager().sendParameterToWatchers(TileController.NODES);
        }
    }

    @Override
    public List<INetworkNode> all() {
        return nodes;
    }

    @Override
    public void replace(INetworkNode node) {
        nodes.remove(node);
        nodes.add(node);
    }

    @Override
    public void disconnectAll() {
        for (INetworkNode node : nodes) {
            if (node.isConnected()) {
                node.onDisconnected(controller);
            }
        }

        nodes.clear();

        controller.getDataManager().sendParameterToWatchers(TileController.NODES);
    }

    public World getWorld() {
        return controller.getWorld();
    }
}
