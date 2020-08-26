package com.refinedmods.refinedstorage.api.network.node

import com.refinedmods.refinedstorage.api.network.INetwork
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

/**
 * Represents a node in the network.
 */
interface INetworkNode {
    /**
     * @return the energy usage of this node
     */
    val energyUsage: Int

    /**
     * Returns the stack that is displayed in the controller GUI.
     * Can be an empty stack if no stack should be shown.
     *
     * @return the item stack of this node
     */
    val itemStack: ItemStack

    /**
     * Called when this node is connected to a network.
     *
     * @param network the network
     */
    fun onConnected(network: INetwork?)

    /**
     * Called when this node is disconnected from a network.
     * This happens when the node is removed, or if the network is removed.
     * If the network runs out of power or no longer runs due to redstone mode settings, this won't be called and has to be detected manually.
     *
     * @param network the network
     */
    fun onDisconnected(network: INetwork?)

    /**
     * Whether this node is active, independent of the network.
     *
     * @return true if this node is active, false otherwise
     */
    val isActive: Boolean

    /**
     * @return the network, or null if this node is not connected to any network
     */
    val network: INetwork?

    /**
     * Updates a network node.
     */
    fun update()

    /**
     * Writes the network node data to NBT.
     *
     * @param tag the tag
     * @return the written tag
     */
    fun write(tag: CompoundTag): CompoundTag

    /**
     * @return the position of this network node
     */
    val pos: BlockPos?

    /**
     * @return the world of this network node
     */
    val world: World?

    /**
     * Marks this node as dirty for saving.
     */
    fun markDirty()

    /**
     * @return the id of this node as specified in [INetworkNodeRegistry]
     */
    val id: Identifier?
    /**
     * @return the owner
     */
    /**
     * @param owner the owner
     */
    var owner: UUID?
}