package com.refinedmods.refinedstorage.screen.widget

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.network.grid.IGridTab
import com.refinedmods.refinedstorage.apiimpl.render.ElementDrawers
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.util.RenderUtils
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.widget.button.Button
import net.minecraft.util.text.StringTextComponent
import java.util.*
import java.util.function.Consumer
import java.util.function.Supplier

class TabListWidget(private val screen: BaseScreen<*>, private val drawers: ElementDrawers, private val tabs: Supplier<List<IGridTab?>>, private val pages: Supplier<Int>, private val page: Supplier<Int>, private val selected: Supplier<Int>, private val tabsPerPage: Int) {
    interface ITabListListener {
        fun onSelectionChanged(tab: Int)
        fun onPageChanged(page: Int)
    }

    private var tabHovering = 0
    private var hadTabs = false
    private val listeners: MutableList<ITabListListener> = LinkedList()
    private var left: Button? = null
    private var right: Button? = null
    fun init(width: Int) {
        left = screen.addButton(screen.getGuiLeft(), screen.getGuiTop() - 22, 20, 20, StringTextComponent("<"), true, pages.get() > 0, Button.IPressable({ btn -> listeners.forEach(Consumer { t: ITabListListener -> t.onPageChanged(page.get() - 1) }) }))
        right = screen.addButton(screen.getGuiLeft() + width - 22, screen.getGuiTop() - 22, 20, 20, StringTextComponent(">"), true, pages.get() > 0, Button.IPressable({ btn -> listeners.forEach(Consumer { t: ITabListListener -> t.onPageChanged(page.get() + 1) }) }))
    }

    fun addListener(listener: ITabListListener) {
        listeners.add(listener)
    }

    fun drawForeground(matrixStack: MatrixStack?, x: Int, y: Int, mouseX: Int, mouseY: Int, visible: Boolean) {
        tabHovering = -1
        if (visible) {
            var j = 0
            for (i in page.get() * tabsPerPage until page.get() * tabsPerPage + tabsPerPage) {
                if (i < tabs.get().size) {
                    drawTab(matrixStack, tabs.get()[i], true, x, y, i, j)
                    if (RenderUtils.inBounds(x + getXOffset() + (IGridTab.TAB_WIDTH + 1) * j, y, IGridTab.TAB_WIDTH, IGridTab.TAB_HEIGHT - if (i == selected.get()) 2 else 7, mouseX.toDouble(), mouseY.toDouble())) {
                        tabHovering = i
                    }
                    j++
                }
            }
        }
    }

    fun update() {
        val hasTabs = !tabs.get().isEmpty()
        if (hadTabs != hasTabs) {
            hadTabs = hasTabs
            screen.init()
        }
        if (page.get() > pages.get()) {
            listeners.forEach(Consumer { t: ITabListListener -> t.onPageChanged(pages.get()) })
        }
        left.visible = pages.get() > 0
        right.visible = pages.get() > 0
        left.active = page.get() > 0
        right.active = page.get() < pages.get()
    }

    fun drawBackground(matrixStack: MatrixStack?, x: Int, y: Int) {
        var j = 0
        for (i in page.get() * tabsPerPage until page.get() * tabsPerPage + tabsPerPage) {
            if (i < tabs.get().size) {
                drawTab(matrixStack, tabs.get()[i], false, x, y, i, j++)
            }
        }
    }

    fun getHeight(): Int {
        return if (!tabs.get().isEmpty()) IGridTab.TAB_HEIGHT - 4 else 0
    }

    private fun getXOffset(): Int {
        return if (pages.get() > 0) {
            24
        } else 0
    }

    private fun drawTab(matrixStack: MatrixStack?, tab: IGridTab?, foregroundLayer: Boolean, x: Int, y: Int, index: Int, num: Int) {
        val isSelected = index == selected.get()
        if (foregroundLayer && !isSelected || !foregroundLayer && isSelected) {
            return
        }
        var tx = x + getXOffset() + (IGridTab.TAB_WIDTH + 1) * num
        var ty = y
        RenderSystem.enableAlphaTest()
        screen.bindTexture(RS.ID, "icons.png")
        if (!isSelected) {
            ty += 3
        }
        var uvx: Int
        var uvy = 225
        var tbw = IGridTab.TAB_WIDTH
        val otx = tx
        if (isSelected) {
            uvx = 227
            if (num > 0 || getXOffset() != 0) {
                uvx = 226
                uvy = 194
                tbw++
                tx--
            }
        } else {
            uvx = 199
        }
        screen.blit(matrixStack, tx, ty, uvx, uvy, tbw, IGridTab.TAB_HEIGHT)
        tab!!.drawIcon(matrixStack, otx + 6, ty + 9 - if (!isSelected) 3 else 0, drawers.itemDrawer, drawers.fluidDrawer)
    }

    fun drawTooltip(matrixStack: MatrixStack?, fontRenderer: FontRenderer?, mouseX: Int, mouseY: Int) {
        if (tabHovering >= 0 && tabHovering < tabs.get().size) {
            tabs.get()[tabHovering]!!.drawTooltip(matrixStack, mouseX, mouseY, screen.width, screen.height, fontRenderer)
        }
    }

    fun mouseClicked(): Boolean {
        if (tabHovering >= 0 && tabHovering < tabs.get().size) {
            listeners.forEach(Consumer { t: ITabListListener -> t.onSelectionChanged(tabHovering) })
            return true
        }
        return false
    }
}