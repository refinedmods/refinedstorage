package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeVisitor;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.util.IWrenchable;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.integration.mcmp.IntegrationMCMP;
import com.raoulvdberge.refinedstorage.integration.mcmp.RSMCMPAddon;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import com.raoulvdberge.refinedstorage.tile.config.RedstoneMode;
import com.raoulvdberge.refinedstorage.util.WorldUtils;

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

public abstract class NetworkNode implements INetworkNode, INetworkNodeVisitor, IWrenchable {
    @Nullable
    protected INetwork network;
    protected World world;
    protected BlockPos pos;
    protected int ticks;
    protected RedstoneMode redstoneMode = RedstoneMode.IGNORE;

    private EnumFacing direction;

    private boolean couldUpdate;
    private int ticksSinceUpdateChanged;

    private boolean active;

    public NetworkNode(World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
    }

    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    public void setRedstoneMode(RedstoneMode redstoneMode) {
        this.redstoneMode = redstoneMode;

        markDirty();
    }

    @Nonnull
    @Override
    public ItemStack getItemStack() {
        IBlockState state = world.getBlockState(pos);

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
        if (!world.isRemote) {
            API.instance().getNetworkNodeManager(world).markForSaving();
        }
    }

    @Override
    public boolean canUpdate() {
        return redstoneMode.isEnabled(world, pos);
    }

    protected int getUpdateThrottleInactiveToActive() {
        return 20;
    }

    protected int getUpdateThrottleActiveToInactive() {
        return 4;
    }

    @Override
    public void update() {
        ++ticks;

        boolean canUpdate = getNetwork() != null && canUpdate();

        if (couldUpdate != canUpdate) {
            ++ticksSinceUpdateChanged;

            if (canUpdate ? (ticksSinceUpdateChanged > getUpdateThrottleInactiveToActive()) : (ticksSinceUpdateChanged > getUpdateThrottleActiveToInactive())) {
                ticksSinceUpdateChanged = 0;
                couldUpdate = canUpdate;

                if (hasConnectivityState()) {
                    WorldUtils.updateBlock(world, pos);
                }

                if (network != null) {
                    onConnectedStateChange(network, canUpdate);

                    if (shouldRebuildGraphOnChange()) {
                        network.getNodeGraph().rebuild();
                    }
                }
            }
        } else {
            ticksSinceUpdateChanged = 0;
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
        return pos;
    }

    @Override
    public World getWorld() {
        return world;
    }

    public boolean canConduct(@Nullable EnumFacing direction) {
        return true;
    }

    @Override
    public void visit(Operator operator) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (canConduct(facing)) {
                operator.apply(world, pos.offset(facing), facing.getOpposite());
            }
        }
    }

    public TileEntity getFacingTile() {
        return world.getTileEntity(pos.offset(getDirection()));
    }

    public EnumFacing getDirection() {
        if (direction == null) {
            resetDirection();
        }

        return direction;
    }

    // @todo: Move this data to the network node.
    public void resetDirection() {
        this.direction = ((TileBase) (IntegrationMCMP.isLoaded() ? RSMCMPAddon.unwrapTile(world, pos) : world.getTileEntity(pos))).getDirection();
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

    @Override
    public boolean equals(Object o) {
        return API.instance().isNetworkNodeEqual(this, o);
    }

    @Override
    public int hashCode() {
        return API.instance().getNetworkNodeHashCode(this);
    }
}
