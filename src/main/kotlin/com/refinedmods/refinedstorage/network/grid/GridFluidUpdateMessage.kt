package com.refinedmods.refinedstorage.network.grid

import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.api.util.StackListEntry
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.screen.grid.GridScreen
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack
import com.refinedmods.refinedstorage.screen.grid.view.FluidGridView
import com.refinedmods.refinedstorage.util.StackUtils.readFluidGridStack
import com.refinedmods.refinedstorage.util.StackUtils.writeFluidGridStack
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fluids.FluidInstance
import net.minecraftforge.fml.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

class GridFluidUpdateMessage {
    private var network: INetwork? = null
    private val canCraft: Boolean
    private var stacks: List<IGridStack> = ArrayList()

    constructor(canCraft: Boolean, stacks: List<IGridStack>) {
        this.canCraft = canCraft
        this.stacks = stacks
    }

    constructor(network: INetwork?, canCraft: Boolean) {
        this.network = network
        this.canCraft = canCraft
    }

    companion object {
        fun decode(buf: PacketByteBuf): GridFluidUpdateMessage {
            val canCraft: Boolean = buf.readBoolean()
            val size: Int = buf.readInt()
            val stacks: MutableList<IGridStack> = ArrayList()
            for (i in 0 until size) {
                stacks.add(readFluidGridStack(buf))
            }
            return GridFluidUpdateMessage(canCraft, stacks)
        }

        fun encode(message: GridFluidUpdateMessage, buf: PacketByteBuf) {
            buf.writeBoolean(message.canCraft)
            val size: Int = message.network!!.fluidStorageCache!!.getList().getStacks().size() + message.network!!.fluidStorageCache!!.getCraftablesList().getStacks().size()
            buf.writeInt(size)
            for (stack in message.network!!.fluidStorageCache!!.getList().getStacks()) {
                val craftingEntry: StackListEntry<FluidInstance>? = message.network!!.fluidStorageCache!!.getCraftablesList()!!.getEntry(stack.stack, IComparer.COMPARE_NBT)
                writeFluidGridStack(buf, stack.stack, stack.id, craftingEntry?.id, false, message.network!!.fluidStorageTracker!![stack.stack])
            }
            for (stack in message.network!!.fluidStorageCache!!.getCraftablesList().getStacks()) {
                val regularEntry: StackListEntry<FluidInstance>? = message.network!!.fluidStorageCache!!.getList()!!.getEntry(stack.stack, IComparer.COMPARE_NBT)
                writeFluidGridStack(buf, stack.stack, stack.id, regularEntry?.id, true, message.network!!.fluidStorageTracker!![stack.stack])
            }
        }

        fun handle(message: GridFluidUpdateMessage, ctx: Supplier<NetworkEvent.Context>) {
            BaseScreen.executeLater(GridScreen::class.java) { grid: GridScreen ->
                grid.view = FluidGridView(grid, GridScreen.defaultSorter, GridScreen.sorters)
                grid.view.setCanCraft(message.canCraft)
                grid.view.stacks = message.stacks
                grid.view.sort()
            }
            ctx.get().setPacketHandled(true)
        }
    }
}