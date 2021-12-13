package com.refinedmods.refinedstorage.apiimpl.network;

import com.google.common.collect.Sets;
import com.refinedmods.refinedstorage.api.network.*;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public class NetworkNodeGraph implements INetworkNodeGraph {
    private final INetwork network;
    private final List<INetworkNodeGraphListener> listeners = new LinkedList<>();
    private final Set<Consumer<INetwork>> actions = new HashSet<>();
    private Set<INetworkNodeGraphEntry> entries = Sets.newConcurrentHashSet();
    private boolean invalidating = false;

    public NetworkNodeGraph(INetwork network) {
        this.network = network;
    }

    @Override
    public void invalidate(Action action, Level level, BlockPos origin) {
        this.invalidating = true;

        Operator operator = new Operator(action);

        INetworkNode originNode = NetworkUtils.getNodeFromBlockEntity(level.getBlockEntity(origin));
        if (originNode instanceof INetworkNodeVisitor) {
            ((INetworkNodeVisitor) originNode).visit(operator);
        }

        Visitor currentVisitor;
        while ((currentVisitor = operator.toCheck.poll()) != null) {
            currentVisitor.visit(operator);
        }

        this.entries = operator.foundNodes;

        if (action == Action.PERFORM) {
            for (INetworkNodeGraphEntry entry : operator.newEntries) {
                entry.getNode().onConnected(network);
            }

            for (INetworkNodeGraphEntry entry : operator.previousEntries) {
                entry.getNode().onDisconnected(network);
            }

            actions.forEach(h -> h.accept(network));
            actions.clear();

            if (!operator.newEntries.isEmpty() || !operator.previousEntries.isEmpty()) {
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
    public Collection<INetworkNodeGraphEntry> all() {
        return entries;
    }

    @Override
    public void addListener(INetworkNodeGraphListener listener) {
        listeners.add(listener);
    }

    @Override
    public void disconnectAll() {
        entries.forEach(entry -> entry.getNode().onDisconnected(network));
        entries.clear();

        listeners.forEach(INetworkNodeGraphListener::onChanged);
    }

    protected Level getWorld() {
        return network.getLevel();
    }

    private static class Visitor implements INetworkNodeVisitor {
        private final INetworkNode node;
        private final Level level;
        private final BlockPos pos;
        private final Direction side;
        private final BlockEntity blockEntity;

        Visitor(INetworkNode node, Level level, BlockPos pos, Direction side, BlockEntity blockEntity) {
            this.node = node;
            this.level = level;
            this.pos = pos;
            this.side = side;
            this.blockEntity = blockEntity;
        }

        @Override
        public void visit(Operator operator) {
            if (node instanceof INetworkNodeVisitor) {
                ((INetworkNodeVisitor) node).visit(operator);
            } else {
                for (Direction checkSide : Direction.values()) {
                    if (checkSide != side) { // Avoid going backward
                        INetworkNode nodeOnSide = NetworkUtils.getNodeFromBlockEntity(blockEntity);

                        if (nodeOnSide == node) {
                            operator.apply(level, pos.relative(checkSide), checkSide.getOpposite());
                        }
                    }
                }
            }
        }
    }

    private class Operator implements INetworkNodeVisitor.Operator {
        private final Set<INetworkNodeGraphEntry> foundNodes = Sets.newConcurrentHashSet(); // All scanned entries

        private final Set<INetworkNodeGraphEntry> newEntries = Sets.newConcurrentHashSet(); // All scanned new entries, that didn't appear in the list before
        private final Set<INetworkNodeGraphEntry> previousEntries = Sets.newConcurrentHashSet(entries); // All unscanned entries (entries that were in the previous list, but not in the new list)

        private final Queue<Visitor> toCheck = new ArrayDeque<>();

        private final Action action;

        public Operator(Action action) {
            this.action = action;
        }

        @Override
        public void apply(Level level, BlockPos pos, @Nullable Direction side) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            INetworkNode otherNode = NetworkUtils.getNodeFromBlockEntity(blockEntity);
            if (otherNode != null) {
                NetworkNodeGraphEntry otherNodeItem = new NetworkNodeGraphEntry(otherNode);

                if (otherNode.getNetwork() != null && !otherNode.getNetwork().equals(network)) {
                    if (action == Action.PERFORM) {
                        dropConflictingBlock(level, pos);
                    }

                    return;
                }

                if (foundNodes.add(otherNodeItem)) {
                    if (!entries.contains(otherNodeItem)) {
                        // We can't let the node connect immediately
                        // We can only let the node connect AFTER the nodes list has changed in the graph
                        // This is so that storage nodes can refresh the item/fluid cache, and the item/fluid cache will notice it then (otherwise not)
                        newEntries.add(otherNodeItem);
                    }

                    previousEntries.remove(otherNodeItem);

                    toCheck.add(new Visitor(otherNode, level, pos, side, blockEntity));
                }
            }
        }

        private void dropConflictingBlock(Level level, BlockPos pos) {
            if (!network.getPosition().equals(pos)) {
                Block.dropResources(level.getBlockState(pos), level, pos, level.getBlockEntity(pos));
                level.removeBlock(pos, false);
            }
        }

        @Override
        public Action getAction() {
            return action;
        }
    }
}
