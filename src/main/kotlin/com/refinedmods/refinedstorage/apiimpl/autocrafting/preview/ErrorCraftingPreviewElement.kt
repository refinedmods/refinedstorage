package com.refinedmods.refinedstorage.apiimpl.autocrafting.preview

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement
import com.refinedmods.refinedstorage.api.autocrafting.task.CalculationResultType
import com.refinedmods.refinedstorage.api.render.IElementDrawers
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class ErrorCraftingPreviewElement(
        private val type: CalculationResultType,
        private val stack: ItemStack
) : ICraftingPreviewElement<ItemStack> {

    override fun getElement(): ItemStack = stack

    override fun draw(matrixStack: MatrixStack, x: Int, y: Int, drawers: IElementDrawers) { /** NO OP */ }

    override var available: Int = 0
    override var toCraft: Int = 0
    override var missing: Boolean = false

    override fun write(buf: PacketByteBuf) {
        buf.writeInt(type.ordinal)
        buf.writeItemStack(stack)
    }

    fun getType(): CalculationResultType {
        return type
    }

    override fun getId(): Identifier = ID


    companion object {
        val ID: Identifier = Identifier(RS.ID, "error")
        fun read(buf: PacketByteBuf): ErrorCraftingPreviewElement {
            val errorIdx: Int = buf.readInt()
            val error: CalculationResultType = if (errorIdx >= 0 && errorIdx < CalculationResultType.values().size) CalculationResultType.values().get(errorIdx) else CalculationResultType.TOO_COMPLEX
            val stack: ItemStack = buf.readItemStack()
            return ErrorCraftingPreviewElement(error, stack)
        }
    }
}