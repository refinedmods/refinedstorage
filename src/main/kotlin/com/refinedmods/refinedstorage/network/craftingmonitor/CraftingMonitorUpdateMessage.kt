package com.refinedmods.refinedstorage.network.craftingmonitor

import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement
import com.refinedmods.refinedstorage.api.autocrafting.task.CraftingTaskReadException
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingRequestInfo
import com.refinedmods.refinedstorage.api.network.grid.IGridTab
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.network.ClientProxy
import com.refinedmods.refinedstorage.network.craftingmonitor.CraftingMonitorUpdateMessage
import com.refinedmods.refinedstorage.screen.CraftingMonitorScreen
import com.refinedmods.refinedstorage.tile.craftingmonitor.ICraftingMonitor
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fml.network.NetworkEvent
import org.apache.logging.log4j.LogManager
import java.util.*
import java.util.function.Function
import java.util.function.Supplier

class CraftingMonitorUpdateMessage {
    private var craftingMonitor: ICraftingMonitor? = null
    var tasks: List<IGridTab> = ArrayList()
        private set

    constructor(craftingMonitor: ICraftingMonitor?) {
        this.craftingMonitor = craftingMonitor
    }

    constructor(tasks: List<IGridTab>) {
        this.tasks = tasks
    }

    companion object {
        private val LOGGER = LogManager.getLogger(CraftingMonitorUpdateMessage::class.java)
        fun decode(buf: PacketByteBuf): CraftingMonitorUpdateMessage {
            val size: Int = buf.readInt()
            val tasks: MutableList<IGridTab> = ArrayList()
            for (i in 0 until size) {
                val id: UUID = buf.readUniqueId()
                var requested: ICraftingRequestInfo? = null
                try {
                    requested = instance().createCraftingRequestInfo(buf.readCompoundTag())
                } catch (e: CraftingTaskReadException) {
                    LOGGER.error("Could not create crafting request info", e)
                }
                val qty: Int = buf.readInt()
                val executionStarted: Long = buf.readLong()
                val percentage: Int = buf.readInt()
                val elements: MutableList<ICraftingMonitorElement?> = ArrayList()
                val elementCount: Int = buf.readInt()
                for (j in 0 until elementCount) {
                    val factory: Function<PacketByteBuf?, ICraftingMonitorElement?>? = instance().getCraftingMonitorElementRegistry()!![buf.readIdentifier()]
                    if (factory != null) {
                        elements.add(factory.apply(buf))
                    }
                }
                tasks.add(CraftingMonitorScreen.Task(id, requested, qty, executionStarted, percentage, elements))
            }
            return CraftingMonitorUpdateMessage(tasks)
        }

        fun encode(message: CraftingMonitorUpdateMessage, buf: PacketByteBuf) {
            buf.writeInt(message.craftingMonitor!!.tasks.size)
            for (task in message.craftingMonitor!!.tasks) {
                buf.writeUniqueId(task.getId())
                buf.writeCompoundTag(task.getRequested()!!.writeToNbt())
                buf.writeInt(task.getQuantity())
                buf.writeLong(task.getStartTime())
                buf.writeInt(task.getCompletionPercentage())
                val elements = task.getCraftingMonitorElements()
                buf.writeInt(elements!!.size)
                for (element in elements) {
                    buf.writeIdentifier(element!!.getId())
                    element.write(buf)
                }
            }
        }

        fun handle(message: CraftingMonitorUpdateMessage, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().enqueueWork({ ClientProxy.onReceivedCraftingMonitorUpdateMessage(message) })
            ctx.get().setPacketHandled(true)
        }
    }
}