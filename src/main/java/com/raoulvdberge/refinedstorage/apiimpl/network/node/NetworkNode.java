package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeVisitor;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.util.OneSixMigrationHelper;
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
import java.util.UUID;

// TODO: getId: return a ResourceLocation.
public abstract class NetworkNode implements INetworkNode, INetworkNodeVisitor {
    private static final String NBT_OWNER = "Owner";
    private static final String NBT_DIRECTION = "Direction";
    private static final String NBT_VERSION = "Version";

    @Nullable
    protected INetwork network;
    protected World world;
    protected BlockPos pos;
    protected int ticks;
    protected RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    @Nullable
    protected UUID owner;
    protected String version;

    private EnumFacing direction = EnumFacing.NORTH;

    // Disable throttling for the first tick.
    // This is to make sure couldUpdate is going to be correctly set.
    // If we place 2 blocks next to each other, and disconnect the first one really fast,
    // the second one would not realize it has been disconnected because couldUpdate == canUpdate.
    // It would however still have the connected state, due to the initial block update packet.
    // The couldUpdate/canUpdate system is separate from that.
    private boolean throttlingDisabled = true;
    private boolean couldUpdate;
    private int ticksSinceUpdateChanged;

    private boolean active;

    public NetworkNode(World world, BlockPos pos) {
        if (world == null) {
            throw new IllegalArgumentException("World cannot be null");
        }

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
        if (redstoneMode.isEnabled(world, pos)) {
            if (network != null) {
                return network.canRun();
            }
        }

        return false;
    }

    protected int getUpdateThrottleInactiveToActive() {
        return 20;
    }

    protected int getUpdateThrottleActiveToInactive() {
        return 4;
    }

    public void setThrottlingDisabled() {
        throttlingDisabled = true;
    }

    @Override
    public void update() {
        ++ticks;

        boolean canUpdate = canUpdate();

        if (couldUpdate != canUpdate) {
            ++ticksSinceUpdateChanged;

            if ((canUpdate ? (ticksSinceUpdateChanged > getUpdateThrottleInactiveToActive()) : (ticksSinceUpdateChanged > getUpdateThrottleActiveToInactive())) || throttlingDisabled) {
                ticksSinceUpdateChanged = 0;
                couldUpdate = canUpdate;
                throttlingDisabled = false;

                if (hasConnectivityState()) {
                    WorldUtils.updateBlock(world, pos);
                }

                if (network != null) {
                    onConnectedStateChange(network, canUpdate);

                    if (shouldRebuildGraphOnChange()) {
                        network.getNodeGraph().invalidate(Action.PERFORM, network.world(), network.getPosition());
                    }
                }
            }
        } else {
            ticksSinceUpdateChanged = 0;
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        if (owner != null) {
            tag.setUniqueId(NBT_OWNER, owner);
        }

        tag.setString(NBT_VERSION, RS.VERSION);

        tag.setInteger(NBT_DIRECTION, direction.ordinal());

        writeConfiguration(tag);

        return tag;
    }

    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        redstoneMode.write(tag);

        return tag;
    }

    public void read(NBTTagCompound tag) {
        if (tag.hasUniqueId(NBT_OWNER)) {
            owner = tag.getUniqueId(NBT_OWNER);
        }

        if (tag.hasKey(NBT_DIRECTION)) {
            direction = EnumFacing.byIndex(tag.getInteger(NBT_DIRECTION));
        }

        if (tag.hasKey(NBT_VERSION)) {
            version = tag.getString(NBT_VERSION);
        }

        readConfiguration(tag);

        // We do this after readConfiguration so the 1.6 migration calls see that version is null.
        OneSixMigrationHelper.removalHook();
        if (version == null) {
            version = RS.VERSION;
        }
    }

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

    @Nullable
    public TileEntity getFacingTile() {
        return world.getTileEntity(pos.offset(getDirection()));
    }

    public EnumFacing getDirection() {
        return direction;
    }

    public void setDirection(EnumFacing direction) {
        this.direction = direction;

        onDirectionChanged();

        markDirty();
    }

    protected void onDirectionChanged() {
        // NO OP
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

    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;

        markDirty();
    }

    @Nullable
    public UUID getOwner() {
        return owner;
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
