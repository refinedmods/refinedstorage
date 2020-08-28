package com.refinedmods.refinedstorage.api.network

import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World


/**
 * Represents a node that can send a wireless signal.
 */
interface IWirelessTransmitter {
    /**
     * @return the range in blocks of this transmitter, starting from [IWirelessTransmitter.origin]
     */
    val range: Int

    /**
     * @return the position where the wireless signal starts
     */
    val origin: BlockPos

    /**
     * @return the dimension in which the transmitter is
     */
    fun getDimension(): RegistryKey<World?>?
}