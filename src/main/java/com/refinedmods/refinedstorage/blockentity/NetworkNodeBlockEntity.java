package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeManager;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.blockentity.config.IRedstoneConfigurable;
import com.refinedmods.refinedstorage.blockentity.config.RedstoneMode;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import com.refinedmods.refinedstorage.capability.NetworkNodeProxyCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class NetworkNodeBlockEntity<N extends NetworkNode> extends BaseBlockEntity implements INetworkNodeProxy<N>, IRedstoneConfigurable {
    public static final BlockEntitySynchronizationParameter<Integer, NetworkNodeBlockEntity<?>> REDSTONE_MODE = RedstoneMode.createParameter(new ResourceLocation(RS.ID, "redstone_mode"));

    private final LazyOptional<INetworkNodeProxy<N>> networkNodeProxy = LazyOptional.of(() -> this);
    private final Class<N> networkNodeClass;
    private N clientNode;
    private N removedNode;

    private static final Logger LOGGER = LogManager.getLogger();

    // TODO: remove this ctor in 1.21
    @Deprecated
    protected NetworkNodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, BlockEntitySynchronizationSpec syncSpec) {
        super(type, pos, state, syncSpec);
        this.networkNodeClass = null;
    }

    protected NetworkNodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, BlockEntitySynchronizationSpec syncSpec,
                                     Class<N> networkNodeClass) {
        super(type, pos, state, syncSpec);
        this.networkNodeClass = networkNodeClass;
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

        try {
            INetworkNode node = manager.getNode(worldPosition);

            if (node == null) {
                LOGGER.warn("Expected a node @ {} but couldn't find it, creating a new one...", worldPosition);
                node = createAndSetNode(manager);
            }

            return doCast(node);
        } catch (ClassCastException e) {
            LOGGER.warn("Node @ {} got desynced with it's block entity container, recreating", worldPosition, e);
            return (N) createAndSetNode(manager);
        }
    }

    private N doCast(INetworkNode node) {
        if (networkNodeClass == null) {
            return (N) node;
        }
        return networkNodeClass.cast(node);
    }

    private INetworkNode createAndSetNode(INetworkNodeManager manager) {
        INetworkNode node = createNode(level, worldPosition);
        manager.setNode(worldPosition, node);
        manager.markForSaving();
        return node;
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();

        if (!level.isClientSide) {
            INetworkNodeManager manager = API.instance().getNetworkNodeManager((ServerLevel) level);

            if (manager.getNode(worldPosition) == null) {
                manager.setNode(worldPosition, createNode(level, worldPosition));
                manager.markForSaving();
            }
        }
    }

    @Override
    public void onRemovedNotDueToChunkUnload() {
        super.onRemovedNotDueToChunkUnload();

        if (!level.isClientSide) {
            INetworkNodeManager manager = API.instance().getNetworkNodeManager((ServerLevel) level);

            INetworkNode node = manager.getNode(worldPosition);

            if (node != null) {
                try {
                    removedNode = doCast(node);
                } catch (ClassCastException e) {
                    removedNode = null;
                }
            }

            manager.removeNode(worldPosition);
            manager.markForSaving();

            if (node != null && node.getNetwork() != null) {
                node.getNetwork().getNodeGraph().invalidate(Action.PERFORM, node.getNetwork().getLevel(), node.getNetwork().getPosition());
            }
        }
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
