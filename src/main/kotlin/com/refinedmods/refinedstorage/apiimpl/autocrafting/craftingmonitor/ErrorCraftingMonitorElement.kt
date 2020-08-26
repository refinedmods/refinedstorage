package com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor

import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement
import com.refinedmods.refinedstorage.api.render.IElementDrawers
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.render.Styles
import com.refinedmods.refinedstorage.util.PacketByteBufUtils
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier


class ErrorCraftingMonitorElement(private val base: ICraftingMonitorElement, private val message: String) : ICraftingMonitorElement {
    override fun draw(matrixStack: MatrixStack, x: Int, y: Int, drawers: IElementDrawers) {
        base.draw(matrixStack, x, y, drawers)
        drawers.errorDrawer.draw(matrixStack, x, y, null)
    }

    override fun getTooltip(): List<Text> {
        val items: MutableList<Text> = base.getTooltip().toMutableList()
        items.add(TranslatableText(message).setStyle(Styles.RED))
        return items
    }

    override fun getId(): Identifier {
        return ID
    }

    override fun getBaseId(): Identifier {
        return base.getId()
    }

    override fun write(buf: PacketByteBuf) {
        buf.writeIdentifier(base.getId())
        buf.writeString(message)
        base.write(buf)
    }

    override fun merge(element: ICraftingMonitorElement): Boolean {
        return elementHashCode() == element.elementHashCode() && base.merge((element as ErrorCraftingMonitorElement).base)
    }

    override fun baseElementHashCode(): Int {
        return base.elementHashCode()
    }

    override fun elementHashCode(): Int {
        return base.elementHashCode() xor message.hashCode()
    }

    fun mergeBases(element: ICraftingMonitorElement) {
        base.merge(element)
    }

    companion object {
        val ID: Identifier = Identifier("error")
        fun read(buf: PacketByteBuf): ErrorCraftingMonitorElement {
            val id: Identifier = buf.readIdentifier()
            val message = PacketByteBufUtils.readString(buf)
            return ErrorCraftingMonitorElement(
                    instance().getCraftingMonitorElementRegistry()!![id]!!.apply(buf), // TODO Seems unsafe
                    message
            )
        }
    }
}