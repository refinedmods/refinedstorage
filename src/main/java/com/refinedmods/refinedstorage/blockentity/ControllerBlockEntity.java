package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.INetworkManager;
import com.refinedmods.refinedstorage.api.network.INetworkNodeGraphEntry;
import com.refinedmods.refinedstorage.api.network.NetworkType;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.Network;
import com.refinedmods.refinedstorage.apiimpl.network.node.RootNetworkNode;
import com.refinedmods.refinedstorage.capability.NetworkNodeProxyCapability;
import com.refinedmods.refinedstorage.blockentity.config.IRedstoneConfigurable;
import com.refinedmods.refinedstorage.blockentity.config.RedstoneMode;
import com.refinedmods.refinedstorage.blockentity.data.RSSerializers;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ControllerBlockEntity extends BaseBlockEntity implements INetworkNodeProxy<RootNetworkNode>, IRedstoneConfigurable {
    public static final BlockEntitySynchronizationParameter<Integer, ControllerBlockEntity> REDSTONE_MODE = RedstoneMode.createParameter();
    public static final BlockEntitySynchronizationParameter<Integer, ControllerBlockEntity> ENERGY_USAGE = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, 0, t -> t.getNetwork().getEnergyUsage());
    public static final BlockEntitySynchronizationParameter<Integer, ControllerBlockEntity> ENERGY_STORED = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, 0, t -> t.getNetwork().getEnergyStorage().getEnergyStored());
    public static final BlockEntitySynchronizationParameter<Integer, ControllerBlockEntity> ENERGY_CAPACITY = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, 0, t -> t.getNetwork().getEnergyStorage().getMaxEnergyStored());
    public static final BlockEntitySynchronizationParameter<List<ClientNode>, ControllerBlockEntity> NODES = new BlockEntitySynchronizationParameter<>(RSSerializers.CLIENT_NODE_SERIALIZER, new ArrayList<>(), ControllerBlockEntity::collectClientNodes);

    private final LazyOptional<INetworkNodeProxy<RootNetworkNode>> networkNodeProxyCap = LazyOptional.of(() -> this);
    private final NetworkType type;
    private INetwork removedNetwork;
    private Network dummyNetwork;
    private final LazyOptional<IEnergyStorage> energyProxyCap = LazyOptional.of(() -> getNetwork().getEnergyStorage());

    public ControllerBlockEntity(NetworkType type, BlockPos pos, BlockState state) {
        super(type == NetworkType.CREATIVE ? RSBlockEntities.CREATIVE_CONTROLLER : RSBlockEntities.CONTROLLER, pos, state);

        dataManager.addWatchedParameter(REDSTONE_MODE);
        dataManager.addWatchedParameter(ENERGY_USAGE);
        dataManager.addWatchedParameter(ENERGY_STORED);
        dataManager.addParameter(ENERGY_CAPACITY);
        dataManager.addParameter(NODES);

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

        INetwork network = API.instance().getNetworkManager((ServerLevel) level).getNetwork(worldPosition);

        if (network == null) {
            throw new IllegalStateException("No network present at " + worldPosition);
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
    public void setRemoved() {
        super.setRemoved();

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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (cap == CapabilityEnergy.ENERGY) {
            return energyProxyCap.cast();
        }

        if (cap == NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY) {
            return networkNodeProxyCap.cast();
        }

        return super.getCapability(cap, direction);
    }
}
