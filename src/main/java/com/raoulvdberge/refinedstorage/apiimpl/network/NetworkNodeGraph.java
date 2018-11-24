package com.raoulvdberge.refinedstorage.apiimpl.network;

import com.google.common.collect.Sets;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeGraph;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeGraphListener;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeVisitor;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.apiimpl.util.OneSixMigrationHelper;
import com.raoulvdberge.refinedstorage.capability.CapabilityNetworkNodeProxy;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

import static com.raoulvdberge.refinedstorage.capability.CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY;

public class NetworkNodeGraph implements INetworkNodeGraph {
    private INetwork network;
    private Set<INetworkNode> nodes = Sets.newConcurrentHashSet();
    private List<INetworkNodeGraphListener> listeners = new LinkedList<>();

    private Set<Consumer<INetwork>> actions = new HashSet<>();

    private boolean invalidating = false;

    public NetworkNodeGraph(INetwork network) {
        this.network = network;
    }

    @Override
    public void invalidate(Action action, World world, BlockPos origin) {
        this.invalidating = true;

        Operator operator = new Operator(action);

        TileEntity tile = world.getTileEntity(origin);
        if (tile != null && tile.hasCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, null)) {
            INetworkNodeProxy proxy = tile.getCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, null);

            if (proxy != null) {
                INetworkNode node = proxy.getNode();

                if (node instanceof INetworkNodeVisitor) {
                    ((INetworkNodeVisitor) node).visit(operator);
                }
            }
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
    @SuppressWarnings("deprecation")
    public INetwork getNetworkForBCReasons() {
        OneSixMigrationHelper.removalHook();

        return network;
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
        return network.world();
    }

    private void dropConflictingBlock(World world, BlockPos pos) {
        if (!network.getPosition().equals(pos)) {
            IBlockState state = world.getBlockState(pos);

            NonNullList<ItemStack> drops = NonNullList.create();
            state.getBlock().getDrops(drops, world, pos, state, 0);

            world.setBlockToAir(pos);

            for (ItemStack drop : drops) {
                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), drop);
            }
        }
    }

    private class Operator implements INetworkNodeVisitor.Operator {
        private Set<INetworkNode> foundNodes = Sets.newConcurrentHashSet(); // All scanned nodes

        private Set<INetworkNode> newNodes = Sets.newConcurrentHashSet(); // All scanned new nodes, that didn't appear in the list before
        private Set<INetworkNode> previousNodes = Sets.newConcurrentHashSet(nodes); // All unscanned nodes (nodes that were in the previous list, but not in the new list)

        private Queue<Visitor> toCheck = new ArrayDeque<>();

        private Action action;

        public Operator(Action action) {
            this.action = action;
        }

        @Override
        public void apply(World world, BlockPos pos, @Nullable EnumFacing side) {
            TileEntity tile = world.getTileEntity(pos);

            if (tile != null && tile.hasCapability(NETWORK_NODE_PROXY_CAPABILITY, side)) {
                INetworkNodeProxy otherNodeProxy = NETWORK_NODE_PROXY_CAPABILITY.cast(tile.getCapability(NETWORK_NODE_PROXY_CAPABILITY, side));
                INetworkNode otherNode = otherNodeProxy.getNode();

                if (otherNode.getNetwork() != null && !otherNode.getNetwork().equals(network)) {
                    if (action == Action.PERFORM) {
                        dropConflictingBlock(world, tile.getPos());
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

        @Override
        public Action getAction() {
            return action;
        }
    }

    private class Visitor implements INetworkNodeVisitor {
        private final INetworkNode node;
        private final World world;
        private final BlockPos pos;
        private final EnumFacing side;
        private final TileEntity tile;

        Visitor(INetworkNode node, World world, BlockPos pos, EnumFacing side, TileEntity tile) {
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
                for (EnumFacing checkSide : EnumFacing.VALUES) {
                    if (checkSide != side) { // Avoid going backward
                        INetworkNodeProxy nodeOnSideProxy = NETWORK_NODE_PROXY_CAPABILITY.cast(tile.getCapability(NETWORK_NODE_PROXY_CAPABILITY, checkSide));

                        if (nodeOnSideProxy != null) {
                            INetworkNode nodeOnSide = nodeOnSideProxy.getNode();

                            if (nodeOnSide == node) {
                                operator.apply(world, pos.offset(checkSide), checkSide.getOpposite());
                            }
                        }
                    }
                }
            }
        }
    }
}
