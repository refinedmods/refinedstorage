package com.refinedmods.refinedstorage.network.grid

import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.screen.grid.GridScreen
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack
import com.refinedmods.refinedstorage.screen.grid.view.FluidGridView
import com.refinedmods.refinedstorage.tile.grid.portable.IPortableGrid
import com.refinedmods.refinedstorage.util.StackUtils.readFluidGridStack
import com.refinedmods.refinedstorage.util.StackUtils.writeFluidGridStack
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fluids.FluidInstance
import net.minecraftforge.fml.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

class PortableGridFluidUpdateMessage {
    private var portableGrid: IPortableGrid? = null
    private var stacks: List<IGridStack> = ArrayList()

    constructor(stacks: List<IGridStack>) {
        this.stacks = stacks
    }

    constructor(portableGrid: IPortableGrid?) {
        this.portableGrid = portableGrid
    }

    companion object {
        fun decode(buf: PacketByteBuf): PortableGridFluidUpdateMessage {
            val size: Int = buf.readInt()
            val stacks: MutableList<IGridStack> = ArrayList()
            for (i in 0 until size) {
                stacks.add(readFluidGridStack(buf))
            }
            return PortableGridFluidUpdateMessage(stacks)
        }

        fun encode(message: PortableGridFluidUpdateMessage, buf: PacketByteBuf) {
            val size: Int = message.portableGrid!!.fluidCache.getList().getStacks().size()
            buf.writeInt(size)
            for (stack in message.portableGrid!!.fluidCache.getList().getStacks()) {
                writeFluidGridStack(buf, stack.stack, stack.id, null, false, message.portableGrid!!.fluidStorageTracker[stack.stack])
            }
        }

        fun handle(message: PortableGridFluidUpdateMessage, ctx: Supplier<NetworkEvent.Context>) {
            BaseScreen.executeLater(GridScreen::class.java) { grid: GridScreen ->
                grid.view = FluidGridView(grid, GridScreen.defaultSorter, GridScreen.sorters)
                grid.view.stacks = message.stacks
                grid.view.sort()
            }
            ctx.get().setPacketHandled(true)
        }
    }
}