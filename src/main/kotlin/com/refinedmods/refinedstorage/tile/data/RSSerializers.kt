package com.refinedmods.refinedstorage.tile.data

import com.refinedmods.refinedstorage.api.storage.AccessType
import com.refinedmods.refinedstorage.tile.ClientNode
import com.refinedmods.refinedstorage.util.AccessTypeUtils
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.PacketIO
import net.minecraft.util.Identifier
import net.minecraftforge.fluids.FluidInstance
import java.util.*

object RSSerializers {
    val CLIENT_NODE_SERIALIZER: PacketIO<List<ClientNode>> = object : PacketIO<List<ClientNode?>?>() {
        fun write(buf: PacketByteBuf, nodes: List<ClientNode>) {
            buf.writeInt(nodes.size)
            for (node in nodes) {
                buf.writeItemStack(node.stack)
                buf.writeInt(node.amount)
                buf.writeInt(node.energyUsage)
            }
        }

        fun read(buf: PacketByteBuf): List<ClientNode> {
            val nodes: MutableList<ClientNode> = ArrayList()
            val size: Int = buf.readInt()
            for (i in 0 until size) {
                nodes.add(ClientNode(buf.readItemStack(), buf.readInt(), buf.readInt()))
            }
            return nodes
        }

        fun createKey(id: Int): DataParameter<List<ClientNode>>? {
            return null
        }

        fun copyValue(value: List<ClientNode>): List<ClientNode> {
            return value
        }
    }
    val FLUID_STACK_SERIALIZER: PacketIO<FluidInstance> = object : PacketIO<FluidInstance?>() {
        fun write(buf: PacketByteBuf?, value: FluidInstance) {
            value.writeToPacket(buf)
        }

        fun read(buf: PacketByteBuf?): FluidInstance {
            return FluidInstance.readFromPacket(buf)
        }

        fun createKey(id: Int): DataParameter<FluidInstance>? {
            return null
        }

        fun copyValue(value: FluidInstance): FluidInstance {
            return value
        }
    }
    val ACCESS_TYPE_SERIALIZER: PacketIO<AccessType> = object : PacketIO<AccessType?>() {
        fun write(buf: PacketByteBuf, value: AccessType) {
            buf.writeInt(value.getId())
        }

        fun read(buf: PacketByteBuf): AccessType {
            return AccessTypeUtils.getAccessType(buf.readInt())
        }

        fun createKey(id: Int): DataParameter<AccessType>? {
            return null
        }

        fun copyValue(value: AccessType): AccessType {
            return value
        }
    }
    val LONG_SERIALIZER: PacketIO<Long> = object : PacketIO<Long?>() {
        fun write(buf: PacketByteBuf, value: Long?) {
            buf.writeLong(value)
        }

        fun read(buf: PacketByteBuf): Long {
            return buf.readLong()
        }

        fun createKey(id: Int): DataParameter<Long>? {
            return null
        }

        fun copyValue(value: Long): Long {
            return value
        }
    }
    val OPTIONAL_RESOURCE_LOCATION_SERIALIZER: PacketIO<Optional<Identifier>> = object : PacketIO<Optional<Identifier?>?>() {
        fun write(buf: PacketByteBuf, value: Optional<Identifier?>) {
            buf.writeBoolean(value.isPresent())
            value.ifPresent(buf::writeIdentifier)
        }

        fun read(buf: PacketByteBuf): Optional<Identifier> {
            return if (!buf.readBoolean()) {
                Optional.empty<Identifier>()
            } else Optional.of(buf.readIdentifier())
        }

        fun createKey(id: Int): DataParameter<Optional<Identifier>>? {
            return null
        }

        fun copyValue(value: Optional<Identifier>): Optional<Identifier> {
            return value
        }
    }
    val LIST_OF_SET_SERIALIZER: PacketIO<List<Set<Identifier>>> = object : PacketIO<List<Set<Identifier?>?>?>() {
        fun write(buf: PacketByteBuf, value: List<Set<Identifier?>>) {
            buf.writeInt(value.size)
            for (values in value) {
                buf.writeInt(values.size)
                values.forEach(buf::writeIdentifier)
            }
        }

        fun read(buf: PacketByteBuf): List<Set<Identifier>> {
            val value: MutableList<Set<Identifier>> = ArrayList<Set<Identifier>>()
            val size: Int = buf.readInt()
            for (i in 0 until size) {
                val setSize: Int = buf.readInt()
                val values: MutableSet<Identifier> = HashSet<Identifier>()
                for (j in 0 until setSize) {
                    values.add(buf.readIdentifier())
                }
                value.add(values)
            }
            return value
        }

        fun createKey(id: Int): DataParameter<List<Set<Identifier>>>? {
            return null
        }

        fun copyValue(value: List<Set<Identifier>>): List<Set<Identifier>> {
            return value
        }
    }
}