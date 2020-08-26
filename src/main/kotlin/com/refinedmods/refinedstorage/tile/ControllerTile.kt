package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.RSTiles
import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.network.NetworkType
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.apiimpl.network.Network
import com.refinedmods.refinedstorage.apiimpl.network.node.RootNetworkNode
import com.refinedmods.refinedstorage.block.ControllerBlock
import com.refinedmods.refinedstorage.block.ControllerBlock.EnergyType
import com.refinedmods.refinedstorage.tile.config.IRedstoneConfigurable
import com.refinedmods.refinedstorage.tile.config.RedstoneMode
import com.refinedmods.refinedstorage.tile.config.RedstoneMode.Companion.createParameter
import com.refinedmods.refinedstorage.tile.data.RSSerializers
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.chunk.ChunkManager
import net.minecraft.world.chunk.WorldChunk
import java.util.*
import java.util.function.Function

class ControllerTile(type: NetworkType):
        BaseTile(if (type === NetworkType.CREATIVE) RSTiles.CREATIVE_CONTROLLER else RSTiles.CONTROLLER),
        INetworkNodeProxy<RootNetworkNode>,
        IRedstoneConfigurable
{
    private val energyProxyCap: IEnergyStorage by lazy { network.energyStorage }
    private val networkNodeProxyCap: INetworkNodeProxy<RootNetworkNode> by lazy { this }
    private val type: NetworkType
    var removedNetwork: INetwork? = null
        private set
    private var dummyNetwork: Network? = null
    override fun writeUpdate(tag: CompoundTag): CompoundTag {
        super.writeUpdate(tag)
        tag.putInt(NBT_ENERGY_TYPE, (network as Network).energyType.ordinal)
        return tag
    }

    override fun readUpdate(tag: CompoundTag?) {
        if (tag!!.contains(NBT_ENERGY_TYPE)) {
            world!!.setBlockState(pos, world!!.getBlockState(pos).with(ControllerBlock.ENERGY_TYPE, EnergyType.values()[tag.getInt(NBT_ENERGY_TYPE)]))
        }
        super.readUpdate(tag)
    }

    val network: INetwork
        get() {
            if (world!!.isClient) {
                val net = dummyNetwork ?: Network(world!!, pos, type)
                dummyNetwork = net
                return net
            }
            return instance().getNetworkManager(world as ServerWorld?)!!.getNetwork(pos)
                    ?: throw IllegalStateException("No network present at $pos")
        }

    fun validate() {
        super.validate()
        if (!world!!.isClient) {
            val manager = instance().getNetworkManager(world as ServerWorld?)
            if (manager!!.getNetwork(pos) == null) {
                manager.setNetwork(pos, Network(world, pos, type))
                manager.markForSaving()
            }
        }
    }

    fun remove() {
        super.remove()
        if (!world.isClient) {
            val manager = instance().getNetworkManager(world as ServerWorld?)
            val network = manager!!.getNetwork(pos)
            removedNetwork = network
            manager.removeNetwork(pos)
            manager.markForSaving()
            network!!.onRemoved()
        }
    }

    override val node: RootNetworkNode
        get() = (network as Network).getRoot()
    override var redstoneMode: RedstoneMode
        get() = (network as Network).getRedstoneMode()
        set(mode) {
            (network as Network).setRedstoneMode(mode)
        }

    // TODO Replace capability
//    fun <T> getCapability(@Nonnull cap: Capability<T>, @Nullable direction: Direction?): LazyOptional<T> {
//        if (cap === CapabilityEnergy.ENERGY) {
//            return energyProxyCap.cast()
//        }
//        return if (cap === NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY) {
//            networkNodeProxyCap.cast()
//        } else super.getCapability(cap, direction)
//    }

    companion object {
        val REDSTONE_MODE = createParameter<ControllerTile>()
        val ENERGY_USAGE = TileDataParameter(DataSerializers.VARINT, 0, Function { t: ControllerTile -> t.network.energyUsage })
        val ENERGY_STORED = TileDataParameter(DataSerializers.VARINT, 0, Function { t: ControllerTile -> t.network.energyStorage.getEnergyStored() })
        val ENERGY_CAPACITY = TileDataParameter(DataSerializers.VARINT, 0, Function { t: ControllerTile -> t.network.energyStorage.getMaxEnergyStored() })
        val NODES: TileDataParameter<List<ClientNode>, ControllerTile> = TileDataParameter<T, E>(RSSerializers.CLIENT_NODE_SERIALIZER, ArrayList<Any>(), Function<E, T> { tile: E -> collectClientNodes(tile) })
        private const val NBT_ENERGY_TYPE = "EnergyType"
        private fun collectClientNodes(tile: ControllerTile): List<ClientNode> {
            val nodes: MutableList<ClientNode> = ArrayList()
            for (node in tile.network.nodeGraph!!.all()!!) {
                if (node!!.isActive) {
                    val stack = node.itemStack
                    if (stack.isEmpty) {
                        continue
                    }
                    val clientNode = ClientNode(stack, 1, node.energyUsage)
                    if (nodes.contains(clientNode)) {
                        val other = nodes[nodes.indexOf(clientNode)]
                        other.amount = other.amount + 1
                    } else {
                        nodes.add(clientNode)
                    }
                }
            }

            nodes.sort(java.util.Comparator { a: ClientNode, b: ClientNode -> b.energyUsage.compareTo(a.energyUsage) })
            return nodes
        }
    }

    init {
        dataManager.addWatchedParameter(REDSTONE_MODE)
        dataManager.addWatchedParameter(ENERGY_USAGE)
        dataManager.addWatchedParameter(ENERGY_STORED)
        dataManager.addParameter(ENERGY_CAPACITY)
        dataManager.addParameter(NODES)
        this.type = type
    }
}