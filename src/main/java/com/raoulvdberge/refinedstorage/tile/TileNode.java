package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeManager;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.api.util.IWrenchable;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class TileNode<N extends NetworkNode> extends TileBase implements INetworkNodeProxy<N>, IRedstoneConfigurable, IWrenchable {
    public static final TileDataParameter<Integer> REDSTONE_MODE = RedstoneMode.createParameter();

    private NBTTagCompound legacyTag;

    private N clientNode;

    protected static final String NBT_ACTIVE = "Active";

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
    public void readConfiguration(NBTTagCompound tag) {
        getNode().readConfiguration(tag);
        getNode().markDirty();
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        this.legacyTag = tag;
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

        NetworkNode node = (NetworkNode) manager.getNode(pos);

        // @TODO: This is a hack to support previous broken versions that have no nodes for some tiles due to a bug.
        // This should actually be called in Block#onBlockAdded.
        if (node == null) {
            RSUtils.debugLog("Creating node at " + pos);

            manager.setNode(pos, node = createNode(world, pos));
            manager.markForSaving();
        }

        if (legacyTag != null) {
            doLegacyCheck(node);
        }

        return (N) node;
    }

    private void doLegacyCheck(NetworkNode node) {
        // Ugly code for checking if this is a legacy tile. Sue me.
        boolean hasMeta = legacyTag.hasKey("x") && legacyTag.hasKey("y") && legacyTag.hasKey("z") && legacyTag.hasKey("id");
        boolean hasForgeData = legacyTag.hasKey("ForgeData");
        boolean hasForgeCaps = legacyTag.hasKey("ForgeCaps");

        // + 1 because of "Direction".
        if (legacyTag.getSize() == 4 + 1 && hasMeta) {
            // NO OP
        } else if (legacyTag.getSize() == 5 + 1 && hasMeta && (hasForgeData || hasForgeCaps)) {
            // NO OP
        } else if (legacyTag.getSize() == 6 + 1 && hasMeta && hasForgeData && hasForgeCaps) {
            // NO OP
        } else {
            RSUtils.debugLog("Reading legacy tag data at " + pos + "!");

            node.read(legacyTag);
            node.markDirty();

            markDirty();
        }

        this.legacyTag = null;
    }

    // @TODO: This needs to be redone. Perhaps we need to reuse the node registry for this.
    public abstract N createNode(World world, BlockPos pos);

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
