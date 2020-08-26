package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.RSContainers
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.apiimpl.network.node.CrafterManagerNetworkNode
import com.refinedmods.refinedstorage.container.slot.CrafterManagerSlot
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler
import com.refinedmods.refinedstorage.inventory.item.validator.PatternItemValidator
import com.refinedmods.refinedstorage.item.PatternItem
import com.refinedmods.refinedstorage.screen.IScreenInfoProvider
import com.refinedmods.refinedstorage.screen.grid.filtering.GridFilterParser
import com.refinedmods.refinedstorage.screen.grid.stack.ItemGridStack
import com.refinedmods.refinedstorage.tile.CrafterManagerTile
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.container.Slot
import net.minecraft.item.ItemStack
import net.minecraft.util.text.Text
import net.minecraftforge.items.IItemHandlerModifiable
import java.util.*

class CrafterManagerContainer(crafterManager: CrafterManagerTile?, player: PlayerEntity, windowId: Int) : BaseContainer(RSContainers.CRAFTER_MANAGER, crafterManager, player, windowId) {
    private var screenInfoProvider: IScreenInfoProvider? = null
    private val crafterManager: CrafterManagerNetworkNode
    private var containerData: Map<String, Int>? = null
    private val dummyInventories: MutableMap<String, IItemHandlerModifiable> = HashMap<String, IItemHandlerModifiable>()
    private val headings: MutableMap<String, Int> = HashMap()
    var rows = 0
        private set

    fun setScreenInfoProvider(infoProvider: IScreenInfoProvider?) {
        screenInfoProvider = infoProvider
    }

    fun initSlotsServer() {
        addPlayerInventory(8, screenInfoProvider!!.yPlayerInventory)
        if (crafterManager.network != null) {
            for ((_, value) in crafterManager.network!!.craftingManager.getNamedContainers()!!) {
                for (handler in value!!) {
                    for (i in 0 until handler.getSlots()) {
                        addSlot(CrafterManagerSlot(handler, i, 0, 0, true, screenInfoProvider, crafterManager))
                    }
                }
            }
        }
    }

    fun initSlots(@Nullable data: Map<String, Int>?) {
        if (data != null) {
            containerData = data
        }
        this.inventorySlots.clear()
        this.inventoryItemStacks.clear()
        headings.clear()
        rows = 0
        addPlayerInventory(8, screenInfoProvider!!.yPlayerInventory)
        var y = 19 + 18 - screenInfoProvider!!.currentOffset * 18
        var x = 8
        val filters = GridFilterParser.getFilters(null, screenInfoProvider!!.searchFieldText, emptyList())
        for ((key, value) in containerData!!) {
            var dummy: IItemHandlerModifiable?
            if (data == null) { // We're only resizing, get the previous inventory...
                dummy = dummyInventories[key]
            } else {
                dummyInventories[key] = object : BaseItemHandler(value) {
                    fun getSlotLimit(slot: Int): Int {
                        return 1
                    }

                    @Nonnull
                    override fun insertItem(slot: Int, @Nonnull stack: ItemStack, simulate: Boolean): ItemStack {
                        return if (PatternItemValidator(player.entityWorld).test(stack)) {
                            super.insertItem(slot, stack, simulate)
                        } else stack
                    }
                }.also { dummy = it }
            }
            var foundItemsInCategory = false
            val yHeading = y - 19
            var slotFound = 0
            for (slot in 0 until value) {
                var visible = true
                if (!screenInfoProvider!!.searchFieldText.trim { it <= ' ' }.isEmpty()) {
                    val stack: ItemStack = dummy.getStackInSlot(slot)
                    if (stack.isEmpty) {
                        visible = false
                    } else {
                        val pattern = PatternItem.fromCache(crafterManager.world, stack)
                        visible = false
                        for (output in pattern.getOutputs()) {
                            val outputConverted = ItemGridStack(output)
                            for (filter in filters) {
                                if (filter.test(outputConverted)) {
                                    visible = true
                                    break
                                }
                            }
                        }
                    }
                }
                addSlot(CrafterManagerSlot(dummy, slot, x, y, visible, screenInfoProvider, crafterManager))
                if (visible) {
                    foundItemsInCategory = true
                    x += 18

                    // Don't increase y level if we are on our last slot row (otherwise we do y += 18 * 3)
                    if ((slotFound + 1) % 9 == 0 && slot + 1 < value) {
                        x = 8
                        y += 18
                        rows++
                    }
                    slotFound++
                }
            }
            if (foundItemsInCategory) {
                headings[key] = yHeading
                x = 8
                y += 18 * 2
                rows += 2 // Heading and first row
            }
        }
    }

    fun getHeadings(): Map<String, Int> {
        return headings
    }

    override fun transferStackInSlot(player: PlayerEntity?, index: Int): ItemStack? {
        var stack = ItemStack.EMPTY
        val slot: Slot = getSlot(index)
        if (slot.getHasStack()) {
            stack = slot.getStack()
            if (!PatternItemValidator(getPlayer().entityWorld).test(stack)) {
                return ItemStack.EMPTY
            }
            if (index < 9 * 4) {
                if (!mergeItemStack(stack, 9 * 4, inventorySlots.size(), false)) {
                    return ItemStack.EMPTY
                }
            } else if (!mergeItemStack(stack, 0, 9 * 4, false)) {
                return ItemStack.EMPTY
            }
            if (stack.count == 0) {
                slot.putStack(ItemStack.EMPTY)
            } else {
                slot.onSlotChanged()
            }
        }
        return stack
    }

    init {
        this.crafterManager = crafterManager.getNode()
    }
}