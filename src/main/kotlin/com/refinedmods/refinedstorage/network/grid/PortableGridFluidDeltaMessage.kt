package com.refinedmods.refinedstorage.network.grid

import com.refinedmods.refinedstorage.api.util.StackListResult
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.screen.grid.GridScreen
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack
import com.refinedmods.refinedstorage.tile.grid.portable.IPortableGrid
import com.refinedmods.refinedstorage.util.StackUtils.readFluidGridStack
import com.refinedmods.refinedstorage.util.StackUtils.writeFluidGridStack
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fluids.FluidInstance
import net.minecraftforge.fml.network.NetworkEvent
import org.apache.commons.lang3.tuple.Pair
import java.util.*
import java.util.function.Consumer
import java.util.function.Supplier

class PortableGridFluidDeltaMessage {
    private var portableGrid: IPortableGrid? = null
    private var deltas: List<StackListResult<FluidInstance>>? = null
    private var clientDeltas: List<Pair<IGridStack, Int>>? = null

    constructor(portableGrid: IPortableGrid?, deltas: List<StackListResult<FluidInstance>>?) {
        this.portableGrid = portableGrid
        this.deltas = deltas
    }

    constructor(clientDeltas: List<Pair<IGridStack, Int>>?) {
        this.clientDeltas = clientDeltas
    }

    companion object {
        fun decode(buf: PacketByteBuf): PortableGridFluidDeltaMessage {
            val size: Int = buf.readInt()
            val clientDeltas: MutableList<Pair<IGridStack, Int>> = LinkedList()
            for (i in 0 until size) {
                val delta: Int = buf.readInt()
                clientDeltas.add(Pair.of(readFluidGridStack(buf), delta))
            }
            return PortableGridFluidDeltaMessage(clientDeltas)
        }

        fun encode(message: PortableGridFluidDeltaMessage, buf: PacketByteBuf) {
            buf.writeInt(message.deltas!!.size)
            for (delta in message.deltas!!) {
                buf.writeInt(delta.change)
                writeFluidGridStack(buf, delta.stack, delta.id, null, false, message.portableGrid!!.fluidStorageTracker[delta.stack])
            }
        }

        fun handle(message: PortableGridFluidDeltaMessage, ctx: Supplier<NetworkEvent.Context>) {
            BaseScreen.executeLater(GridScreen::class.java) { grid: GridScreen ->
                message.clientDeltas!!.forEach(Consumer { p: Pair<IGridStack, Int> -> grid.view.postChange(p.left, p.right) })
                grid.view.sort()
            }
            ctx.get().setPacketHandled(true)
        }
    }
}