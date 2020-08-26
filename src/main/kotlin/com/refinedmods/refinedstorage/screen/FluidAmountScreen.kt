package com.refinedmods.refinedstorage.screen

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.container.FluidAmountContainer
import com.refinedmods.refinedstorage.network.SetFluidFilterSlotMessage
import com.refinedmods.refinedstorage.util.StackUtils.copy
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.text.Text
import net.minecraft.util.text.TranslationTextComponent
import net.minecraftforge.fluids.FluidInstance
import org.apache.commons.lang3.tuple.Pair
import java.util.function.Function

class FluidAmountScreen(parent: BaseScreen<*>?, player: PlayerEntity, private val containerSlot: Int, stack: FluidInstance?, maxAmount: Int, @Nullable alternativesScreenFactory: Function<Screen, Screen>?) : AmountSpecifyingScreen<FluidAmountContainer?>(parent, FluidAmountContainer(player, stack), if (alternativesScreenFactory != null) 194 else 172, 99, player.inventory, TranslationTextComponent("gui.refinedstorage.fluid_amount")) {
    private val stack: FluidInstance?
    private override val maxAmount: Int

    @Nullable
    private val alternativesScreenFactory: Function<Screen, Screen>?
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
        return stack.getAmount()
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
                100, 500, 1000,
                -100, -500, -1000
        )
    }

    override fun onOkButtonPressed(shiftDown: Boolean) {
        try {
            val amount = amountField!!.text.toInt()
            RS.NETWORK_HANDLER.sendToServer(SetFluidFilterSlotMessage(containerSlot, copy(stack, amount)))
            close()
        } catch (e: NumberFormatException) {
            // NO OP
        }
    }

    init {
        this.stack = stack
        this.maxAmount = maxAmount
        this.alternativesScreenFactory = alternativesScreenFactory
    }
}