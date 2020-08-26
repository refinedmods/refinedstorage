package com.refinedmods.refinedstorage.apiimpl.autocrafting.preview

import com.mojang.blaze3d.systems.RenderSystem
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement
import com.refinedmods.refinedstorage.api.render.IElementDrawers
import com.refinedmods.refinedstorage.util.RenderUtils
import net.minecraft.client.MinecraftClient
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class ItemCraftingPreviewElement(
        stack: ItemStack,
        override var available: Int = 0,
        override var missing: Boolean = false,
        override var toCraft: Int = 0
): ICraftingPreviewElement<ItemStack> {
    private val stack: ItemStack = stack.copy()

    init {
        this.stack.count = 1
    }

    override fun write(buf: PacketByteBuf) {
        buf.writeItemStack(stack)
        buf.writeInt(available)
        buf.writeBoolean(missing)
        buf.writeInt(toCraft)
    }

    override fun getElement(): ItemStack = stack

//    @OnlyIn(Dist.CLIENT) // TODO Limit Access
    override fun draw(matrixStack: MatrixStack, _x: Int, _y: Int, drawers: IElementDrawers) {
        var tx = _x
        var ty = _y
        if (missing) {
            drawers.overlayDrawer.draw(matrixStack, tx, ty, -0xd2122)
        }
        tx += 5
        ty += 7
        drawers.itemDrawer.draw(matrixStack, tx, ty, getElement())
        val scale = if (MinecraftClient.getInstance().forcesUnicodeFont()) 1f else 0.5f
        ty += 2
        RenderSystem.pushMatrix()
        RenderSystem.scalef(scale, scale, 1f)
        if (toCraft > 0) {
            val format = if (missing) "gui.refinedstorage.crafting_preview.missing" else "gui.refinedstorage.crafting_preview.to_craft"
            drawers.stringDrawer.draw(matrixStack, RenderUtils.getOffsetOnScale(tx + 23, scale), RenderUtils.getOffsetOnScale(ty, scale), I18n.translate(format, toCraft))
            ty += 7
        }
        if (available > 0) {
            drawers.stringDrawer.draw(matrixStack, RenderUtils.getOffsetOnScale(tx + 23, scale), RenderUtils.getOffsetOnScale(ty, scale), I18n.translate("gui.refinedstorage.crafting_preview.available", available))
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
        val ID: Identifier = Identifier(RS.ID, "item")
        fun read(buf: PacketByteBuf): ItemCraftingPreviewElement {
            val stack: ItemStack = buf.readItemStack()
            val available: Int = buf.readInt()
            val missing: Boolean = buf.readBoolean()
            val toCraft: Int = buf.readInt()
            return ItemCraftingPreviewElement(stack, available, missing, toCraft)
        }
    }
}