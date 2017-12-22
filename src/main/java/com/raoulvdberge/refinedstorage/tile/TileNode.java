package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeManager;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.api.util.IWrenchable;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNode;
import com.raoulvdberge.refinedstorage.capability.CapabilityNetworkNodeProxy;
import com.raoulvdberge.refinedstorage.tile.config.IRedstoneConfigurable;
import com.raoulvdberge.refinedstorage.tile.config.RedstoneMode;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class TileNode<N extends NetworkNode> extends TileBase implements INetworkNodeProxy<N>, IRedstoneConfigurable, IWrenchable {
    public static final TileDataParameter<Integer, TileNode> REDSTONE_MODE = RedstoneMode.createParameter();

    protected static final String NBT_ACTIVE = "Active";

    private N clientNode;

    public TileNode() {
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
    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        return getNode().writeConfiguration(tag);
    }

    @Override
    public void setDirection(EnumFacing direction) {
        super.setDirection(direction);

        getNode().resetDirection();
    }

    @Override
    public void readConfiguration(NBTTagCompound tag) {
        getNode().readConfiguration(tag);
        getNode().markDirty();
    }

    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        tag.setBoolean(NBT_ACTIVE, getNode().getNetwork() != null && getNode().canUpdate());

        return tag;
    }

    public void readUpdate(NBTTagCompound tag) {
        super.readUpdate(tag);

        getNode().setActive(tag.getBoolean(NBT_ACTIVE));
    }

    @Override
    @Nullable
    public IItemHandler getDrops() {
        return getNode().getDrops();
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

        INetworkNodeManager manager = API.instance().getNetworkNodeManager(world);

        INetworkNode node = manager.getNode(pos);

        if (node == null || !node.getId().equals(getNodeId())) {
            manager.setNode(pos, node = createNode(world, pos));
            manager.markForSaving();
        }

        return (N) node;
    }

    public abstract N createNode(World world, BlockPos pos);

    public abstract String getNodeId();

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing side) {
        if (capability == CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY) {
            return true;
        }

        return super.hasCapability(capability, side);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing side) {
        if (capability == CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY) {
            return CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY.cast(this);
        }

        return super.getCapability(capability, side);
    }
}
