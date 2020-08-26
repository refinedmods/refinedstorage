package com.refinedmods.refinedstorage.screen

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.apiimpl.network.Network.Companion.getEnergyScaled
import com.refinedmods.refinedstorage.container.ControllerContainer
import com.refinedmods.refinedstorage.screen.widget.ScrollbarWidget
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton
import com.refinedmods.refinedstorage.tile.ClientNode
import com.refinedmods.refinedstorage.tile.ControllerTile
import com.refinedmods.refinedstorage.util.RenderUtils
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.Text

class ControllerScreen(container: ControllerContainer, inventory: PlayerInventory?, title: Text?) : BaseScreen<ControllerContainer?>(container, 176, 181, inventory, title) {
    private val scrollbar: ScrollbarWidget
    override fun onPostInit(x: Int, y: Int) {
        addSideButton(RedstoneModeSideButton(this, ControllerTile.REDSTONE_MODE))
    }

    override fun tick(x: Int, y: Int) {
        scrollbar.isEnabled = getRows() > VISIBLE_ROWS
        scrollbar.setMaxOffset(getRows() - VISIBLE_ROWS)
    }

    override fun renderBackground(matrixStack: MatrixStack?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        bindTexture(RS.ID, "gui/controller.png")
        blit(matrixStack, x, y, 0, 0, xSize, ySize)
        val energyBarHeightNew = getEnergyScaled(ControllerTile.ENERGY_STORED.value, ControllerTile.ENERGY_CAPACITY.value, ENERGY_BAR_HEIGHT)
        blit(matrixStack, x + ENERGY_BAR_X, y + ENERGY_BAR_Y + ENERGY_BAR_HEIGHT - energyBarHeightNew, 178, ENERGY_BAR_HEIGHT - energyBarHeightNew, ENERGY_BAR_WIDTH, energyBarHeightNew)
        scrollbar.render(matrixStack)
    }

    fun mouseMoved(mx: Double, my: Double) {
        scrollbar.mouseMoved(mx, my)
        super.mouseMoved(mx, my)
    }

    fun mouseClicked(mx: Double, my: Double, button: Int): Boolean {
        return scrollbar.mouseClicked(mx, my, button) || super.mouseClicked(mx, my, button)
    }

    fun mouseReleased(mx: Double, my: Double, button: Int): Boolean {
        return scrollbar.mouseReleased(mx, my, button) || super.mouseReleased(mx, my, button)
    }

    fun mouseScrolled(x: Double, y: Double, delta: Double): Boolean {
        return scrollbar.mouseScrolled(x, y, delta) || super.mouseScrolled(x, y, delta)
    }

    override fun renderForeground(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int) {
        renderString(matrixStack, 7, 7, title.getString())
        renderString(matrixStack, 7, 87, I18n.format("container.inventory"))
        var x = 33
        var y = 26
        var slot = scrollbar.offset * 2
        RenderHelper.setupGui3DDiffuseLighting()
        val nodes = ControllerTile.NODES.value
        var hoveringNode: ClientNode? = null
        for (i in 0..3) {
            if (slot < nodes.size) {
                val node = nodes[slot]
                renderItem(matrixStack, x, y + 5, node.stack)
                val scale = if (minecraft.getForceUnicodeFont()) 1f else 0.5f
                RenderSystem.pushMatrix()
                RenderSystem.scalef(scale, scale, 1f)
                renderString(
                        matrixStack,
                        RenderUtils.getOffsetOnScale(x + 1, scale),
                        RenderUtils.getOffsetOnScale(y - 2, scale),
                        trimNameIfNeeded(!minecraft.getForceUnicodeFont(), node.stack.getDisplayName().getString())
                )
                renderString(matrixStack, RenderUtils.getOffsetOnScale(x + 21, scale), RenderUtils.getOffsetOnScale(y + 10, scale), node.amount.toString() + "x")
                RenderSystem.popMatrix()
                if (RenderUtils.inBounds(x, y, 16, 16, mouseX.toDouble(), mouseY.toDouble())) {
                    hoveringNode = node
                }
            }
            if (i == 1) {
                x = 33
                y += 30
            } else {
                x += 60
            }
            slot++
        }
        if (hoveringNode != null) {
            renderTooltip(matrixStack, mouseX, mouseY, I18n.format("misc.refinedstorage.energy_usage_minimal", hoveringNode.energyUsage))
        }
        if (RenderUtils.inBounds(ENERGY_BAR_X, ENERGY_BAR_Y, ENERGY_BAR_WIDTH, ENERGY_BAR_HEIGHT, mouseX.toDouble(), mouseY.toDouble())) {
            renderTooltip(matrixStack, mouseX, mouseY, I18n.format("misc.refinedstorage.energy_usage", ControllerTile.ENERGY_USAGE.value).toString() + "\n" + I18n.format("misc.refinedstorage.energy_stored", ControllerTile.ENERGY_STORED.value, ControllerTile.ENERGY_CAPACITY.value))
        }
    }

    private fun getRows(): Int {
        return Math.max(0, Math.ceil(ControllerTile.NODES.value.size.toFloat() / 2f.toDouble()).toInt())
    }

    private fun trimNameIfNeeded(scaled: Boolean, name: String): String {
        var name = name
        val max = if (scaled) 20 else 13
        if (name.length > max) {
            name = name.substring(0, max) + "..."
        }
        return name
    }

    companion object {
        private const val VISIBLE_ROWS = 2
        private const val ENERGY_BAR_X = 8
        private const val ENERGY_BAR_Y = 20
        private const val ENERGY_BAR_WIDTH = 16
        private const val ENERGY_BAR_HEIGHT = 59
    }

    init {
        scrollbar = ScrollbarWidget(this, 157, 20, 12, 59)
    }
}