package com.raoulvdberge.refinedstorage.apiimpl.network;

import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.api.network.INetworkNeighborhoodAware;
import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeGraph;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeProxy;
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

import static com.raoulvdberge.refinedstorage.proxy.CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY;

public class NetworkNodeGraph implements INetworkNodeGraph {
    private TileController controller;

    private List<INetworkNode> nodes = new ArrayList<>();

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

        Set<INetworkNode> newNodes = new HashSet<>();
        Queue<NodeToCheck> toCheck = new ArrayDeque<>();

        INetworkNeighborhoodAware.Operator operator = (world, pos, side) -> {
            TileEntity tile = world.getTileEntity(pos);

            if (tile != null && !tile.isInvalid()) {
                if (tile instanceof TileController) {
                    removeOtherController(world, pos);
                } else if (tile.hasCapability(NETWORK_NODE_PROXY_CAPABILITY, side)) {
                    INetworkNodeProxy otherNodeProxy = NETWORK_NODE_PROXY_CAPABILITY.cast(tile.getCapability(NETWORK_NODE_PROXY_CAPABILITY, side));
                    INetworkNode otherNode = otherNodeProxy.getNode();

                    if (otherNode != null && newNodes.add(otherNode)) {
                        toCheck.add(new NodeToCheck(otherNode, world, pos, side, tile));
                    }
                }
            }
        };

        BlockPos controllerPos = controller.getPos();
        World controllerWorld = controller.getWorld();

        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos pos = controllerPos.offset(facing);
            operator.apply(controllerWorld, pos, facing.getOpposite());
        }

        NodeToCheck currentNodeToCheck;
        while ((currentNodeToCheck = toCheck.poll()) != null) {
            currentNodeToCheck.walkNeighborhood(operator);
        }

        List<INetworkNode> oldNodes = nodes;
        nodes = new ArrayList<>(newNodes);

        boolean changed = false;

        for (INetworkNode node : nodes) {
            if (!oldNodes.contains(node)) {
                node.onConnected(controller);

                changed = true;
            }
        }

        for (INetworkNode oldNode : oldNodes) {
            if (!nodes.contains(oldNode)) {
                oldNode.onDisconnected(controller);

                changed = true;
            }
        }

        if (changed) {
            controller.getDataManager().sendParameterToWatchers(TileController.NODES);
        }
    }

    @Override
    public List<INetworkNode> all() {
        return nodes;
    }

    @Override
    public void disconnectAll() {
        List<INetworkNode> oldNodes = new ArrayList<>(nodes);

        nodes.clear();

        for (INetworkNode node : oldNodes) {
            if (node.getNetwork() == controller) {
                node.onDisconnected(controller);
            }
        }

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

    private class NodeToCheck implements INetworkNeighborhoodAware {
        private final INetworkNode node;
        private final World world;
        private final BlockPos pos;
        private final EnumFacing side;
        private final TileEntity tile;

        NodeToCheck(INetworkNode node, World world, BlockPos pos, EnumFacing side, TileEntity tile) {
            this.node = node;
            this.world = world;
            this.pos = pos;
            this.side = side;
            this.tile = tile;
        }

        @Override
        public void walkNeighborhood(Operator operator) {
            if (node instanceof INetworkNeighborhoodAware) {
                ((INetworkNeighborhoodAware) node).walkNeighborhood(operator);
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
