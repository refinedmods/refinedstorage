package com.refinedmods.refinedstorage.network.grid

import com.refinedmods.refinedstorage.api.util.StackListResult
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.screen.grid.GridScreen
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack
import com.refinedmods.refinedstorage.tile.grid.portable.IPortableGrid
import com.refinedmods.refinedstorage.util.StackUtils.readItemGridStack
import com.refinedmods.refinedstorage.util.StackUtils.writeItemGridStack
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fml.network.NetworkEvent
import org.apache.commons.lang3.tuple.Pair
import java.util.*
import java.util.function.Consumer
import java.util.function.Supplier

class PortableGridItemDeltaMessage {
    @Nullable
    private var portableGrid: IPortableGrid? = null
    private var deltas: List<StackListResult<ItemStack>>? = null
    private var clientDeltas: List<Pair<IGridStack, Int>>? = null

    constructor(portableGrid: IPortableGrid?, deltas: List<StackListResult<ItemStack>>?) {
        this.portableGrid = portableGrid
        this.deltas = deltas
    }

    constructor(clientDeltas: List<Pair<IGridStack, Int>>?) {
        this.clientDeltas = clientDeltas
    }

    companion object {
        fun decode(buf: PacketByteBuf): PortableGridItemDeltaMessage {
            val size: Int = buf.readInt()
            val clientDeltas: MutableList<Pair<IGridStack, Int>> = LinkedList()
            for (i in 0 until size) {
                val delta: Int = buf.readInt()
                clientDeltas.add(Pair.of(readItemGridStack(buf), delta))
            }
            return PortableGridItemDeltaMessage(clientDeltas)
        }

        fun encode(message: PortableGridItemDeltaMessage, buf: PacketByteBuf) {
            buf.writeInt(message.deltas!!.size)
            for (delta in message.deltas!!) {
                buf.writeInt(delta.change)
                writeItemGridStack(buf, delta.stack, delta.id, null, false, message.portableGrid!!.itemStorageTracker[delta.stack])
            }
        }

        fun handle(message: PortableGridItemDeltaMessage, ctx: Supplier<NetworkEvent.Context>) {
            BaseScreen.executeLater(GridScreen::class.java) { grid: GridScreen ->
                message.clientDeltas!!.forEach(Consumer { p: Pair<IGridStack, Int> -> grid.view.postChange(p.left, p.right) })
                grid.view.sort()
            }
            ctx.get().setPacketHandled(true)
        }
    }
}