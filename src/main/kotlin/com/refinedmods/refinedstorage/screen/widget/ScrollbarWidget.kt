package com.refinedmods.refinedstorage.screen.widget

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.integration.jei.GridRecipeTransferHandler
import com.refinedmods.refinedstorage.integration.jei.JeiIntegration
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.util.RenderUtils
import net.minecraft.client.gui.IGuiEventListener
import java.util.*
import java.util.function.Consumer

class ScrollbarWidget(private val screen: BaseScreen<*>, private val x: Int, private val y: Int, private val width: Int, private val height: Int) : IGuiEventListener {
    private var enabled = false
    private var offset = 0
    private var maxOffset = 0
    private var clicked = false
    private val listeners: MutableList<ScrollbarWidgetListener> = LinkedList()
    fun addListener(listener: ScrollbarWidgetListener) {
        listeners.add(listener)
    }

    fun getWidth(): Int {
        return width
    }

    fun getHeight(): Int {
        return height
    }

    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }

    fun isEnabled(): Boolean {
        return enabled
    }

    fun render(matrixStack: MatrixStack?) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        screen.bindTexture(RS.ID, "icons.png")
        screen.blit(matrixStack, screen.getGuiLeft() + x, screen.getGuiTop() + y + Math.min(height - SCROLLER_HEIGHT.toFloat(), offset.toFloat() / maxOffset.toFloat() * (height - SCROLLER_HEIGHT).toFloat()).toInt(), if (isEnabled()) 232 else 244, 0, 12, 15)
    }

    fun mouseClicked(mx: Double, my: Double, button: Int): Boolean {
        var mx = mx
        var my = my
        mx -= screen.getGuiLeft()
        my -= screen.getGuiTop()
        if (button == 0 && RenderUtils.inBounds(x, y, width, height, mx, my)) {
            // Prevent accidental scrollbar click after clicking recipe transfer button
            if (JeiIntegration.isLoaded && System.currentTimeMillis() - GridRecipeTransferHandler.LAST_TRANSFER_TIME <= GridRecipeTransferHandler.TRANSFER_SCROLLBAR_DELAY_MS) {
                return false
            }
            updateOffset(my)
            clicked = true
            return true
        }
        return false
    }

    fun mouseMoved(mx: Double, my: Double) {
        var mx = mx
        var my = my
        mx -= screen.getGuiLeft()
        my -= screen.getGuiTop()
        if (clicked && RenderUtils.inBounds(x, y, width, height, mx, my)) {
            updateOffset(my)
        }
    }

    private fun updateOffset(my: Double) {
        setOffset(Math.floor((my - y).toFloat() / (height - SCROLLER_HEIGHT).toFloat() * maxOffset as Float.toDouble()).toInt())
    }

    fun mouseReleased(mx: Double, my: Double, button: Int): Boolean {
        if (clicked) {
            clicked = false
            return true
        }
        return false
    }

    fun mouseScrolled(mouseX: Double, mouseY: Double, scrollDelta: Double): Boolean {
        if (isEnabled()) {
            setOffset(offset + Math.max(Math.min((-scrollDelta).toInt(), 1), -1))
            return true
        }
        return false
    }

    fun setMaxOffset(maxOffset: Int) {
        this.maxOffset = maxOffset
        if (offset > maxOffset) {
            offset = Math.max(0, maxOffset)
        }
    }

    fun getOffset(): Int {
        return offset
    }

    fun setOffset(offset: Int) {
        val oldOffset = this.offset
        if (offset >= 0 && offset <= maxOffset) {
            this.offset = offset
            listeners.forEach(Consumer { l: ScrollbarWidgetListener -> l.onOffsetChanged(oldOffset, offset) })
        }
    }

    companion object {
        private const val SCROLLER_HEIGHT = 15
    }
}