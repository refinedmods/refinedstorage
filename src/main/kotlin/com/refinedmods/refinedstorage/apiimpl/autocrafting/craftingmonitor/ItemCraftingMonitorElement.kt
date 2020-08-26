package com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor

import com.mojang.blaze3d.systems.RenderSystem
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement
import com.refinedmods.refinedstorage.api.render.IElementDrawers
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.util.RenderUtils
import com.refinedmods.refinedstorage.util.StackUtils
import net.minecraft.client.MinecraftClient
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class ItemCraftingMonitorElement(
        private val stack: ItemStack,
        private var stored: Int,
        private var missing: Int,
        private var processing: Int,
        private var scheduled: Int,
        private var crafting: Int
) : ICraftingMonitorElement {

//    @OnlyIn(Dist.CLIENT) // TODO Limit access
    override fun draw(matrixStack: MatrixStack, x: Int, y: Int, drawers: IElementDrawers) {
        when {
            missing > 0 -> drawers.overlayDrawer.draw(matrixStack, x, y, COLOR_MISSING)
            processing > 0 -> drawers.overlayDrawer.draw(matrixStack, x, y, COLOR_PROCESSING)
            scheduled > 0 -> drawers.overlayDrawer.draw(matrixStack, x, y, COLOR_SCHEDULED)
            crafting > 0 -> drawers.overlayDrawer.draw(matrixStack, x, y, COLOR_CRAFTING)
        }
        drawers.itemDrawer.draw(matrixStack, x + 4, y + 6, stack)
        val scale = if (MinecraftClient.getInstance().forcesUnicodeFont()) 1f else 0.5f
        RenderSystem.pushMatrix()
        RenderSystem.scalef(scale, scale, 1f)
        var yy = y + 7
        if (stored > 0) {
            drawers.stringDrawer.draw(matrixStack, RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy, scale), I18n.translate("gui.refinedstorage.crafting_monitor.stored", stored))
            yy += 7
        }
        if (missing > 0) {
            drawers.stringDrawer.draw(matrixStack, RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy, scale), I18n.translate("gui.refinedstorage.crafting_monitor.missing", missing))
            yy += 7
        }
        if (processing > 0) {
            drawers.stringDrawer.draw(matrixStack, RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy, scale), I18n.translate("gui.refinedstorage.crafting_monitor.processing", processing))
            yy += 7
        }
        if (scheduled > 0) {
            drawers.stringDrawer.draw(matrixStack, RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy, scale), I18n.translate("gui.refinedstorage.crafting_monitor.scheduled", scheduled))
            yy += 7
        }
        if (crafting > 0) {
            drawers.stringDrawer.draw(matrixStack, RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy, scale), I18n.translate("gui.refinedstorage.crafting_monitor.crafting", crafting))
        }
        RenderSystem.popMatrix()
    }


    override fun getBaseId(): Identifier = ID
    override fun getId(): Identifier = ID
    override fun getTooltip(): List<Text> = RenderUtils.getTooltipFromItem(stack)


    override fun write(buf: PacketByteBuf) {
        StackUtils.writeItemStack(buf, stack)
        buf.writeInt(stored)
        buf.writeInt(missing)
        buf.writeInt(processing)
        buf.writeInt(scheduled)
        buf.writeInt(crafting)
    }

    override fun merge(element: ICraftingMonitorElement): Boolean {
        if (element.getId() == getId() && elementHashCode() == element.elementHashCode()) {
            stored += (element as ItemCraftingMonitorElement).stored
            missing += element.missing
            processing += element.processing
            scheduled += element.scheduled
            crafting += element.crafting
            return true
        }
        return false
    }

    override fun baseElementHashCode(): Int {
        return elementHashCode()
    }

    override fun elementHashCode(): Int {
        return API.instance().getItemStackHashCode(stack)
    }

    class Builder(private val stack: ItemStack) {
        private var stored = 0
        private var missing = 0
        private var processing = 0
        private var scheduled = 0
        private var crafting = 0
        fun stored(stored: Int): Builder {
            this.stored = stored
            return this
        }

        fun missing(missing: Int): Builder {
            this.missing = missing
            return this
        }

        fun processing(processing: Int): Builder {
            this.processing = processing
            return this
        }

        fun scheduled(scheduled: Int): Builder {
            this.scheduled = scheduled
            return this
        }

        fun crafting(crafting: Int): Builder {
            this.crafting = crafting
            return this
        }

        fun build(): ItemCraftingMonitorElement {
            return ItemCraftingMonitorElement(stack, stored, missing, processing, scheduled, crafting)
        }

        companion object {
            fun forStack(stack: ItemStack): Builder {
                return Builder(stack)
            }
        }

    }

    companion object {
        private const val COLOR_PROCESSING = -0x261209
        private const val COLOR_MISSING = -0xd2122
        private const val COLOR_SCHEDULED = -0x171a36
        private const val COLOR_CRAFTING = -0x52243a
        val ID: Identifier = Identifier(RS.ID, "item")
        fun read(buf: PacketByteBuf): ItemCraftingMonitorElement {
            return ItemCraftingMonitorElement(
                    StackUtils.readItemStack(buf),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt()
            )
        }
    }
}