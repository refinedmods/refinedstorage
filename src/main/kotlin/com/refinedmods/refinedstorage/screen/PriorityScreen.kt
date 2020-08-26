package com.refinedmods.refinedstorage.screen

import com.refinedmods.refinedstorage.tile.data.TileDataManager
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.util.text.Text
import net.minecraft.util.text.TranslationTextComponent
import org.apache.commons.lang3.tuple.Pair

class PriorityScreen(parent: BaseScreen<*>?, private val priority: TileDataParameter<Int, *>, inventory: PlayerInventory?) : AmountSpecifyingScreen<Container?>(parent, object : Container(null, 0) {
    fun canInteractWith(player: PlayerEntity?): Boolean {
        return false
    }
}, 164, 92, inventory, TranslationTextComponent("misc.refinedstorage.priority")) {
    override fun getDefaultAmount(): Int {
        return priority.value
    }

    override fun getOkButtonText(): Text {
        return TranslationTextComponent("misc.refinedstorage.set")
    }

    override fun getTexture(): String {
        return "gui/priority.png"
    }

    override fun getAmountPos(): Pair<Int, Int> {
        return Pair.of(18 + 1, 47 + 1)
    }

    override fun getOkCancelPos(): Pair<Int?, Int?>? {
        return Pair.of(107, 30)
    }

    override fun canAmountGoNegative(): Boolean {
        return true
    }

    override fun getMaxAmount(): Int {
        return Int.MAX_VALUE
    }

    override fun getIncrements(): IntArray {
        return intArrayOf(
                1, 5, 10,
                -1, -5, -10
        )
    }

    override fun onOkButtonPressed(noPreview: Boolean) {
        try {
            val amount = amountField!!.text.toInt()
            TileDataManager.setParameter(priority, amount)
            close()
        } catch (e: NumberFormatException) {
            // NO OP
        }
    }
}