package com.refinedmods.refinedstorage.apiimpl.network.node;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.INetworkNodeVisitor;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.block.BaseBlock;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import com.refinedmods.refinedstorage.tile.config.RedstoneMode;
import com.refinedmods.refinedstorage.util.NetworkUtils;
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
    private static final int CURRENT_VERSION = 1;

    @Nullable
    protected INetwork network;
    // @Volatile: Mental note. At this moment world instances are retained in Minecraft (since 1.16).
    // This means that during the entire server lifetime, all worlds are present and will not change their instance.
    // However, due to the memory footprint of worlds and modded minecraft having the tendency to have lots of worlds,
    // Forge is planning to unload (aka remove) worlds so their instances will change.
    // This is problematic as this attribute will target the wrong world in that case.
    // Idea: possibly change to a getter based on RegistryKey<World>?
    // Another note: this attribute isn't the *real* problem. Because network nodes are in WorldSavedData in a tick handler,
    // new instances of network nodes will be created when the world refreshes (causing this field to be different too).
    // However, network nodes in the network graph *AREN'T* recreated when the world refreshes, causing the graph to have the incorrect instance, and even worse,
    // having multiple different instances of the same network node.
    protected World world;
    protected BlockPos pos;
    protected int ticks;
    protected RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    private boolean redstonePowered = false;
    @Nullable
    protected UUID owner;
    protected String version;

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

    protected NetworkNode(World world, BlockPos pos) {
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
        if (!world.isRemote) {
            API.instance().getNetworkNodeManager((ServerWorld) world).markForSaving();
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
            redstonePowered = world.isBlockPowered(pos);
        }

        ++ticks;

        boolean canUpdate = canUpdate();

        if (couldUpdate != canUpdate) {
            ++ticksSinceUpdateChanged;

            if ((canUpdate ? (ticksSinceUpdateChanged > getUpdateThrottleInactiveToActive()) : (ticksSinceUpdateChanged > getUpdateThrottleActiveToInactive())) || throttlingDisabled) {
                ticksSinceUpdateChanged = 0;
                couldUpdate = canUpdate;
                throttlingDisabled = false;

                BlockState blockState = world.getBlockState(pos);

                if (blockState.getBlock() instanceof NetworkNodeBlock && ((NetworkNodeBlock) blockState.getBlock()).hasConnectedState()) {
                    world.setBlockState(pos, world.getBlockState(pos).with(NetworkNodeBlock.CONNECTED, canUpdate));
                }

                if (network != null) {
                    onConnectedStateChange(network, canUpdate, ConnectivityStateChangeCause.REDSTONE_MODE_OR_NETWORK_ENERGY_CHANGE);

                    if (shouldRebuildGraphOnChange()) {
                        network.getNodeGraph().invalidate(Action.PERFORM, network.getWorld(), network.getPosition());
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

        tag.putInt(NBT_VERSION, CURRENT_VERSION);

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


    @Override
    public boolean canConduct(Direction direction) {
        return true;
    }

    @Override
    public void visit(Operator operator) {
        for (Direction facing : Direction.values()) {
            INetworkNode oppositeNode = NetworkUtils.getNodeFromTile(world.getTileEntity(pos.offset(facing)));
            if (oppositeNode == null) {
                continue;
            }
            if (canConduct(facing) && oppositeNode.canConduct(facing.getOpposite())) {
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
    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;

        markDirty();
    }

    @Override
    @Nullable
    public UUID getOwner() {
        return owner;
    }
}
