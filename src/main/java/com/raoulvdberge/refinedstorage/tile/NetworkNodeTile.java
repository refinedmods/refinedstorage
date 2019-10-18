package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeManager;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNode;
import com.raoulvdberge.refinedstorage.capability.NetworkNodeProxyCapability;
import com.raoulvdberge.refinedstorage.tile.config.IRedstoneConfigurable;
import com.raoulvdberge.refinedstorage.tile.config.RedstoneMode;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class NetworkNodeTile<N extends NetworkNode> extends BaseTile implements INetworkNodeProxy<N>, IRedstoneConfigurable {
    public static final TileDataParameter<Integer, NetworkNodeTile> REDSTONE_MODE = RedstoneMode.createParameter();

    private N clientNode;

    private LazyOptional<INetworkNodeProxy<N>> networkNodeProxy = LazyOptional.of(() -> this);

    private N removedNode;

    public NetworkNodeTile(TileEntityType<?> tileType) {
        super(tileType);

        dataManager.addWatchedParameter(REDSTONE_MODE);
    }

    @Override
    public RedstoneMode getRedstoneMode() {
        return getNode().getRedstoneMode();
    }

    @Override
    public void setRedstoneMode(RedstoneMode mode) {
        getNode().setRedstoneMode(mode);
    }

    @Override
    @Nonnull
    @SuppressWarnings("unchecked")
    public N getNode() {
        if (world.isRemote) {
            if (clientNode == null) {
                clientNode = createNode(world, pos);
            }

            return clientNode;
        }

        INetworkNodeManager manager = API.instance().getNetworkNodeManager((ServerWorld) world);

        INetworkNode node = manager.getNode(pos);

        if (node == null) {
            throw new RuntimeException("No network node present at " + pos.toString() + ", consider removing the block at this position");
        }

        return (N) node;
    }

    @Override
    public void validate() {
        super.validate();

        if (!world.isRemote) {
            INetworkNodeManager manager = API.instance().getNetworkNodeManager((ServerWorld) world);

            if (manager.getNode(pos) == null) {
                manager.setNode(pos, createNode(world, pos));
                manager.markForSaving();
            }
        }
    }

    @Override
    public void remove() {
        super.remove();

        if (!world.isRemote) {
            INetworkNodeManager manager = API.instance().getNetworkNodeManager((ServerWorld) world);

            INetworkNode node = manager.getNode(pos);

            if (node != null) {
                removedNode = (N) node;
            }

            manager.removeNode(pos);
            manager.markForSaving();

            if (node != null && node.getNetwork() != null) {
                node.getNetwork().getNodeGraph().invalidate(Action.PERFORM, node.getNetwork().world(), node.getNetwork().getPosition());
            }
        }
    }

    public N getRemovedNode() {
        return removedNode;
    }

    public abstract N createNode(World world, BlockPos pos);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (cap == NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY) {
            return networkNodeProxy.cast();
        }

        return super.getCapability(cap, direction);
    }
}
