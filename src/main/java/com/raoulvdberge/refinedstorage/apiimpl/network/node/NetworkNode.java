package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.INetworkNeighborhoodAware;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.util.IWrenchable;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.tile.INetworkNodeContainer;
import com.raoulvdberge.refinedstorage.tile.config.RedstoneMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class NetworkNode implements INetworkNode, INetworkNeighborhoodAware, IWrenchable {
    @Nullable
    protected INetwork network;
    protected INetworkNodeContainer container;
    protected int ticks;
    protected RedstoneMode redstoneMode = RedstoneMode.IGNORE;

    private boolean couldUpdate;
    private boolean active;

    public NetworkNode(INetworkNodeContainer container) {
        this.container = container;
    }

    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    public void setRedstoneMode(RedstoneMode redstoneMode) {
        this.redstoneMode = redstoneMode;

        markDirty();
    }

    @Nullable
    public INetworkNodeContainer getContainer() {
        return container;
    }

    public void setContainer(INetworkNodeContainer container) {
        this.container = container;
    }

    @Nonnull
    @Override
    public ItemStack getItemStack() {
        IBlockState state = container.world().getBlockState(container.pos());

        return new ItemStack(Item.getItemFromBlock(state.getBlock()), 1, state.getBlock().getMetaFromState(state));
    }

    @Override
    public void onConnected(INetwork network) {
        onConnectedStateChange(network, true);

        this.network = network;
    }

    @Override
    public void onDisconnected(INetwork network) {
        this.network = null;

        onConnectedStateChange(network, false);
    }

    protected void onConnectedStateChange(INetwork network, boolean state) {
        // NO OP
    }

    @Override
    public void markDirty() {
        if (container.world() != null && !container.world().isRemote) {
            API.instance().getNetworkNodeManager(container.world()).markForSaving();
        }
    }

    @Override
    public boolean canUpdate() {
        return redstoneMode.isEnabled(container.world(), container.pos());
    }

    @Override
    public void update() {
        ++ticks;

        boolean canUpdate = getNetwork() != null && canUpdate();

        if (couldUpdate != canUpdate) {
            couldUpdate = canUpdate;

            if (hasConnectivityState()) {
                RSUtils.updateBlock(container.world(), container.pos());
            }

            if (network != null) {
                onConnectedStateChange(network, couldUpdate);

                if (shouldRebuildGraphOnChange()) {
                    network.getNodeGraph().rebuild();
                }
            }
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        writeConfiguration(tag);

        return tag;
    }

    @Override
    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        redstoneMode.write(tag);

        return tag;
    }

    public void read(NBTTagCompound tag) {
        readConfiguration(tag);
    }

    @Override
    public void readConfiguration(NBTTagCompound tag) {
        redstoneMode = RedstoneMode.read(tag);
    }

    @Nullable
    @Override
    public INetwork getNetwork() {
        return network;
    }

    @Override
    public BlockPos getPos() {
        return container.pos();
    }

    @Override
    public World getWorld() {
        return container.world();
    }

    public boolean canConduct(@Nullable EnumFacing direction) {
        return true;
    }

    @Override
    public void walkNeighborhood(Operator operator) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (canConduct(facing)) {
                operator.apply(container.world(), container.pos().offset(facing), facing.getOpposite());
            }
        }
    }

    public TileEntity getFacingTile() {
        return container.world().getTileEntity(container.pos().offset(container.getDirection()));
    }

    @Nullable
    public IItemHandler getDrops() {
        return null;
    }

    public boolean shouldRebuildGraphOnChange() {
        return false;
    }

    public boolean hasConnectivityState() {
        return false;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
