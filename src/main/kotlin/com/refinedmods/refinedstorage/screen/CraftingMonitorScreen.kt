package com.refinedmods.refinedstorage.screen

import com.google.common.collect.Lists
import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingRequestInfo
import com.refinedmods.refinedstorage.api.network.grid.IGridTab
import com.refinedmods.refinedstorage.api.render.IElementDrawer
import com.refinedmods.refinedstorage.api.render.IElementDrawers
import com.refinedmods.refinedstorage.api.util.IFilter
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.apiimpl.render.CraftingMonitorElementDrawers
import com.refinedmods.refinedstorage.apiimpl.render.ElementDrawers
import com.refinedmods.refinedstorage.container.CraftingMonitorContainer
import com.refinedmods.refinedstorage.network.craftingmonitor.CraftingMonitorCancelMessage
import com.refinedmods.refinedstorage.screen.widget.ScrollbarWidget
import com.refinedmods.refinedstorage.screen.widget.TabListWidget
import com.refinedmods.refinedstorage.screen.widget.TabListWidget.ITabListListener
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton
import com.refinedmods.refinedstorage.tile.craftingmonitor.ICraftingMonitor
import com.refinedmods.refinedstorage.util.RenderUtils
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.widget.button.Button
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.text.Text
import net.minecraft.util.text.TranslationTextComponent
import net.minecraftforge.fluids.FluidInstance
import java.util.*
import java.util.function.Supplier

class CraftingMonitorScreen(container: CraftingMonitorContainer, inventory: PlayerInventory?, title: Text?) : BaseScreen<CraftingMonitorContainer?>(container, 254, 201, inventory, title) {
    class Task(val id: UUID, private val requested: ICraftingRequestInfo, private val qty: Int, private val executionStarted: Long, private val completionPercentage: Int, val elements: List<ICraftingMonitorElement>) : IGridTab {
        override fun drawTooltip(matrixStack: MatrixStack?, x: Int, y: Int, screenWidth: Int, screenHeight: Int, fontRenderer: FontRenderer?) {
            val textLines: List<Text> = Lists.newArrayList(if (requested.item != null) requested.item.getDisplayName() else requested.fluid.getDisplayName())
            val smallTextLines: MutableList<String> = Lists.newArrayList()
            val totalSecs = (System.currentTimeMillis() - executionStarted).toInt() / 1000
            val minutes = totalSecs % 3600 / 60
            val seconds = totalSecs % 60
            smallTextLines.add(I18n.format("gui.refinedstorage.crafting_monitor.tooltip.requested", if (requested.fluid != null) instance().getQuantityFormatter()!!.formatInBucketForm(qty) else instance().getQuantityFormatter()!!.format(qty)))
            smallTextLines.add(String.format("%02d:%02d", minutes, seconds))
            smallTextLines.add(String.format("%d%%", completionPercentage))
            RenderUtils.drawTooltipWithSmallText(matrixStack, textLines, smallTextLines, true, ItemStack.EMPTY, x, y, screenWidth, screenHeight, fontRenderer)
        }

        override fun getFilters(): List<IFilter<*>>? {
            return null
        }

        fun drawIcon(matrixStack: MatrixStack?, x: Int, y: Int, itemDrawer: IElementDrawer<ItemStack?>?, fluidDrawer: IElementDrawer<FluidInstance?>?) {
            if (requested.item != null) {
                RenderHelper.setupGui3DDiffuseLighting()
                itemDrawer!!.draw(matrixStack, x, y, requested.item)
            } else {
                fluidDrawer!!.draw(matrixStack, x, y, requested.fluid)
                RenderSystem.enableAlphaTest()
            }
        }
    }

    private var cancelButton: Button? = null
    private var cancelAllButton: Button? = null
    private val scrollbar: ScrollbarWidget?
    private val craftingMonitor: ICraftingMonitor
    private var tasks: List<IGridTab?> = emptyList()
    private val tabs: TabListWidget
    private val drawers: IElementDrawers = CraftingMonitorElementDrawers(this, font, ITEM_WIDTH, ITEM_HEIGHT)
    fun setTasks(tasks: List<IGridTab?>) {
        this.tasks = tasks
    }

    fun getElements(): List<ICraftingMonitorElement> {
        if (!craftingMonitor.isActiveOnClient) {
            return emptyList()
        }
        val tab = getCurrentTab() ?: return emptyList()
        return (tab as Task).elements
    }

    override fun onPostInit(x: Int, y: Int) {
        tabs.init(xSize)
        if (craftingMonitor.redstoneModeParameter != null) {
            addSideButton(RedstoneModeSideButton(this, craftingMonitor.redstoneModeParameter))
        }
        val cancel: Text = TranslationTextComponent("gui.cancel")
        val cancelAll: Text = TranslationTextComponent("misc.refinedstorage.cancel_all")
        val cancelButtonWidth: Int = 14 + font.getStringWidth(cancel.getString())
        val cancelAllButtonWidth: Int = 14 + font.getStringWidth(cancelAll.getString())
        cancelButton = addButton(x + 7, y + 201 - 20 - 7, cancelButtonWidth, 20, cancel, false, true, Button.IPressable({ btn ->
            if (hasValidTabSelected()) {
                RS.NETWORK_HANDLER.sendToServer(CraftingMonitorCancelMessage((getCurrentTab() as Task?)!!.id))
            }
        }))
        cancelAllButton = addButton(x + 7 + cancelButtonWidth + 4, y + 201 - 20 - 7, cancelAllButtonWidth, 20, cancelAll, false, true, Button.IPressable({ btn ->
            if (!tasks.isEmpty()) {
                RS.NETWORK_HANDLER.sendToServer(CraftingMonitorCancelMessage(null))
            }
        }))
    }

