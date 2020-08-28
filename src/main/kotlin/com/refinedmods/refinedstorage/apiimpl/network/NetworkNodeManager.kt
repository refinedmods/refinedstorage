package com.refinedmods.refinedstorage.apiimpl.network

import com.refinedmods.refinedstorage.api.network.node.INetworkNode
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeFactory
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeManager
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.extensions.LIST_TAG_TYPE
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.apache.logging.log4j.LogManager
import java.util.concurrent.ConcurrentHashMap

class NetworkNodeManager(
        name: String?,
        private val world: World
):
//        WorldSavedData(name),  // TODO Figure out where this is now...
        INetworkNodeManager
{
    private val logger = LogManager.getLogger(javaClass)
    private val nodes: ConcurrentHashMap<BlockPos, INetworkNode> = ConcurrentHashMap<BlockPos, INetworkNode>()
    fun read(tag: CompoundTag) {
        if (tag.contains(NBT_NODES)) {
            val nodesTag: ListTag = tag.getList(NBT_NODES, LIST_TAG_TYPE)
            nodes.clear()
            for (i in nodesTag.indices) {
                val nodeTag: CompoundTag = nodesTag.getCompound(i)
                val id = Identifier(nodeTag.getString(NBT_NODE_ID))
                val data: CompoundTag = nodeTag.getCompound(NBT_NODE_DATA)
                val pos: BlockPos = BlockPos.fromLong(nodeTag.getLong(NBT_NODE_POS))
                val factory: INetworkNodeFactory? = API.instance().networkNodeRegistry.get(id)
                if (factory != null) {
                    var node: INetworkNode? = null
                    try {
                        node = factory.create(data, world, pos)
                    } catch (t: Throwable) {
                        logger.error("Could not read network node", t)
                    }
                    if (node != null) {
                        nodes.put(pos, node)
                    }
                } else {
                    logger.warn("Factory for $id not found in network node registry")
                }
            }
        }
    }

    fun write(tag: CompoundTag): CompoundTag {
        val list = ListTag()
        for (node in all()) {
            try {
                val nodeTag = CompoundTag()
                nodeTag.putString(NBT_NODE_ID, node.id.toString())
                nodeTag.putLong(NBT_NODE_POS, node.pos.asLong())
                nodeTag.put(NBT_NODE_DATA, node.write(CompoundTag()))
                list.add(nodeTag)
            } catch (t: Throwable) {
                logger.error("Error while saving network node", t)
            }
        }
        tag.put(NBT_NODES, list)
        return tag
    }

    override fun getNode(pos: BlockPos): INetworkNode? {
        return nodes[pos]
    }

    override fun removeNode(pos: BlockPos) {
        nodes.remove(pos)
    }

    override fun setNode(pos: BlockPos, node: INetworkNode) {
        nodes[pos] = node
    }

    override fun all(): Collection<INetworkNode> {
        return nodes.values
    }

    override fun markForSaving() {
//        markDirty()
    }

    companion object {
        const val NAME = "refinedstorage_nodes"
        private const val NBT_NODES = "Nodes"
        private const val NBT_NODE_ID = "Id"
        private const val NBT_NODE_DATA = "Data"
        private const val NBT_NODE_POS = "Pos"
    }

}