package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeManager;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.blockentity.config.IRedstoneConfigurable;
import com.refinedmods.refinedstorage.blockentity.config.RedstoneMode;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.capability.NetworkNodeProxyCapability;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class NetworkNodeBlockEntity<N extends NetworkNode> extends BaseBlockEntity implements INetworkNodeProxy<N>, IRedstoneConfigurable {
    public static final BlockEntitySynchronizationParameter<Integer, NetworkNodeBlockEntity> REDSTONE_MODE = RedstoneMode.createParameter();
    private final LazyOptional<INetworkNodeProxy<N>> networkNodeProxy = LazyOptional.of(() -> this);
    private N clientNode;
    private N removedNode;

    protected NetworkNodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

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
        if (level.isClientSide) {
            if (clientNode == null) {
                clientNode = createNode(level, worldPosition);
            }

            return clientNode;
        }

        INetworkNodeManager manager = API.instance().getNetworkNodeManager((ServerLevel) level);

        INetworkNode node = manager.getNode(worldPosition);

        if (node == null) {
            throw new IllegalStateException("No network node present at " + worldPosition.toString() + ", consider removing the block at this position");
        }

        return (N) node;
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();

        if (!level.isClientSide) {
            initialize();
        }
    }

    private void initialize() {
        INetworkNodeManager manager = API.instance().getNetworkNodeManager((ServerLevel) level);

        if (manager.getNode(worldPosition) == null) {
            initializeNetworkNode(manager);
            discoverNeighboringNetworkNodes();
        }
    }

    private void initializeNetworkNode(INetworkNodeManager manager) {
        manager.setNode(worldPosition, createNode(level, worldPosition));
        manager.markForSaving();
    }

    private void discoverNeighboringNetworkNodes() {
        for (Direction facing : Direction.values()) {
            INetworkNode neighbor = NetworkUtils.getNodeAtPosition(level, worldPosition.relative(facing));
            if (neighbor != null && neighbor.getNetwork() != null) {
                neighbor.getNetwork().getNodeGraph().invalidate(Action.PERFORM, neighbor.getNetwork().getLevel(), neighbor.getNetwork().getPosition());
                return;
            }
        }
    }

    @Override
    public void onRemoved(RemovalReason reason) {
        super.onRemoved(reason);

        if (!level.isClientSide) {
            remove(reason);
        }
    }

    private void remove(RemovalReason reason) {
        INetworkNodeManager manager = API.instance().getNetworkNodeManager((ServerLevel) level);

        INetworkNode node = manager.getNode(worldPosition);

        if (reason == RemovalReason.REMOVED) {
            performRemove(manager, node);
        }

        if (node != null && node.getNetwork() != null) {
            node.getNetwork().getNodeGraph().invalidate(Action.PERFORM, node.getNetwork().getLevel(), node.getNetwork().getPosition());
        }
    }

    private void performRemove(INetworkNodeManager manager, INetworkNode node) {
        if (node != null) {
            removedNode = (N) node;
        }

        manager.removeNode(worldPosition);
        manager.markForSaving();
    }

    public N getRemovedNode() {
        return removedNode;
    }

    public abstract N createNode(Level level, BlockPos pos);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (cap == NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY) {
            return networkNodeProxy.cast();
        }

        return super.getCapability(cap, direction);
    }
}
