package com.refinedmods.refinedstorage.screen.grid

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.container.CraftingSettingsContainer
import com.refinedmods.refinedstorage.network.grid.GridCraftingPreviewRequestMessage
import com.refinedmods.refinedstorage.screen.AmountSpecifyingScreen
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.screen.grid.stack.FluidGridStack
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.text.Text
import net.minecraft.util.text.TranslationTextComponent
import net.minecraftforge.fluids.FluidAttributes

class CraftingSettingsScreen(parent: BaseScreen<*>?, player: PlayerEntity, private val stack: IGridStack?) : AmountSpecifyingScreen<CraftingSettingsContainer?>(parent, CraftingSettingsContainer(player, stack), 172, 99, player.inventory, TranslationTextComponent("container.crafting")) {
    protected override val okButtonText: Text
        protected get() = TranslationTextComponent("misc.refinedstorage.start")
    protected override val texture: String
        protected get() = "gui/amount_specifying.png"
    protected override val increments: IntArray
        protected get() = if (stack is FluidGridStack) {
            intArrayOf(
                    100, 500, 1000,
                    -100, -500, -1000
            )
        } else {
            intArrayOf(
                    1, 10, 64,
                    -1, -10, -64
            )
        }
    protected override val defaultAmount: Int
        protected get() = if (stack is FluidGridStack) FluidAttributes.BUCKET_VOLUME else 1

    override fun canAmountGoNegative(): Boolean {
        return false
    }

    override fun getMaxAmount(): Int {
        return Int.MAX_VALUE
    }

    override fun onOkButtonPressed(shiftDown: Boolean) {
        try {
            val quantity = amountField!!.text.toInt()
            RS.NETWORK_HANDLER.sendToServer(GridCraftingPreviewRequestMessage(stack.getId(), quantity, shiftDown, stack is FluidGridStack))
            okButton.active = false
        } catch (e: NumberFormatException) {
            // NO OP
        }
    }
}