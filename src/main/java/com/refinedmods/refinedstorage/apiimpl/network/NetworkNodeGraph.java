package com.refinedmods.refinedstorage.apiimpl.network;

import com.google.common.collect.Sets;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.INetworkNodeGraph;
import com.refinedmods.refinedstorage.api.network.INetworkNodeGraphListener;
import com.refinedmods.refinedstorage.api.network.INetworkNodeVisitor;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public class NetworkNodeGraph implements INetworkNodeGraph {
    private final INetwork network;
    private Set<INetworkNode> nodes = Sets.newConcurrentHashSet();
    private final List<INetworkNodeGraphListener> listeners = new LinkedList<>();

    private final Set<Consumer<INetwork>> actions = new HashSet<>();

    private boolean invalidating = false;

    public NetworkNodeGraph(INetwork network) {
        this.network = network;
    }

    @Override
    public void invalidate(Action action, World world, BlockPos origin) {
        this.invalidating = true;

        Operator operator = new Operator(action);

        INetworkNode originNode = NetworkUtils.getNodeFromTile(world.getTileEntity(origin));
        if (originNode instanceof INetworkNodeVisitor) {
            ((INetworkNodeVisitor) originNode).visit(operator);
        }

        Visitor currentVisitor;
        while ((currentVisitor = operator.toCheck.poll()) != null) {
            currentVisitor.visit(operator);
        }

        this.nodes = operator.foundNodes;

        if (action == Action.PERFORM) {
            for (INetworkNode node : operator.newNodes) {
                node.onConnected(network);
            }

            for (INetworkNode node : operator.previousNodes) {
                node.onDisconnected(network);
            }

            actions.forEach(h -> h.accept(network));
            actions.clear();

            if (!operator.newNodes.isEmpty() || !operator.previousNodes.isEmpty()) {
                listeners.forEach(INetworkNodeGraphListener::onChanged);
            }
        }

        this.invalidating = false;
    }

    @Override
    public void runActionWhenPossible(Consumer<INetwork> handler) {
        if (invalidating) {
            actions.add(handler);
        } else {
            handler.accept(network);
        }
    }

    @Override
    public Collection<INetworkNode> all() {
        return nodes;
    }

    @Override
    public void addListener(INetworkNodeGraphListener listener) {
        listeners.add(listener);
    }

    @Override
    public void disconnectAll() {
        nodes.forEach(n -> n.onDisconnected(network));
        nodes.clear();

        listeners.forEach(INetworkNodeGraphListener::onChanged);
    }

    protected World getWorld() {
        return network.getWorld();
    }

    private class Operator implements INetworkNodeVisitor.Operator {
        private final Set<INetworkNode> foundNodes = Sets.newConcurrentHashSet(); // All scanned nodes

        private final Set<INetworkNode> newNodes = Sets.newConcurrentHashSet(); // All scanned new nodes, that didn't appear in the list before
        private final Set<INetworkNode> previousNodes = Sets.newConcurrentHashSet(nodes); // All unscanned nodes (nodes that were in the previous list, but not in the new list)

        private final Queue<Visitor> toCheck = new ArrayDeque<>();

        private final Action action;

        public Operator(Action action) {
            this.action = action;
        }

        @Override
        public void apply(World world, BlockPos pos, @Nullable Direction side) {
            TileEntity tile = world.getTileEntity(pos);

            INetworkNode otherNode = NetworkUtils.getNodeFromTile(tile);

            if (otherNode != null) {
                if (otherNode.getNetwork() != null && !otherNode.getNetwork().equals(network)) {
                    if (action == Action.PERFORM) {
                        dropConflictingBlock(world, pos);
                    }

                    return;
                }

                if (foundNodes.add(otherNode)) {
                    if (!nodes.contains(otherNode)) {
                        // We can't let the node connect immediately
                        // We can only let the node connect AFTER the nodes list has changed in the graph
                        // This is so that storage nodes can refresh the item/fluid cache, and the item/fluid cache will notice it then (otherwise not)
                        newNodes.add(otherNode);
                    }

                    previousNodes.remove(otherNode);

                    toCheck.add(new Visitor(otherNode, world, pos, side, tile));
                }
            }
        }

        private void dropConflictingBlock(World world, BlockPos pos) {
            if (!network.getPosition().equals(pos)) {
                Block.spawnDrops(world.getBlockState(pos), world, pos, world.getTileEntity(pos));

                world.removeBlock(pos, false);
            }
        }

        @Override
        public Action getAction() {
            return action;
        }
    }

    private static class Visitor implements INetworkNodeVisitor {
        private final INetworkNode node;
        private final World world;
        private final BlockPos pos;
        private final Direction side;
        private final TileEntity tile;

        Visitor(INetworkNode node, World world, BlockPos pos, Direction side, TileEntity tile) {
            this.node = node;
            this.world = world;
            this.pos = pos;
            this.side = side;
            this.tile = tile;
        }

        @Override
        public void visit(Operator operator) {
            if (node instanceof INetworkNodeVisitor) {
                ((INetworkNodeVisitor) node).visit(operator);
            } else {
                for (Direction checkSide : Direction.values()) {
                    if (checkSide != side) { // Avoid going backward
                        INetworkNode nodeOnSide = NetworkUtils.getNodeFromTile(tile);

                        if (nodeOnSide == node) {
                            operator.apply(world, pos.offset(checkSide), checkSide.getOpposite());
                        }
                    }
                }
            }
        }
    }
}
