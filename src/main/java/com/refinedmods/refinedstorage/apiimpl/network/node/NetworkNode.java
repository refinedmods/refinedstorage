package com.refinedmods.refinedstorage.apiimpl.network.node;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.INetworkNodeVisitor;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.block.BaseBlock;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import com.refinedmods.refinedstorage.blockentity.config.RedstoneMode;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public abstract class NetworkNode implements INetworkNode, INetworkNodeVisitor {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String NBT_OWNER = "Owner";
    private static final String NBT_VERSION = "Version";
    private static final int CURRENT_VERSION = 1;

    @Nullable
    protected INetwork network;
    // @Volatile: Mental note. At this moment world instances are retained in Minecraft (since 1.16).
    // This means that during the entire server lifetime, all worlds are present and will not change their instance.
    // However, due to the memory footprint of worlds and modded minecraft having the tendency to have lots of worlds,
    // Forge is planning to unload (aka remove) worlds so their instances will change.
    // This is problematic as this attribute will target the wrong world in that case.
    // Idea: possibly change to a getter based on RegistryKey<Level>?
    // Another note: this attribute isn't the *real* problem. Because network nodes are in SavedData in a tick handler,
    // new instances of network nodes will be created when the world refreshes (causing this field to be different too).
    // However, network nodes in the network graph *AREN'T* recreated when the world refreshes, causing the graph to have the incorrect instance, and even worse,
    // having multiple different instances of the same network node.
    protected Level level;
    protected BlockPos pos;
    protected int ticks;
    protected RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    @Nullable
    protected UUID owner;
    protected String version;
    private boolean redstonePowered = false;
    private Direction direction;

    // Disable throttling for the first tick.
    // This is to make sure couldUpdate is going to be correctly set.
    // If we place 2 blocks next to each other, and disconnect the first one really fast,
    // the second one would not realize it has been disconnected because couldUpdate == canUpdate.
    // It would however still have the connected state, due to the initial block update packet.
    // The couldUpdate/canUpdate system is separate from that.
    private boolean throttlingDisabled = true;
    private boolean couldUpdate;
    private int ticksSinceUpdateChanged;

    protected NetworkNode(Level level, BlockPos pos) {
        if (level == null) {
            throw new IllegalArgumentException("World cannot be null");
        }

        this.level = level;
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
        return new ItemStack(Item.BY_BLOCK.get(level.getBlockState(pos).getBlock()), 1);
    }

    @Override
    public void onConnected(INetwork network) {
        onConnectedStateChange(network, true, ConnectivityStateChangeCause.GRAPH_CHANGE);

        this.network = network;
    }

    @Override
    public void onDisconnected(INetwork network) {
        this.network = null;

        onConnectedStateChange(network, false, ConnectivityStateChangeCause.GRAPH_CHANGE);
    }

    protected void onConnectedStateChange(INetwork network, boolean state, ConnectivityStateChangeCause cause) {
        // NO OP
    }

    @Override
    public void markDirty() {
        if (!level.isClientSide) {
            API.instance().getNetworkNodeManager((ServerLevel) level).markForSaving();
        }
    }

    @Override
    public boolean isActive() {
        return redstoneMode.isEnabled(redstonePowered);
    }

    protected final boolean canUpdate() {
        if (isActive() && network != null) {
            return network.canRun();
        }

        return false;
    }

    protected int getUpdateThrottleInactiveToActive() {
        return 20;
    }

    protected int getUpdateThrottleActiveToInactive() {
        return 4;
    }

    public void setRedstonePowered(boolean redstonePowered) {
        this.redstonePowered = redstonePowered;
    }

    @Override
    public void update() {
        if (ticks == 0) {
            redstonePowered = level.hasNeighborSignal(pos);
        }

        ++ticks;

        boolean canUpdate = canUpdate();

        if (couldUpdate != canUpdate) {
            ++ticksSinceUpdateChanged;

            if ((canUpdate ? (ticksSinceUpdateChanged > getUpdateThrottleInactiveToActive()) : (ticksSinceUpdateChanged > getUpdateThrottleActiveToInactive())) || throttlingDisabled) {
                ticksSinceUpdateChanged = 0;
                couldUpdate = canUpdate;
                throttlingDisabled = false;

                BlockState blockState = level.getBlockState(pos);

                if (blockState.getBlock() instanceof NetworkNodeBlock && ((NetworkNodeBlock) blockState.getBlock()).hasConnectedState()) {
                    level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(NetworkNodeBlock.CONNECTED, canUpdate));
                }

                if (network != null) {
                    onConnectedStateChange(network, canUpdate, ConnectivityStateChangeCause.REDSTONE_MODE_OR_NETWORK_ENERGY_CHANGE);

                    if (shouldRebuildGraphOnChange()) {
                        network.getNodeGraph().invalidate(Action.PERFORM, network.getLevel(), network.getPosition());
                    }
                }
            }
        } else {
            ticksSinceUpdateChanged = 0;
        }
    }

    @Override
    public CompoundTag write(CompoundTag tag) {
        if (owner != null) {
            tag.putUUID(NBT_OWNER, owner);
        }

        tag.putInt(NBT_VERSION, CURRENT_VERSION);

        writeConfiguration(tag);

        return tag;
    }

    public CompoundTag writeConfiguration(CompoundTag tag) {
        redstoneMode.write(tag);

        return tag;
    }

    public void read(CompoundTag tag) {
        if (tag.hasUUID(NBT_OWNER)) {
            owner = tag.getUUID(NBT_OWNER);
        }

        if (tag.contains(NBT_VERSION)) {
            version = tag.getString(NBT_VERSION);
        }

        readConfiguration(tag);
    }

    public void readConfiguration(CompoundTag tag) {
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
    public Level getLevel() {
        return level;
    }

    @Override
    public boolean canConduct(Direction direction) {
        return true;
    }

    @Override
    public void visit(Operator operator) {
        for (Direction facing : Direction.values()) {
            BlockPos facingPos = pos.relative(facing);
            if (!level.isLoaded(facingPos)) {
                return;
            }
            INetworkNode oppositeNode = NetworkUtils.getNodeFromBlockEntity(level.getBlockEntity(facingPos));
            if (oppositeNode == null) {
                continue;
            }
            if (canConduct(facing) && oppositeNode.canReceive(facing.getOpposite())) {
                operator.apply(level, facingPos, facing.getOpposite());
            }
        }
    }

    @Nullable
    public BlockEntity getFacingBlockEntity() {
        BlockPos facingPos = pos.relative(getDirection());
        if (!level.isLoaded(facingPos)) {
            return null;
        }

        return level.getBlockEntity(facingPos);
    }

    public Direction getDirection() {
        if (direction == null) {
            BlockState state = level.getBlockState(pos);

            if (state.getBlock() instanceof BaseBlock baseBlock) {
                DirectionProperty property = baseBlock.getDirection().getProperty();

                if (state.hasProperty(property)) {
                    direction = state.getValue(property);
                } else {
                    LOGGER.warn("Node @ {} has no direction! Consider recreating the block", pos);
                    return Direction.NORTH;
                }
            }
        }

        return direction;
    }

    public void onDirectionChanged(Direction direction) {
        this.direction = direction;
    }

    @Nullable
    public IItemHandler getDrops() {
        return null;
    }

    public boolean shouldRebuildGraphOnChange() {
        return false;
    }

    @Override
    @Nullable
    public UUID getOwner() {
        return owner;
    }

    @Override
    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;

        markDirty();
    }
}
