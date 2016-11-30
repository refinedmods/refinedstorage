package com.raoulvdberge.refinedstorage.apiimpl.network;

import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeGraph;
import com.raoulvdberge.refinedstorage.api.storage.IStorageProvider;
import com.raoulvdberge.refinedstorage.item.ItemBlockController;
import com.raoulvdberge.refinedstorage.tile.TileController;
import com.raoulvdberge.refinedstorage.tile.TileNetworkTransmitter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.util.*;

public class NetworkNodeGraph implements INetworkNodeGraph {
    private TileController controller;

    private List<INetworkNode> nodes = new ArrayList<>();

    public NetworkNodeGraph(TileController controller) {
        this.controller = controller;
    }

    @Override
    public void rebuild(BlockPos start, boolean notify) {
        if (start == null) {
            start = controller.getPosition();
        }

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
                world.setBlockToAir(currentPos);

                IBlockState state = world.getBlockState(currentPos);

                InventoryHelper.spawnItemStack(
                    world,
                    currentPos.getX(),
                    currentPos.getY(),
                    currentPos.getZ(),
                    ItemBlockController.createStackWithNBT(new ItemStack(RSBlocks.CONTROLLER, 1, state.getBlock().getMetaFromState(state)))
                );

                continue;
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

        List<INetworkNode> oldNodes = new ArrayList<>(nodes);

        this.nodes = newNodes;

        boolean changed = false;

        if (notify) {
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
    public void replace(INetworkNode node) {
        nodes.remove(node);
        nodes.add(node);

        if (node instanceof ICraftingPatternContainer) {
            controller.rebuildPatterns();
        }

        if (node instanceof IStorageProvider) {
            controller.getItemStorageCache().invalidate();
            controller.getFluidStorageCache().invalidate();
        }

        controller.getDataManager().sendParameterToWatchers(TileController.NODES);
    }

    @Override
    public void disconnectAll() {
        List<INetworkNode> oldNodes = new ArrayList<>(nodes);

        nodes.clear();

        for (INetworkNode node : oldNodes) {
            if (node.isConnected()) {
                node.onDisconnected(controller);
            }
        }

        controller.getDataManager().sendParameterToWatchers(TileController.NODES);
    }

    protected World getWorld() {
        return controller.getWorld();
    }
}
