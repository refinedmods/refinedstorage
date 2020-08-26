package com.refinedmods.refinedstorage.apiimpl.network.node

import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.network.INetworkNodeVisitor
import com.refinedmods.refinedstorage.api.network.node.INetworkNode
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.apiimpl.network.nodeimport.ConnectivityStateChangeCause
import com.refinedmods.refinedstorage.block.BaseBlock
import com.refinedmods.refinedstorage.block.BlockDirection
import com.refinedmods.refinedstorage.block.NetworkNodeBlock
import com.refinedmods.refinedstorage.tile.config.RedstoneMode
import net.minecraft.block.entity.BlockEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import java.util.*

abstract class NetworkNode(

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
        @JvmField override val world: World,
        @JvmField override val pos: BlockPos
) : INetworkNode, INetworkNodeVisitor {
    companion object {
        private const val NBT_OWNER = "Owner"
        private const val NBT_VERSION = "Version"
        private const val VERSION = 1
    }

    override var network: INetwork? = null
    
    @JvmField
    protected var ticks = 0
    var redstoneMode = RedstoneMode.IGNORE
        set(redstoneMode: RedstoneMode) {
            field = redstoneMode
            markDirty()
        }
    private var redstonePowered = false

    override var owner: UUID? = null
        set(value) {
            field = value
            markDirty()
        }

    private var version: String? = null
    private var blockDirection: BlockDirection? = null

    // Disable throttling for the first tick.
    // This is to make sure couldUpdate is going to be correctly set.
    // If we place 2 blocks next to each other, and disconnect the first one really fast,
    // the second one would not realize it has been disconnected because couldUpdate == canUpdate.
    // It would however still have the connected state, due to the initial block update packet.
    // The couldUpdate/canUpdate system is separate from that.
    private var throttlingDisabled = true
    private var couldUpdate = false
    private var ticksSinceUpdateChanged = 0

    override val itemStack: ItemStack
        get() {
            return ItemStack(
                    Item.fromBlock(world.getBlockState(pos).block),
                    1
            )
        }

    override fun onConnected(network: INetwork?) {
        onConnectedStateChange(network, true, ConnectivityStateChangeCause.GRAPH_CHANGE)
        this.network = network
    }

    override fun onDisconnected(network: INetwork?) {
        this.network = null
        onConnectedStateChange(network, false, ConnectivityStateChangeCause.GRAPH_CHANGE)
    }

    protected open fun onConnectedStateChange(network: INetwork?, state: Boolean, cause: ConnectivityStateChangeCause?) {
        // NO OP
    }

    override fun markDirty() {
        if (!world.isClient) {
            instance().getNetworkNodeManager(world as ServerWorld).markForSaving()
        }
    }

    override val isActive: Boolean
        get() {
            return redstoneMode.isEnabled(redstonePowered)
        }

    protected fun canUpdate(): Boolean {
        return if (isActive && network != null) {
            network!!.canRun()
        } else false
    }

    protected open val updateThrottleInactiveToActive: Int
        get() = 20
    protected open val updateThrottleActiveToInactive: Int
        get() = 4

    fun setRedstonePowered(redstonePowered: Boolean) {
        this.redstonePowered = redstonePowered
    }

    override fun update() {
        if (ticks == 0) {
            redstonePowered = world.isReceivingRedstonePower(pos)
        }
        ++ticks
        val canUpdate = canUpdate()
        if (couldUpdate != canUpdate) {
            ++ticksSinceUpdateChanged
            if ((if (canUpdate) ticksSinceUpdateChanged > updateThrottleInactiveToActive else ticksSinceUpdateChanged > updateThrottleActiveToInactive) || throttlingDisabled) {
                ticksSinceUpdateChanged = 0
                couldUpdate = canUpdate
                throttlingDisabled = false
                val blockState = world.getBlockState(pos)
                if (blockState.block is NetworkNodeBlock && (blockState.block as NetworkNodeBlock).hasConnectedState()) {
                    world.setBlockState(pos, world.getBlockState(pos).with(NetworkNodeBlock.CONNECTED, canUpdate))
                }
                if (network != null) {
                    onConnectedStateChange(network, canUpdate, ConnectivityStateChangeCause.REDSTONE_MODE_OR_NETWORK_ENERGY_CHANGE)
                    if (shouldRebuildGraphOnChange()) {
                        network!!.nodeGraph!!.invalidate(Action.PERFORM, network!!.world, network!!.position)
                    }
                }
            }
        } else {
            ticksSinceUpdateChanged = 0
        }
    }

    override fun write(tag: CompoundTag): CompoundTag {
        if (owner != null) {
            tag.putUuid(NBT_OWNER, owner)
        }
        tag.putInt(NBT_VERSION, VERSION)
        writeConfiguration(tag)
        return tag
    }

    open fun writeConfiguration(tag: CompoundTag): CompoundTag {
        redstoneMode.write(tag)
        return tag
    }

    open fun read(tag: CompoundTag) {
        if (tag.containsUuid(NBT_OWNER)) {
            owner = tag.getUuid(NBT_OWNER)
        }
        if (tag.contains(NBT_VERSION)) {
            version = tag.getString(NBT_VERSION)
        }
        readConfiguration(tag)
    }

    open fun readConfiguration(tag: CompoundTag) {
        redstoneMode = RedstoneMode.read(tag)
    }


    fun canConduct(direction: Direction): Boolean {
        return true
    }

    override fun visit(operator: INetworkNodeVisitor.Operator?) {
        operator?.let {
            for (facing in Direction.values()) {
                if (canConduct(facing)) {
                    it.apply(world, pos.offset(facing), facing.getOpposite())
                }
            }
        }

    }

    open val facingTile: BlockEntity?
        get() = world.getBlockEntity(pos.offset(direction))
    
    var direction: Direction? = null
        get() {
            if (field == null) {
                val state = world.getBlockState(pos)
                if (state.block is BaseBlock) {
                    direction = state.get((state.block as BaseBlock).direction.property)
                }
            }
            return field
        }

    fun onBlockDirectionChanged(direction: BlockDirection?) {
        this.blockDirection = direction
    }

    fun onDirectionChanged(direction: Direction?) {
        this.direction = direction
    }
    
    open val drops: Inventory?
        get() = null

    open fun shouldRebuildGraphOnChange(): Boolean {
        return false
    }

    override fun equals(other: Any?): Boolean {
        return instance().isNetworkNodeEqual(this, other)
    }

    override fun hashCode(): Int {
        return instance().getNetworkNodeHashCode(this)
    }
}