    private fun updateScrollbar() {
        if (scrollbar != null) {
            scrollbar.isEnabled = getRows() > ROWS
            scrollbar.setMaxOffset(getRows() - ROWS)
        }
    }

    private fun getRows(): Int {
        return Math.max(0, Math.ceil(getElements().size.toFloat() / 3f.toDouble()).toInt())
    }

    override fun tick(x: Int, y: Int) {
        updateScrollbar()
        tabs.update()
        if (cancelButton != null) {
            cancelButton.active = hasValidTabSelected()
        }
        if (cancelAllButton != null) {
            cancelAllButton.active = tasks.size > 0
        }
    }

    private fun hasValidTabSelected(): Boolean {
        return getCurrentTab() != null
    }

    @Nullable
    private fun getCurrentTab(): IGridTab? {
        val currentTab = craftingMonitor.tabSelected
        if (currentTab.isPresent) {
            val tab = getTabById(currentTab.get())
            if (tab != null) {
                return tab
            }
        }
        return if (tasks.isEmpty()) {
            null
        } else tasks[0]
    }

    @Nullable
    private fun getTabById(id: UUID): IGridTab? {
        return tasks.stream().filter { t: IGridTab? -> (t as Task?)!!.id == id }.findFirst().orElse(null)
    }

    override fun renderBackground(matrixStack: MatrixStack?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        if (craftingMonitor.isActiveOnClient) {
            tabs.drawBackground(matrixStack, x, y - tabs.height)
        }
        bindTexture(RS.ID, "gui/crafting_preview.png")
        blit(matrixStack, x, y, 0, 0, xSize, ySize)
        scrollbar!!.render(matrixStack)
        tabs.drawForeground(matrixStack, x, y - tabs.height, mouseX, mouseY, craftingMonitor.isActiveOnClient)
    }

    override fun renderForeground(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int) {
        renderString(matrixStack, 7, 7, title.getString())
        var item = if (scrollbar != null) scrollbar.offset * 3 else 0
        RenderHelper.setupGui3DDiffuseLighting()
        var x = 7
        var y = 20
        var tooltip: List<Text?>? = null
        for (i in 0 until 3 * 5) {
            if (item < getElements().size) {
                val element = getElements()[item]
                element.draw(matrixStack, x, y, drawers)
                if (RenderUtils.inBounds(x, y, ITEM_WIDTH, ITEM_HEIGHT, mouseX.toDouble(), mouseY.toDouble())) {
                    tooltip = element.getTooltip()
                }
                if ((i + 1) % 3 == 0) {
                    x = 7
                    y += 30
                } else {
                    x += 74
                }
            }
            item++
        }
        if (tooltip != null && !tooltip.isEmpty()) {
            renderTooltip(matrixStack, ItemStack.EMPTY, mouseX, mouseY, tooltip)
        }
        tabs.drawTooltip(matrixStack, font, mouseX, mouseY)
    }

    fun mouseClicked(mouseX: Double, mouseY: Double, clickedButton: Int): Boolean {
        if (tabs.mouseClicked()) {
            return true
        }
        return if (scrollbar!!.mouseClicked(mouseX, mouseY, clickedButton)) {
            true
        } else super.mouseClicked(mouseX, mouseY, clickedButton)
    }

    fun mouseMoved(mx: Double, my: Double) {
        scrollbar!!.mouseMoved(mx, my)
        super.mouseMoved(mx, my)
    }

    fun mouseReleased(mx: Double, my: Double, button: Int): Boolean {
        return scrollbar!!.mouseReleased(mx, my, button) || super.mouseReleased(mx, my, button)
    }

    fun mouseScrolled(x: Double, y: Double, delta: Double): Boolean {
        return scrollbar!!.mouseScrolled(x, y, delta) || super.mouseScrolled(x, y, delta)
    }

    companion object {
        private const val ROWS = 5
        private const val ITEM_WIDTH = 73
        private const val ITEM_HEIGHT = 29
    }

    init {
        craftingMonitor = container.craftingMonitor
        tabs = TabListWidget(this, ElementDrawers(this, font), Supplier { tasks }, Supplier { Math.floor(Math.max(0, tasks.size - 1).toFloat() / ICraftingMonitor.TABS_PER_PAGE as Float.toDouble()).toInt() }, Supplier { craftingMonitor.tabPage }, label@ Supplier {
            val tab = getCurrentTab() ?: return@label -1
            tasks.indexOf(tab)
        }, ICraftingMonitor.TABS_PER_PAGE)
        tabs.addListener(object : ITabListListener {
            override fun onSelectionChanged(tab: Int) {
                craftingMonitor.onTabSelectionChanged(Optional.of((tasks[tab] as Task?)!!.id))
                scrollbar!!.offset = 0
            }

            override fun onPageChanged(page: Int) {
                craftingMonitor.onTabPageChanged(page)
            }
        })
        scrollbar = ScrollbarWidget(this, 235, 20, 12, 149)
    }
}