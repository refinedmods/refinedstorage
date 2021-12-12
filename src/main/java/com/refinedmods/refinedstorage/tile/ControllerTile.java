package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.INetworkManager;
import com.refinedmods.refinedstorage.api.network.INetworkNodeGraphEntry;
import com.refinedmods.refinedstorage.api.network.NetworkType;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.Network;
import com.refinedmods.refinedstorage.apiimpl.network.node.RootNetworkNode;
import com.refinedmods.refinedstorage.block.ControllerBlock;
import com.refinedmods.refinedstorage.capability.NetworkNodeProxyCapability;
import com.refinedmods.refinedstorage.tile.config.IRedstoneConfigurable;
import com.refinedmods.refinedstorage.tile.config.RedstoneMode;
import com.refinedmods.refinedstorage.tile.data.RSSerializers;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.Direction;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ControllerTile extends BaseTile implements INetworkNodeProxy<RootNetworkNode>, IRedstoneConfigurable {
    public static final TileDataParameter<Integer, ControllerTile> REDSTONE_MODE = RedstoneMode.createParameter();
    public static final TileDataParameter<Integer, ControllerTile> ENERGY_USAGE = new TileDataParameter<>(DataSerializers.INT, 0, t -> t.getNetwork().getEnergyUsage());
    public static final TileDataParameter<Integer, ControllerTile> ENERGY_STORED = new TileDataParameter<>(DataSerializers.INT, 0, t -> t.getNetwork().getEnergyStorage().getEnergyStored());
    public static final TileDataParameter<Integer, ControllerTile> ENERGY_CAPACITY = new TileDataParameter<>(DataSerializers.INT, 0, t -> t.getNetwork().getEnergyStorage().getMaxEnergyStored());
    public static final TileDataParameter<List<ClientNode>, ControllerTile> NODES = new TileDataParameter<>(RSSerializers.CLIENT_NODE_SERIALIZER, new ArrayList<>(), ControllerTile::collectClientNodes);

    private static final String NBT_ENERGY_TYPE = "EnergyType";

    private final LazyOptional<IEnergyStorage> energyProxyCap = LazyOptional.of(() -> getNetwork().getEnergyStorage());
    private final LazyOptional<INetworkNodeProxy<RootNetworkNode>> networkNodeProxyCap = LazyOptional.of(() -> this);

    private final NetworkType type;

    private INetwork removedNetwork;

    private Network dummyNetwork;

    public ControllerTile(NetworkType type) {
        super(type == NetworkType.CREATIVE ? RSTiles.CREATIVE_CONTROLLER : RSTiles.CONTROLLER);

        dataManager.addWatchedParameter(REDSTONE_MODE);
        dataManager.addWatchedParameter(ENERGY_USAGE);
        dataManager.addWatchedParameter(ENERGY_STORED);
        dataManager.addParameter(ENERGY_CAPACITY);
        dataManager.addParameter(NODES);

        this.type = type;
    }

    @Override
    public CompoundNBT writeUpdate(CompoundNBT tag) {
        super.writeUpdate(tag);

        tag.putInt(NBT_ENERGY_TYPE, ((Network) getNetwork()).getEnergyType().ordinal());

        return tag;
    }

    @Override
    public void readUpdate(CompoundNBT tag) {
        if (tag.contains(NBT_ENERGY_TYPE)) {
            level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(ControllerBlock.ENERGY_TYPE, ControllerBlock.EnergyType.values()[tag.getInt(NBT_ENERGY_TYPE)]));
        }

        super.readUpdate(tag);
    }

    public INetwork getNetwork() {
        if (level.isClientSide) {
            if (dummyNetwork == null) {
                dummyNetwork = new Network(level, worldPosition, type);
            }

            return dummyNetwork;
        }

        INetwork network = API.instance().getNetworkManager((ServerWorld) level).getNetwork(worldPosition);

        if (network == null) {
            throw new IllegalStateException("No network present at " + worldPosition);
        }

        return network;
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();

        if (!level.isClientSide) {
            INetworkManager manager = API.instance().getNetworkManager((ServerWorld) level);

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
            INetworkManager manager = API.instance().getNetworkManager((ServerWorld) level);

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

    private static List<ClientNode> collectClientNodes(ControllerTile tile) {
        List<ClientNode> nodes = new ArrayList<>();

        for (INetworkNodeGraphEntry entry : tile.getNetwork().getNodeGraph().all()) {
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
}
