package com.refinedmods.refinedstorage.apiimpl.autocrafting.preview

import com.mojang.blaze3d.systems.RenderSystem
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement
import com.refinedmods.refinedstorage.api.render.IElementDrawers
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.util.RenderUtils
import net.minecraft.client.MinecraftClient
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import reborncore.common.fluid.container.FluidInstance

class FluidCraftingPreviewElement(
        _stack: FluidInstance,
        override var available: Int = 0,
        override var missing: Boolean = false,
        override var toCraft: Int = 0,
        private val stack: FluidInstance = _stack.copy()
) : ICraftingPreviewElement<FluidInstance> {
    override fun write(buf: PacketByteBuf) {
        buf.writeCompoundTag(stack.tag)
        buf.writeInt(available)
        buf.writeBoolean(missing)
        buf.writeInt(toCraft)
    }

    override fun getElement(): FluidInstance = stack


//    @OnlyIn(Dist.CLIENT) // TODO Limit Access
    override fun draw(matrixStack: MatrixStack, x: Int, y: Int, drawers: IElementDrawers) {
        var tx = x
        var ty = y
        if (missing) {
            drawers.overlayDrawer.draw(matrixStack, tx, ty, -0xd2122)
        }
        tx += 5
        ty += 7
        drawers.fluidDrawer.draw(matrixStack, tx, ty, getElement())
        val scale = if (MinecraftClient.getInstance().forcesUnicodeFont()) 1f else 0.5f
        ty += 2
        RenderSystem.pushMatrix()
        RenderSystem.scalef(scale, scale, 1f)
        if (toCraft > 0) {
            val format = if (missing) "gui.refinedstorage.crafting_preview.missing" else "gui.refinedstorage.crafting_preview.to_craft"
            drawers.stringDrawer.draw(matrixStack, RenderUtils.getOffsetOnScale(tx + 23, scale), RenderUtils.getOffsetOnScale(ty, scale), I18n.translate(format, API.instance().getQuantityFormatter()!!.formatInBucketForm(toCraft)))
            ty += 7
        }
        if (available > 0) {
            drawers.stringDrawer.draw(matrixStack, RenderUtils.getOffsetOnScale(tx + 23, scale), RenderUtils.getOffsetOnScale(ty, scale), I18n.translate("gui.refinedstorage.crafting_preview.available", API.instance().getQuantityFormatter()!!.formatInBucketForm(available)))
        }
        RenderSystem.popMatrix()
    }

    fun addAvailable(amount: Int) {
        available += amount
    }

    fun addToCraft(amount: Int) {
        toCraft += amount
    }

    override fun getId(): Identifier = ID

    companion object {
        val ID: Identifier = Identifier(RS.ID, "fluid")
        fun read(buf: PacketByteBuf): FluidCraftingPreviewElement {
            val stack = FluidInstance()
            stack.read(buf.readCompoundTag()!!)
            val available: Int = buf.readInt()
            val missing: Boolean = buf.readBoolean()
            val toCraft: Int = buf.readInt()
            return FluidCraftingPreviewElement(stack, available, missing, toCraft)
        }
    }
}