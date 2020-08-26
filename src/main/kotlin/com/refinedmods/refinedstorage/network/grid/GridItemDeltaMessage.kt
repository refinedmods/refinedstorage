package com.refinedmods.refinedstorage.network.grid

import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.api.util.StackListEntry
import com.refinedmods.refinedstorage.api.util.StackListResult
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.screen.grid.GridScreen
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack
import com.refinedmods.refinedstorage.util.StackUtils.readItemGridStack
import com.refinedmods.refinedstorage.util.StackUtils.writeItemGridStack
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fml.network.NetworkEvent
import org.apache.commons.lang3.tuple.Pair
import java.util.*
import java.util.function.Consumer
import java.util.function.Supplier

class GridItemDeltaMessage {
    @Nullable
    private var network: INetwork? = null
    private var deltas: List<StackListResult<ItemStack>>? = null
    private var clientDeltas: List<Pair<IGridStack, Int>>? = null

    constructor(network: INetwork?, deltas: List<StackListResult<ItemStack>>?) {
        this.network = network
        this.deltas = deltas
    }

    constructor(clientDeltas: List<Pair<IGridStack, Int>>?) {
        this.clientDeltas = clientDeltas
    }

    companion object {
        fun decode(buf: PacketByteBuf): GridItemDeltaMessage {
            val size: Int = buf.readInt()
            val clientDeltas: MutableList<Pair<IGridStack, Int>> = LinkedList()
            for (i in 0 until size) {
                val delta: Int = buf.readInt()
                clientDeltas.add(Pair.of(readItemGridStack(buf), delta))
            }
            return GridItemDeltaMessage(clientDeltas)
        }

        fun encode(message: GridItemDeltaMessage, buf: PacketByteBuf) {
            buf.writeInt(message.deltas!!.size)
            for (delta in message.deltas!!) {
                buf.writeInt(delta.change)
                val craftingEntry: StackListEntry<ItemStack>? = message.network!!.itemStorageCache!!.getCraftablesList()!!.getEntry(delta.stack, IComparer.COMPARE_NBT)
                writeItemGridStack(buf, delta.stack, delta.id, craftingEntry?.id, false, message.network!!.itemStorageTracker!![delta.stack])
            }
        }

        fun handle(message: GridItemDeltaMessage, ctx: Supplier<NetworkEvent.Context>) {
            BaseScreen.executeLater(GridScreen::class.java) { grid: GridScreen ->
                message.clientDeltas!!.forEach(Consumer { p: Pair<IGridStack, Int> -> grid.view.postChange(p.left, p.right) })
                grid.view.sort()
            }
            ctx.get().setPacketHandled(true)
        }
    }
}