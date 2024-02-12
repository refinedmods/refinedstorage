package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.INetworkManager;
import com.refinedmods.refinedstorage.api.network.INetworkNodeGraphEntry;
import com.refinedmods.refinedstorage.api.network.NetworkType;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.Network;
import com.refinedmods.refinedstorage.apiimpl.network.node.RootNetworkNode;
import com.refinedmods.refinedstorage.blockentity.config.IRedstoneConfigurable;
import com.refinedmods.refinedstorage.blockentity.config.RedstoneMode;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import com.refinedmods.refinedstorage.blockentity.data.RSSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ControllerBlockEntity extends BaseBlockEntity implements INetworkNodeProxy<RootNetworkNode>, IRedstoneConfigurable {
    private static final Logger LOGGER = LogManager.getLogger();

    public static final BlockEntitySynchronizationParameter<Integer, ControllerBlockEntity> REDSTONE_MODE = RedstoneMode.createParameter(new ResourceLocation(RS.ID, "controller_redstone_mode"));
    public static final BlockEntitySynchronizationParameter<Integer, ControllerBlockEntity> ENERGY_USAGE = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "controller_energy_usage"), EntityDataSerializers.INT, 0, t -> t.getNetwork().getEnergyUsage());
    public static final BlockEntitySynchronizationParameter<Integer, ControllerBlockEntity> ENERGY_STORED = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "controller_energy_stored"), EntityDataSerializers.INT, 0, t -> t.getNetwork().getEnergyStorage().getEnergyStored());
    public static final BlockEntitySynchronizationParameter<Integer, ControllerBlockEntity> ENERGY_CAPACITY = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "controller_energy_capacity"), EntityDataSerializers.INT, 0, t -> t.getNetwork().getEnergyStorage().getMaxEnergyStored());
    public static final BlockEntitySynchronizationParameter<List<ClientNode>, ControllerBlockEntity> NODES = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "controller_nodes"), RSSerializers.CLIENT_NODE_SERIALIZER, new ArrayList<>(), ControllerBlockEntity::collectClientNodes);

    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
        .addWatchedParameter(REDSTONE_MODE)
        .addWatchedParameter(ENERGY_USAGE)
        .addWatchedParameter(ENERGY_STORED)
        .addParameter(ENERGY_CAPACITY)
        .addParameter(NODES)
        .build();

    private final NetworkType type;
    private INetwork removedNetwork;
    private Network dummyNetwork;

    public ControllerBlockEntity(NetworkType type, BlockPos pos, BlockState state) {
        super(type == NetworkType.CREATIVE ? RSBlockEntities.CREATIVE_CONTROLLER.get() : RSBlockEntities.CONTROLLER.get(), pos, state, SPEC);
        this.type = type;
    }

    private static List<ClientNode> collectClientNodes(ControllerBlockEntity blockEntity) {
        List<ClientNode> nodes = new ArrayList<>();

        for (INetworkNodeGraphEntry entry : blockEntity.getNetwork().getNodeGraph().all()) {
            if (entry.getNode().isActive()) {
                ItemStack stack = entry.getNode().getItemStack();

                if (stack.isEmpty()) {
                    continue;
                }

                ClientNode clientNode = new ClientNode(stack, 1, entry.getNode().getEnergyUsage());

                if (nodes.contains(clientNode)) {
                    ClientNode other = nodes.get(nodes.indexOf(clientNode));

                    other.setAmount(other.getAmount() + 1);
                } else {
                    nodes.add(clientNode);
                }
            }
        }

        nodes.sort((a, b) -> Integer.compare(b.getEnergyUsage(), a.getEnergyUsage()));

        return nodes;
    }

    public INetwork getNetwork() {
        if (level.isClientSide) {
            if (dummyNetwork == null) {
                dummyNetwork = new Network(level, worldPosition, type);
            }

            return dummyNetwork;
        }

        INetworkManager manager = API.instance().getNetworkManager((ServerLevel) level);
        INetwork network = manager.getNetwork(worldPosition);

        if (network == null) {
            LOGGER.warn("Expected a network @ {} but couldn't find it, creating a new one...", worldPosition);
            network = new Network(level, worldPosition, type);
            manager.setNetwork(worldPosition, network);
            manager.markForSaving();
        }

        return network;
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();

        if (!level.isClientSide) {
            INetworkManager manager = API.instance().getNetworkManager((ServerLevel) level);

            if (manager.getNetwork(worldPosition) == null) {
                manager.setNetwork(worldPosition, new Network(level, worldPosition, type));
                manager.markForSaving();
            }
        }
    }

    @Override
    public void onRemovedNotDueToChunkUnload() {
        super.onRemovedNotDueToChunkUnload();

        if (!level.isClientSide) {
            INetworkManager manager = API.instance().getNetworkManager((ServerLevel) level);

            INetwork network = manager.getNetwork(worldPosition);

            removedNetwork = network;

            manager.removeNetwork(worldPosition);
            manager.markForSaving();

            network.onRemoved();
        }
    }

    public INetwork getRemovedNetwork() {
        return removedNetwork;
    }

    @Override
    @Nonnull
    public RootNetworkNode getNode() {
        return ((Network) getNetwork()).getRoot();
    }

    @Override
    public RedstoneMode getRedstoneMode() {
        return ((Network) getNetwork()).getRedstoneMode();
    }

    @Override
    public void setRedstoneMode(RedstoneMode mode) {
        ((Network) getNetwork()).setRedstoneMode(mode);
    }
}
