package com.refinedmods.refinedstorage.network.grid

import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.api.util.StackListEntry
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.screen.grid.GridScreen
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack
import com.refinedmods.refinedstorage.screen.grid.view.ItemGridView
import com.refinedmods.refinedstorage.util.StackUtils.readItemGridStack
import com.refinedmods.refinedstorage.util.StackUtils.writeItemGridStack
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fml.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

class GridItemUpdateMessage {
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
        fun decode(buf: PacketByteBuf): GridItemUpdateMessage {
            val canCraft: Boolean = buf.readBoolean()
            val size: Int = buf.readInt()
            val stacks: MutableList<IGridStack> = ArrayList()
            for (i in 0 until size) {
                stacks.add(readItemGridStack(buf))
            }
            return GridItemUpdateMessage(canCraft, stacks)
        }

        fun encode(message: GridItemUpdateMessage, buf: PacketByteBuf) {
            buf.writeBoolean(message.canCraft)
            val size: Int = message.network!!.itemStorageCache!!.getList().getStacks().size() + message.network!!.itemStorageCache!!.getCraftablesList().getStacks().size()
            buf.writeInt(size)
            for (stack in message.network!!.itemStorageCache!!.getList().getStacks()) {
                val craftingEntry: StackListEntry<ItemStack>? = message.network!!.itemStorageCache!!.getCraftablesList()!!.getEntry(stack.stack, IComparer.COMPARE_NBT)
                writeItemGridStack(buf, stack.stack!!, stack.id, craftingEntry?.id, false, message.network!!.itemStorageTracker!![stack.stack])
            }
            for (stack in message.network!!.itemStorageCache!!.getCraftablesList().getStacks()) {
                val regularEntry: StackListEntry<ItemStack>? = message.network!!.itemStorageCache!!.getList()!!.getEntry(stack.stack, IComparer.COMPARE_NBT)
                writeItemGridStack(buf, stack.stack!!, stack.id, regularEntry?.id, true, message.network!!.itemStorageTracker!![stack.stack])
            }
        }

        fun handle(message: GridItemUpdateMessage, ctx: Supplier<NetworkEvent.Context>) {
            BaseScreen.executeLater(GridScreen::class.java) { grid: GridScreen ->
                grid.view = ItemGridView(grid, GridScreen.defaultSorter, GridScreen.sorters)
                grid.view.setCanCraft(message.canCraft)
                grid.view.stacks = message.stacks
                grid.view.sort()
            }
            ctx.get().setPacketHandled(true)
        }
    }
}