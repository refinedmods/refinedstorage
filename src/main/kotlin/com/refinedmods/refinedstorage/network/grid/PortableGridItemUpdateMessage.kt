package com.refinedmods.refinedstorage.network.grid

import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.screen.grid.GridScreen
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack
import com.refinedmods.refinedstorage.screen.grid.view.ItemGridView
import com.refinedmods.refinedstorage.tile.grid.portable.IPortableGrid
import com.refinedmods.refinedstorage.util.StackUtils.readItemGridStack
import com.refinedmods.refinedstorage.util.StackUtils.writeItemGridStack
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fml.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

class PortableGridItemUpdateMessage {
    private var portableGrid: IPortableGrid? = null
    private var stacks: List<IGridStack> = ArrayList()

    constructor(stacks: List<IGridStack>) {
        this.stacks = stacks
    }

    constructor(portableGrid: IPortableGrid?) {
        this.portableGrid = portableGrid
    }

    companion object {
        fun decode(buf: PacketByteBuf): PortableGridItemUpdateMessage {
            val size: Int = buf.readInt()
            val stacks: MutableList<IGridStack> = ArrayList()
            for (i in 0 until size) {
                stacks.add(readItemGridStack(buf))
            }
            return PortableGridItemUpdateMessage(stacks)
        }

        fun encode(message: PortableGridItemUpdateMessage, buf: PacketByteBuf) {
            val size: Int = message.portableGrid!!.itemCache.getList().getStacks().size()
            buf.writeInt(size)
            for (stack in message.portableGrid!!.itemCache.getList().getStacks()) {
                writeItemGridStack(buf, stack.stack!!, stack.id, null, false, message.portableGrid!!.itemStorageTracker[stack.stack])
            }
        }

        fun handle(message: PortableGridItemUpdateMessage, ctx: Supplier<NetworkEvent.Context>) {
            BaseScreen.executeLater(GridScreen::class.java) { grid: GridScreen ->
                grid.view = ItemGridView(grid, GridScreen.defaultSorter, GridScreen.sorters)
                grid.view.stacks = message.stacks
                grid.view.sort()
            }
            ctx.get().setPacketHandled(true)
        }
    }
}