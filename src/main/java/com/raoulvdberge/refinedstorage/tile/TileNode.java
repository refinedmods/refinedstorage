package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeManager;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.api.util.IWrenchable;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.INetworkNodeHolder;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNode;
import com.raoulvdberge.refinedstorage.proxy.CapabilityNetworkNodeProxy;
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

public abstract class TileNode<N extends NetworkNode> extends TileBase implements INetworkNodeProxy<N>, INetworkNodeHolder, IRedstoneConfigurable, IWrenchable {
    public static final TileDataParameter<Integer> REDSTONE_MODE = RedstoneMode.createParameter();

    private NBTTagCompound legacyTagToRead;
    private static final String NBT_ACTIVE = "Active";

    public TileNode() {
        dataManager.addWatchedParameter(REDSTONE_MODE);
    }

    @Override
    public void update() {
        if (!getWorld().isRemote) {
            if (legacyTagToRead != null) {
                getNode().read(legacyTagToRead);
                getNode().markDirty();

                legacyTagToRead = null;
            }

            getNode().update();
        }

        super.update();
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (getNode().getHolder().world() == null) {
            getNode().setHolder(this);
        }
    }

    @Override
    public World world() {
        return getWorld();
    }

    @Override
    public BlockPos pos() {
        return pos;
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
    public void readConfiguration(NBTTagCompound tag) {
        getNode().readConfiguration(tag);
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        // When we have more than the direction stored, this is a legacy tag
        if (tag.getSize() > 1) {
            legacyTagToRead = tag;
        }
    }

    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        tag.setBoolean(NBT_ACTIVE, getNode().getNetwork() != null && getNode().canUpdate());

        return tag;
    }

    public void readUpdate(NBTTagCompound tag) {
        getNode().setActive(tag.getBoolean(NBT_ACTIVE));

        super.readUpdate(tag);
    }

    public IItemHandler getDrops() {
        return getNode().getDrops();
    }

    @Override
    @Nonnull
    @SuppressWarnings("unchecked")
    public N getNode() {
        INetworkNodeManager manager = API.instance().getNetworkNodeManager(getWorld().provider.getDimension());

        INetworkNode node = manager.getNode(pos);

        if (node == null) {
            manager.setNode(pos, node = createNode());
        }

        return (N) node;
    }

    public abstract N createNode();

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side) {
        if (capability == CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY) {
            return true;
        }

        return super.hasCapability(capability, side);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side) {
        if (capability == CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY) {
            return CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY.cast(this);
        }

        return super.getCapability(capability, side);
    }
}
