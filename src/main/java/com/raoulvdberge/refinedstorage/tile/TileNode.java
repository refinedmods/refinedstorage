package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import com.raoulvdberge.refinedstorage.api.util.IWrenchable;
import com.raoulvdberge.refinedstorage.proxy.CapabilityNetworkNode;
import com.raoulvdberge.refinedstorage.tile.config.IRedstoneConfigurable;
import com.raoulvdberge.refinedstorage.tile.config.RedstoneMode;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class TileNode extends TileBase implements INetworkNode, IRedstoneConfigurable, IWrenchable {
    public static final TileDataParameter<Integer> REDSTONE_MODE = RedstoneMode.createParameter();

    private static final String NBT_CONNECTED = "Connected";
    private static final String NBT_NETWORK = "Network";

    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    private boolean active;
    private boolean update;

    private BlockPos networkPos;

    protected boolean connected;
    protected INetworkMaster network;

    protected boolean rebuildOnUpdateChange;

    public TileNode() {
        dataManager.addWatchedParameter(REDSTONE_MODE);
    }

    @Override
    public boolean canUpdate() {
        return redstoneMode.isEnabled(getWorld(), pos);
    }

    public boolean isActive() {
        return isConnected() && canUpdate();
    }

    public abstract void updateNode();

    @Override
    public void update() {
        if (!getWorld().isRemote) {
            if (networkPos != null) {
                TileEntity tile = getWorld().getTileEntity(networkPos);

                if (tile instanceof INetworkMaster) {
                    ((INetworkMaster) tile).getNodeGraph().replace(this);

                    onConnected((INetworkMaster) tile);
                }

                networkPos = null;
            }

            if (update != canUpdate() && network != null) {
                update = canUpdate();

                onConnectionChange(network, update);

                if (rebuildOnUpdateChange) {
                    network.getNodeGraph().rebuild();
                }
            }

            if (active != isActive() && hasConnectivityState()) {
                updateBlock();

                active = isActive();
            }

            if (isActive()) {
                updateNode();
            }
        }

        super.update();
    }

    @Override
    public void onConnected(INetworkMaster network) {
        this.connected = true;
        this.network = network;

        onConnectionChange(network, true);

        markDirty();
    }

    @Override
    public void onDisconnected(INetworkMaster network) {
        onConnectionChange(network, false);

        this.connected = false;
        this.network = null;

        markDirty();
    }

    public void onConnectionChange(INetworkMaster network, boolean state) {
        // NO OP
    }

    public boolean canConduct(@Nullable EnumFacing direction) {
        return true;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side) {
        if (capability == CapabilityNetworkNode.NETWORK_NODE_CAPABILITY && canConduct(side)) {
            return true;
        }
        return super.hasCapability(capability, side);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side) {
        if (capability == CapabilityNetworkNode.NETWORK_NODE_CAPABILITY && canConduct(side)) {
            return CapabilityNetworkNode.NETWORK_NODE_CAPABILITY.cast(this);
        }
        return super.getCapability(capability, side);
    }

    @Override
    public INetworkMaster getNetwork() {
        return network;
    }

    @Override
    public BlockPos getPosition() {
        return this.getPos();
    }

    @Override
    public World getNodeWorld() {
        return getWorld();
    }

    @Nonnull
    @Override
    public ItemStack getItemStack() {
        IBlockState state = getWorld().getBlockState(pos);
        Item item = Item.getItemFromBlock(state.getBlock());
        return new ItemStack(item, 1, state.getBlock().getMetaFromState(state));
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    @Override
    public void setRedstoneMode(RedstoneMode mode) {
        this.redstoneMode = mode;

        markDirty();
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        readConfiguration(tag);

        if (tag.hasKey(NBT_NETWORK)) {
            networkPos = BlockPos.fromLong(tag.getLong(NBT_NETWORK));
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        writeConfiguration(tag);

        if (network != null) {
            tag.setLong(NBT_NETWORK, network.getPosition().toLong());
        }

        return tag;
    }

    @Override
    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        redstoneMode.write(tag);

        return tag;
    }

    @Override
    public void readConfiguration(NBTTagCompound tag) {
        redstoneMode = RedstoneMode.read(tag);
    }

    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        if (hasConnectivityState()) {
            tag.setBoolean(NBT_CONNECTED, isActive());
        }

        return tag;
    }

    public void readUpdate(NBTTagCompound tag) {
        if (hasConnectivityState()) {
            connected = tag.getBoolean(NBT_CONNECTED);
        }

        super.readUpdate(tag);
    }

    public boolean hasConnectivityState() {
        return false;
    }
}
