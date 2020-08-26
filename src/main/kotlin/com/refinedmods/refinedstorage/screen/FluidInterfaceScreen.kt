package com.refinedmods.refinedstorage.screen

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.apiimpl.network.node.FluidInterfaceNetworkNode
import com.refinedmods.refinedstorage.container.FluidInterfaceContainer
import com.refinedmods.refinedstorage.render.FluidRenderer
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton
import com.refinedmods.refinedstorage.tile.FluidInterfaceTile
import com.refinedmods.refinedstorage.util.RenderUtils
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.Text
import net.minecraft.util.text.TextFormatting

class FluidInterfaceScreen(container: FluidInterfaceContainer, inventory: PlayerInventory?, title: Text?) : BaseScreen<FluidInterfaceContainer?>(container, 211, 204, inventory, title) {
    override fun onPostInit(x: Int, y: Int) {
        addSideButton(RedstoneModeSideButton(this, FluidInterfaceTile.REDSTONE_MODE))
    }

    override fun tick(x: Int, y: Int) {}
    override fun renderBackground(matrixStack: MatrixStack?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        bindTexture(RS.ID, "gui/fluid_interface.png")
        blit(matrixStack, x, y, 0, 0, xSize, ySize)
        if (!FluidInterfaceTile.TANK_IN.value.isEmpty()) {
            TANK_RENDERER.render(matrixStack, x + 46, y + 56, FluidInterfaceTile.TANK_IN.value)
        }
        if (!FluidInterfaceTile.TANK_OUT.value.isEmpty()) {
            TANK_RENDERER.render(matrixStack, x + 118, y + 56, FluidInterfaceTile.TANK_OUT.value)
        }
    }

    override fun renderForeground(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int) {
        renderString(matrixStack, 7, 7, title.getString())
        renderString(matrixStack, 43 + 4, 20, I18n.format("gui.refinedstorage.fluid_interface.in"))
        renderString(matrixStack, 115 + 1, 20, I18n.format("gui.refinedstorage.fluid_interface.out"))
        renderString(matrixStack, 7, 111, I18n.format("container.inventory"))
        if (RenderUtils.inBounds(46, 56, 12, 47, mouseX.toDouble(), mouseY.toDouble()) && !FluidInterfaceTile.TANK_IN.value.isEmpty()) {
            renderTooltip(matrixStack, mouseX, mouseY, FluidInterfaceTile.TANK_IN.value.getDisplayName().getString().toString() + "\n" + TextFormatting.GRAY + instance().getQuantityFormatter()!!.formatInBucketForm(FluidInterfaceTile.TANK_IN.value.getAmount()) + TextFormatting.RESET)
        }
        if (RenderUtils.inBounds(118, 56, 12, 47, mouseX.toDouble(), mouseY.toDouble()) && !FluidInterfaceTile.TANK_OUT.value.isEmpty()) {
            renderTooltip(matrixStack, mouseX, mouseY, FluidInterfaceTile.TANK_OUT.value.getDisplayName().getString().toString() + "\n" + TextFormatting.GRAY + instance().getQuantityFormatter()!!.formatInBucketForm(FluidInterfaceTile.TANK_OUT.value.getAmount()) + TextFormatting.RESET)
        }
    }

    companion object {
        private val TANK_RENDERER = FluidRenderer(FluidInterfaceNetworkNode.TANK_CAPACITY, 12, 47, 1)
    }
}