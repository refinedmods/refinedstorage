package com.refinedmods.refinedstorage.apiimpl.network

import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.network.INetworkManager
import com.refinedmods.refinedstorage.api.network.NetworkType
import com.refinedmods.refinedstorage.extensions.LIST_TAG_TYPE
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.apache.logging.log4j.LogManager
import java.util.concurrent.ConcurrentHashMap

class NetworkManager(name: String?, world: World):
//        WorldSavedData(name), // TODO Figure out save location
        INetworkManager
{
    private val world: World
    private val logger = LogManager.getLogger(javaClass)
    private val networks: ConcurrentHashMap<BlockPos, INetwork> = ConcurrentHashMap<BlockPos, INetwork>()
    fun read(tag: CompoundTag) {
        if (tag.contains(NBT_NETWORKS)) {
            val networksTag: ListTag = tag.getList(NBT_NETWORKS, LIST_TAG_TYPE)
            networks.clear()
            for (i in networksTag.indices) {
                val networkTag: CompoundTag = networksTag.getCompound(i)
                val data: CompoundTag = networkTag.getCompound(NBT_DATA)
                val pos: BlockPos = BlockPos.fromLong(networkTag.getLong(NBT_POS))
                val type: Int = networkTag.getInt(NBT_TYPE)
                var network: INetwork = Network(world, pos, NetworkType.values()[type])
                try {
                    network = network.readFromNbt(data)
                } catch (t: Throwable) {
                    logger.error("Error while reading network", t)
                }
                networks.put(pos, network)
            }
        }
    }

    fun write(tag: CompoundTag): CompoundTag {
        val list = ListTag()
        for (network in all()) {
            try {
                val networkTag = CompoundTag()
                networkTag.putLong(NBT_POS, network.position.asLong())
                networkTag.put(NBT_DATA, network.writeToNbt(CompoundTag()))
                networkTag.putInt(NBT_TYPE, network.type.ordinal)
                list.add(networkTag)
            } catch (t: Throwable) {
                logger.error("Error while saving network", t)
            }
        }
        tag.put(NBT_NETWORKS, list)
        return tag
    }

    override fun getNetwork(pos: BlockPos): INetwork? {
        return networks[pos]
    }

    override fun removeNetwork(pos: BlockPos) {
        networks.remove(pos)
    }

    override fun setNetwork(pos: BlockPos, network: INetwork) {
        networks[pos] = network
    }

    override fun all(): Collection<INetwork> {
        return networks.values
    }

    override fun markForSaving() {
//        markDirty() // TODO mark dirty
    }

    companion object {
        const val NAME = "refinedstorage_networks"
        private const val NBT_NETWORKS = "Networks"
        private const val NBT_TYPE = "Type"
        private const val NBT_DATA = "Data"
        private const val NBT_POS = "Pos"
    }

    init {
        this.world = world
    }
}