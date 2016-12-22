package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeProvider;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.apiimpl.API;
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

import javax.annotation.Nullable;

public abstract class TileNode extends TileBase implements INetworkNodeProxy, INetworkNodeHolder, IRedstoneConfigurable {
    public static final TileDataParameter<Integer> REDSTONE_MODE = RedstoneMode.createParameter();

    private static final String NBT_ACTIVE = "Active";

    public TileNode() {
        dataManager.addWatchedParameter(REDSTONE_MODE);
    }

    @Override
    public void update() {
        super.update();

        if (!getWorld().isRemote) {
            getNode().update();
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();

        if (getWorld() != null) {
            INetworkNode node = getNode();

            API.instance().getNetworkNodeProvider(getWorld().provider.getDimension()).removeNode(pos);

            if (!getWorld().isRemote && node.getNetwork() != null) {
                node.getNetwork().getNodeGraph().rebuild();
            }
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
        return ((NetworkNode) getNode()).getRedstoneMode();
    }

    @Override
    public void setRedstoneMode(RedstoneMode mode) {
        ((NetworkNode) getNode()).setRedstoneMode(mode);
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        getNode().read(tag);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        getNode().write(tag);

        return tag;
    }

    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        tag.setBoolean(NBT_ACTIVE, getNode().getNetwork() != null && getNode().canUpdate());

        return tag;
    }

    public void readUpdate(NBTTagCompound tag) {
        ((NetworkNode) getNode()).setActive(tag.getBoolean(NBT_ACTIVE));

        super.readUpdate(tag);
    }

    public IItemHandler getDrops() {
        return ((NetworkNode) getNode()).getDrops();
    }

    @Override
    public INetworkNode getNode() {
        INetworkNodeProvider provider = API.instance().getNetworkNodeProvider(getWorld().provider.getDimension());

        INetworkNode node = provider.getNode(pos);

        if (node == null) {
            provider.setNode(pos, node = createNode());
        }

        return node;
    }

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
