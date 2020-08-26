package com.refinedmods.refinedstorage.screen

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.container.AmountContainer
import com.refinedmods.refinedstorage.network.SetFilterSlotMessage
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.text.Text
import net.minecraft.util.text.TranslationTextComponent
import net.minecraftforge.items.ItemHandlerHelper
import org.apache.commons.lang3.tuple.Pair
import java.util.function.Function

class ItemAmountScreen(parent: BaseScreen<*>?, player: PlayerEntity, private val containerSlot: Int, private val stack: ItemStack, private override val maxAmount: Int, @field:Nullable @param:Nullable private val alternativesScreenFactory: Function<Screen, Screen>?) : AmountSpecifyingScreen<AmountContainer?>(parent, AmountContainer(player, stack), if (alternativesScreenFactory != null) 194 else 172, 99, player.inventory, TranslationTextComponent("gui.refinedstorage.item_amount")) {
    override fun getOkCancelButtonWidth(): Int {
        return if (alternativesScreenFactory != null) 75 else super.getOkCancelButtonWidth()
    }

    override fun onPostInit(x: Int, y: Int) {
        super.onPostInit(x, y)
        if (alternativesScreenFactory != null) {
            addButton(x + 114, cancelButton.y + 24, okCancelButtonWidth, 20, TranslationTextComponent("gui.refinedstorage.alternatives"), true, true, Button.IPressable({ btn -> minecraft.displayGuiScreen(alternativesScreenFactory.apply(this)) }))
        }
    }

    override fun getOkCancelPos(): Pair<Int?, Int?>? {
        return if (alternativesScreenFactory == null) {
            super.getOkCancelPos()
        } else Pair.of(114, 22)
    }

    override fun getDefaultAmount(): Int {
        return stack.count
    }

    override fun canAmountGoNegative(): Boolean {
        return false
    }

    override fun getMaxAmount(): Int {
        return maxAmount
    }

    override fun getOkButtonText(): Text {
        return TranslationTextComponent("misc.refinedstorage.set")
    }

    override fun getTexture(): String {
        return if (alternativesScreenFactory != null) "gui/amount_specifying_wide.png" else "gui/amount_specifying.png"
    }

    override fun getIncrements(): IntArray {
        return intArrayOf(
                1, 10, 64,
                -1, -10, -64
        )
    }

    override fun onOkButtonPressed(shiftDown: Boolean) {
        try {
            val amount = amountField!!.text.toInt()
            RS.NETWORK_HANDLER.sendToServer(SetFilterSlotMessage(containerSlot, ItemHandlerHelper.copyStackWithSize(stack, amount)))
            close()
        } catch (e: NumberFormatException) {
            // NO OP
        }
    }
}