package com.raoulvdberge.refinedstorage.apiimpl.network;

import com.google.common.collect.Sets;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeGraph;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeVisitor;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.item.ItemBlockController;
import com.raoulvdberge.refinedstorage.tile.TileController;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Consumer;

import static com.raoulvdberge.refinedstorage.capability.CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY;

public class NetworkNodeGraph implements INetworkNodeGraph {
    private TileController controller;
    private Set<INetworkNode> nodes = Sets.newConcurrentHashSet();
    private Set<Consumer<INetwork>> postRebuildActions = new HashSet<>();
    private boolean rebuilding = false;

    public NetworkNodeGraph(TileController controller) {
        this.controller = controller;
    }

    @Override
    public void rebuild() {
        if (!controller.canRun()) {
            if (!nodes.isEmpty()) {
                disconnectAll();
            }

            return;
        }

        rebuilding = true;

        Operator operator = new Operator();

        BlockPos controllerPos = controller.getPos();
        World controllerWorld = controller.getWorld();

        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos pos = controllerPos.offset(facing);
            operator.apply(controllerWorld, pos, facing.getOpposite());
        }

        Visitor currentVisitor;
        while ((currentVisitor = operator.toCheck.poll()) != null) {
            currentVisitor.visit(operator);
        }

        this.nodes = operator.foundNodes;

        for (INetworkNode node : operator.newNodes) {
            node.onConnected(controller);
        }

        for (INetworkNode node : operator.previousNodes) {
            node.onDisconnected(controller);
        }

        postRebuildActions.forEach(a -> a.accept(controller));
        postRebuildActions.clear();

        if (!operator.newNodes.isEmpty() || !operator.previousNodes.isEmpty()) {
            controller.getDataManager().sendParameterToWatchers(TileController.NODES);
        }

        rebuilding = false;
    }

    @Override
    public void schedulePostRebuildAction(Consumer<INetwork> action) {
        if (rebuilding) {
            postRebuildActions.add(action);
        } else {
            action.accept(controller);
        }
    }

    @Override
    public Collection<INetworkNode> all() {
        return nodes;
    }

    @Override
    public void disconnectAll() {
        nodes.forEach(n -> n.onDisconnected(controller));
        nodes.clear();

        controller.getDataManager().sendParameterToWatchers(TileController.NODES);
    }

    protected World getWorld() {
        return controller.getWorld();
    }

    private void removeOtherController(World world, BlockPos otherControllerPos) {
        if (!controller.getPos().equals(otherControllerPos)) {
            IBlockState state = world.getBlockState(otherControllerPos);

            ItemStack stackToSpawn = ItemBlockController.createStackWithNBT(new ItemStack(RSBlocks.CONTROLLER, 1, state.getBlock().getMetaFromState(state)));

            world.setBlockToAir(otherControllerPos);

            InventoryHelper.spawnItemStack(
                world,
                otherControllerPos.getX(),
                otherControllerPos.getY(),
                otherControllerPos.getZ(),
                stackToSpawn
            );
        }
    }

    private class Operator implements INetworkNodeVisitor.Operator {
        private Set<INetworkNode> foundNodes = Sets.newConcurrentHashSet(); // All scanned nodes

        private Set<INetworkNode> newNodes = Sets.newConcurrentHashSet(); // All scanned new nodes, that didn't appear in the list before
        private Set<INetworkNode> previousNodes = Sets.newConcurrentHashSet(nodes); // All unscanned nodes (nodes that were in the previous list, but not in the new list)

        private Queue<Visitor> toCheck = new ArrayDeque<>();

        @Override
        public void apply(World world, BlockPos pos, EnumFacing side) {
            TileEntity tile = world.getTileEntity(pos);

            if (tile != null) {
                if (tile instanceof TileController) {
                    removeOtherController(world, pos);
                } else if (tile.hasCapability(NETWORK_NODE_PROXY_CAPABILITY, side)) {
                    INetworkNodeProxy otherNodeProxy = NETWORK_NODE_PROXY_CAPABILITY.cast(tile.getCapability(NETWORK_NODE_PROXY_CAPABILITY, side));
                    INetworkNode otherNode = otherNodeProxy.getNode();

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
