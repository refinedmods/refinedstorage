package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeVisitor;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.block.BaseBlock;
import com.raoulvdberge.refinedstorage.block.NodeBlock;
import com.raoulvdberge.refinedstorage.tile.config.RedstoneMode;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public abstract class NetworkNode implements INetworkNode, INetworkNodeVisitor {
    private static final String NBT_OWNER = "Owner";
    private static final String NBT_VERSION = "Version";
    private static final int VERSION = 1;

    @Nullable
    protected INetwork network;
    protected World world;
    protected BlockPos pos;
    protected int ticks;
    protected RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    @Nullable
    protected UUID owner;
    protected String version;

    private Direction direction = Direction.NORTH;

    // Disable throttling for the first tick.
    // This is to make sure couldUpdate is going to be correctly set.
    // If we place 2 blocks next to each other, and disconnect the first one really fast,
    // the second one would not realize it has been disconnected because couldUpdate == canUpdate.
    // It would however still have the connected state, due to the initial block update packet.
    // The couldUpdate/canUpdate system is separate from that.
    private boolean throttlingDisabled = true;
    private boolean couldUpdate;
    private int ticksSinceUpdateChanged;

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
        return new ItemStack(Item.BLOCK_TO_ITEM.get(world.getBlockState(pos).getBlock()), 1);
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
            API.instance().getNetworkNodeManager((ServerWorld) world).markForSaving();
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

    // @TODO: DELETE.
    public boolean hasConnectivityState() {
        return true;
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

                BlockState blockState = world.getBlockState(pos);

                if (blockState.getBlock() instanceof NodeBlock && ((NodeBlock) blockState.getBlock()).hasConnectedState()) {
                    world.setBlockState(pos, world.getBlockState(pos).with(NodeBlock.CONNECTED, canUpdate));
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
    public CompoundNBT write(CompoundNBT tag) {
        if (owner != null) {
            tag.putUniqueId(NBT_OWNER, owner);
        }

        tag.putInt(NBT_VERSION, VERSION);

        writeConfiguration(tag);

        return tag;
    }

    public CompoundNBT writeConfiguration(CompoundNBT tag) {
        redstoneMode.write(tag);

        return tag;
    }

    public void read(CompoundNBT tag) {
        if (tag.hasUniqueId(NBT_OWNER)) {
            owner = tag.getUniqueId(NBT_OWNER);
        }

        if (tag.contains(NBT_VERSION)) {
            version = tag.getString(NBT_VERSION);
        }

        readConfiguration(tag);
    }

    public void readConfiguration(CompoundNBT tag) {
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

    public boolean canConduct(@Nullable Direction direction) {
        return true;
    }

    @Override
    public void visit(Operator operator) {
        for (Direction facing : Direction.values()) {
            if (canConduct(facing)) {
                operator.apply(world, pos.offset(facing), facing.getOpposite());
            }
        }
    }

    @Nullable
    public TileEntity getFacingTile() {
        return world.getTileEntity(pos.offset(getDirection()));
    }

    public Direction getDirection() {
        if (direction == null) {
            BlockState state = world.getBlockState(pos);

            if (state.getBlock() instanceof BaseBlock) {
                direction = state.get(((BaseBlock) state.getBlock()).getDirection().getProperty());
            }
        }

        return direction;
    }

    @Nullable
    public IItemHandler getDrops() {
        return null;
    }

    public boolean shouldRebuildGraphOnChange() {
        return false;
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
