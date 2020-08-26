package com.refinedmods.refinedstorage.screen.grid

import com.google.common.collect.Lists
import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.RSKeyBindings
import com.refinedmods.refinedstorage.api.network.grid.GridType
import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.api.network.grid.handler.IItemGridHandler
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode
import com.refinedmods.refinedstorage.container.GridContainer
import com.refinedmods.refinedstorage.network.grid.*
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.screen.IScreenInfoProvider
import com.refinedmods.refinedstorage.screen.grid.sorting.*
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack
import com.refinedmods.refinedstorage.screen.grid.stack.ItemGridStack
import com.refinedmods.refinedstorage.screen.grid.view.FluidGridView
import com.refinedmods.refinedstorage.screen.grid.view.IGridView
import com.refinedmods.refinedstorage.screen.grid.view.ItemGridView
import com.refinedmods.refinedstorage.screen.widget.CheckboxWidget
import com.refinedmods.refinedstorage.screen.widget.ScrollbarWidget
import com.refinedmods.refinedstorage.screen.widget.SearchWidget
import com.refinedmods.refinedstorage.screen.widget.TabListWidget
import com.refinedmods.refinedstorage.screen.widget.TabListWidget.ITabListListener
import com.refinedmods.refinedstorage.screen.widget.sidebutton.*
import com.refinedmods.refinedstorage.tile.data.TileDataManager
import com.refinedmods.refinedstorage.tile.grid.GridTile
import com.refinedmods.refinedstorage.tile.grid.portable.IPortableGrid
import com.refinedmods.refinedstorage.tile.grid.portable.PortableGridTile
import com.refinedmods.refinedstorage.util.RenderUtils
import com.refinedmods.refinedstorage.util.TimeUtils
import net.minecraft.client.audio.SimpleSound
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.SoundEvents
import net.minecraft.util.text.Text
import net.minecraft.util.text.TranslationTextComponent
import org.lwjgl.glfw.GLFW
import java.util.*

class GridScreen(container: GridContainer, val grid: IGrid?, inventory: PlayerInventory?, title: Text?) : BaseScreen<GridContainer?>(container, 227, 0, inventory, title), IScreenInfoProvider {
    var view: IGridView
    var searchField: SearchWidget? = null
        private set
    private var exactPattern: CheckboxWidget? = null
    private var processingPattern: CheckboxWidget? = null
    private var scrollbar: ScrollbarWidget? = null
    private val tabs: TabListWidget
    private var wasConnected: Boolean
    private var doSort = false
    var slotNumber = 0
        private set

    override fun onPreInit() {
        super.onPreInit()
        doSort = true
        this.ySize = topHeight + bottomHeight + visibleRows * 18
    }

    override fun onPostInit(x: Int, y: Int) {
        this.container.initSlots()
        tabs.init(xSize - 32)
        scrollbar = ScrollbarWidget(this, 174, topHeight, 12, visibleRows * 18 - 2)
        if (grid is GridNetworkNode || grid is PortableGridTile) {
            addSideButton(RedstoneModeSideButton(this, if (grid is GridNetworkNode) GridTile.REDSTONE_MODE else PortableGridTile.REDSTONE_MODE))
        }
        val sx = x + 80 + 1
        val sy = y + 6 + 1
        if (searchField == null) {
            searchField = SearchWidget(font, sx, sy, 88 - 6)
            searchField.setResponder { value ->
                searchField!!.updateJei()
                view.sort() // Use getter since this view can be replaced.
            }
            searchField!!.setMode(grid!!.searchBoxMode)
        } else {
            searchField!!.x = sx
            searchField!!.y = sy
        }
        addButton(searchField)
        if (grid!!.viewType != -1) {
            addSideButton(GridViewTypeSideButton(this, grid))
        }
        addSideButton(GridSortingDirectionSideButton(this, grid))
        addSideButton(GridSortingTypeSideButton(this, grid))
        addSideButton(GridSearchBoxModeSideButton(this))
        addSideButton(GridSizeSideButton(this, { grid.size }) { size: Int? -> grid.onSizeChanged(size!!) })
        if (grid.gridType === GridType.PATTERN) {
            processingPattern = addCheckBox(x + 7, y + topHeight + visibleRows * 18 + 60, TranslationTextComponent("misc.refinedstorage.processing"), GridTile.PROCESSING_PATTERN.value) { btn: CheckboxButton? ->
                // Rebuild the inventory slots before the slot change packet arrives.
                GridTile.PROCESSING_PATTERN.setValue(false, processingPattern.isChecked())
                (grid as GridNetworkNode?)!!.clearMatrix() // The server does this but let's do it earlier so the client doesn't notice.
                this.container.initSlots()
                TileDataManager.setParameter(GridTile.PROCESSING_PATTERN, processingPattern.isChecked())
            }
            if (!processingPattern.isChecked()) {
                exactPattern = addCheckBox(
                        processingPattern.x + processingPattern.getWidth() + 5,
                        y + topHeight + visibleRows * 18 + 60,
                        TranslationTextComponent("misc.refinedstorage.exact"),
                        GridTile.EXACT_PATTERN.value
                ) { btn: CheckboxButton? -> TileDataManager.setParameter(GridTile.EXACT_PATTERN, exactPattern.isChecked()) }
            }
            addSideButton(TypeSideButton(this, GridTile.PROCESSING_TYPE))
        }
        updateScrollbar()
    }

    override fun tick(x: Int, y: Int) {
        if (wasConnected != grid!!.isGridActive) {
            wasConnected = grid.isGridActive
            view.sort()
        }
        if (BaseScreen.Companion.isKeyDown(RSKeyBindings.CLEAR_GRID_CRAFTING_MATRIX)) {
            RS.NETWORK_HANDLER.sendToServer(GridClearMessage())
        }
        tabs.update()
    }

    override val topHeight: Int
        get() = 19
    override val bottomHeight: Int
        get() = if (grid!!.gridType === GridType.CRAFTING) {
            156
        } else if (grid!!.gridType === GridType.PATTERN) {
            169
        } else {
            99
        }
    override val yPlayerInventory: Int
        get() {
            var yp = topHeight + visibleRows * 18
            if (grid!!.gridType === GridType.NORMAL || grid!!.gridType === GridType.FLUID) {
                yp += 16
            } else if (grid!!.gridType === GridType.CRAFTING) {
                yp += 73
            } else if (grid!!.gridType === GridType.PATTERN) {
                yp += 86
            }
            return yp
        }
    override val rows: Int
        get() = Math.max(0, Math.ceil(view.stacks.size.toFloat() / 9f.toDouble()).toInt())
    override val currentOffset: Int
        get() = scrollbar!!.offset
    override val searchFieldText: String?
        get() = searchField!!.text
    override val visibleRows: Int
        get() = when (grid!!.size) {
            IGrid.SIZE_STRETCH -> {
                val screenSpaceAvailable: Int = height - topHeight - bottomHeight
                Math.max(3, Math.min(screenSpaceAvailable / 18 - 3, RS.CLIENT_CONFIG.grid.getMaxRowsStretch()))
            }
            IGrid.SIZE_SMALL -> 3
            IGrid.SIZE_MEDIUM -> 5
            IGrid.SIZE_LARGE -> 8
            else -> 3
        }
    private val isOverSlotWithStack: Boolean
        private get() = grid!!.isGridActive && isOverSlot && slotNumber < view.stacks.size
    private val isOverSlot: Boolean
        private get() = slotNumber >= 0

    fun isOverSlotArea(mouseX: Double, mouseY: Double): Boolean {
        return RenderUtils.inBounds(7, 19, 162, 18 * visibleRows, mouseX, mouseY)
    }

    private fun isOverClear(mouseX: Double, mouseY: Double): Boolean {
        val y = topHeight + visibleRows * 18 + 4
        return when (grid!!.gridType) {
            GridType.CRAFTING -> RenderUtils.inBounds(82, y, 7, 7, mouseX, mouseY)
            GridType.PATTERN -> {
                if ((grid as GridNetworkNode?)!!.isProcessingPattern()) {
                    RenderUtils.inBounds(154, y, 7, 7, mouseX, mouseY)
                } else RenderUtils.inBounds(82, y, 7, 7, mouseX, mouseY)
            }
            else -> false
        }
    }

    private fun isOverCreatePattern(mouseX: Double, mouseY: Double): Boolean {
        return grid!!.gridType === GridType.PATTERN && RenderUtils.inBounds(172, topHeight + visibleRows * 18 + 22, 16, 16, mouseX, mouseY) && (grid as GridNetworkNode?)!!.canCreatePattern()
    }

    override fun renderBackground(matrixStack: MatrixStack?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        tabs.drawBackground(matrixStack, x, y - tabs.height)
        if (grid is IPortableGrid) {
            bindTexture(RS.ID, "gui/portable_grid.png")
        } else if (grid!!.gridType === GridType.CRAFTING) {
            bindTexture(RS.ID, "gui/crafting_grid.png")
        } else if (grid!!.gridType === GridType.PATTERN) {
            bindTexture(RS.ID, "gui/pattern_grid" + (if ((grid as GridNetworkNode?)!!.isProcessingPattern()) "_processing" else "") + ".png")
        } else {
            bindTexture(RS.ID, "gui/grid.png")
        }
        var yy = y
        blit(matrixStack, x, yy, 0, 0, xSize - 34, topHeight)

        // Filters and/or portable grid disk
        blit(matrixStack, x + xSize - 34 + 4, y, 197, 0, 30, if (grid is IPortableGrid) 114 else 82)
        val rows = visibleRows
        for (i in 0 until rows) {
            yy += 18
            blit(matrixStack, x, yy, 0, topHeight + if (i > 0) if (i == rows - 1) 18 * 2 else 18 else 0, xSize - 34, 18)
        }
        yy += 18
        blit(matrixStack, x, yy, 0, topHeight + 18 * 3, xSize - 34, bottomHeight)
        if (grid!!.gridType === GridType.PATTERN) {
            var ty = 0
            if (isOverCreatePattern(mouseX - guiLeft, mouseY - guiTop)) {
                ty = 1
            }
            if (!(grid as GridNetworkNode?)!!.canCreatePattern()) {
                ty = 2
            }
            blit(matrixStack, x + 172, y + topHeight + visibleRows * 18 + 22, 240, ty * 16, 16, 16)
        }
        tabs.drawForeground(matrixStack, x, y - tabs.height, mouseX, mouseY, true)
        searchField!!.render(matrixStack, 0, 0, 0f)
        scrollbar!!.render(matrixStack)
    }

    override fun render(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.render(matrixStack, mouseX, mouseY, partialTicks)

        // Drawn in here for bug #1844 (https://github.com/refinedmods/refinedstorage/issues/1844)
        // Item tooltips can't be rendered in the foreground layer due to the X offset translation.
        if (isOverSlotWithStack) {
            drawGridTooltip(matrixStack, view.stacks[slotNumber], mouseX, mouseY)
        }
    }

    override fun renderForeground(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int) {
        renderString(matrixStack, 7, 7, title.getString())
        renderString(matrixStack, 7, yPlayerInventory - 12, I18n.format("container.inventory"))
        var x = 8
        var y = 19
        slotNumber = -1
        var slot = if (scrollbar != null) scrollbar!!.offset * 9 else 0
        RenderHelper.setupGui3DDiffuseLighting()
        for (i in 0 until 9 * visibleRows) {
            if (RenderUtils.inBounds(x, y, 16, 16, mouseX.toDouble(), mouseY.toDouble()) || !grid!!.isGridActive) {
                slotNumber = slot
            }
            if (slot < view.stacks.size) {
                view.stacks[slot]!!.draw(matrixStack, this, x, y)
            }
            if (RenderUtils.inBounds(x, y, 16, 16, mouseX.toDouble(), mouseY.toDouble()) || !grid!!.isGridActive) {
                val color = if (grid!!.isGridActive) -2130706433 else -0xa4a4a5
                RenderSystem.pushMatrix()
                RenderSystem.disableLighting()
                RenderSystem.disableDepthTest()
                RenderSystem.colorMask(true, true, true, false)
                fillGradient(matrixStack, x, y, x + 16, y + 16, color, color)
                RenderSystem.colorMask(true, true, true, true)
                RenderSystem.popMatrix()
            }
            slot++
            x += 18
            if ((i + 1) % 9 == 0) {
                x = 8
                y += 18
            }
        }
        if (isOverClear(mouseX.toDouble(), mouseY.toDouble())) {
            renderTooltip(matrixStack, mouseX, mouseY, I18n.format("misc.refinedstorage.clear"))
        }
        if (isOverCreatePattern(mouseX.toDouble(), mouseY.toDouble())) {
            renderTooltip(matrixStack, mouseX, mouseY, I18n.format("gui.refinedstorage.grid.pattern_create"))
        }
        tabs.drawTooltip(matrixStack, font, mouseX, mouseY)
    }

    private fun drawGridTooltip(matrixStack: MatrixStack?, gridStack: IGridStack?, mouseX: Int, mouseY: Int) {
        val textLines: List<Text?>? = gridStack.getTooltip()
        val smallTextLines: MutableList<String> = Lists.newArrayList()
        if (!gridStack!!.isCraftable) {
            smallTextLines.add(I18n.format("misc.refinedstorage.total", gridStack.formattedFullQuantity))
        }
        if (gridStack.trackerEntry != null) {
            smallTextLines.add(TimeUtils.getAgo(gridStack.trackerEntry.getTime(), gridStack.trackerEntry.getName()))
        }
        val stack = if (gridStack is ItemGridStack) gridStack.stack else ItemStack.EMPTY
        RenderUtils.drawTooltipWithSmallText(matrixStack, textLines, smallTextLines, RS.CLIENT_CONFIG.grid.getDetailedTooltip(), stack, mouseX, mouseY, width, height, font)
    }

    fun mouseClicked(mouseX: Double, mouseY: Double, clickedButton: Int): Boolean {
        if (tabs.mouseClicked()) {
            return true
        }
        if (scrollbar!!.mouseClicked(mouseX, mouseY, clickedButton)) {
            return true
        }
        if (RS.CLIENT_CONFIG.grid.getPreventSortingWhileShiftIsDown()) {
            doSort = !isOverSlotArea(mouseX - guiLeft, mouseY - guiTop) && !isOverCraftingOutputArea(mouseX - guiLeft, mouseY - guiTop)
        }
        val clickedClear = clickedButton == 0 && isOverClear(mouseX - guiLeft, mouseY - guiTop)
        val clickedCreatePattern = clickedButton == 0 && isOverCreatePattern(mouseX - guiLeft, mouseY - guiTop)
        if (clickedCreatePattern) {
            minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
            RS.NETWORK_HANDLER.sendToServer(GridPatternCreateMessage((grid as GridNetworkNode?)!!.pos))
            return true
        } else if (grid!!.isGridActive) {
            if (clickedClear) {
                minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
                RS.NETWORK_HANDLER.sendToServer(GridClearMessage())
                return true
            }
            val held: ItemStack = container.getPlayer().inventory.getItemStack()
            if (isOverSlotArea(mouseX - guiLeft, mouseY - guiTop) && !held.isEmpty && (clickedButton == 0 || clickedButton == 1)) {
                if (grid.gridType === GridType.FLUID) {
                    RS.NETWORK_HANDLER.sendToServer(GridFluidInsertHeldMessage())
                } else {
                    RS.NETWORK_HANDLER.sendToServer(GridItemInsertHeldMessage(clickedButton == 1))
                }
                return true
            }
            if (isOverSlotWithStack) {
                val isMiddleClickPulling = !held.isEmpty && clickedButton == 2
                val isPulling = held.isEmpty || isMiddleClickPulling
                val stack = view.stacks[slotNumber]
                if (isPulling) {
                    if (view.canCraft() && stack!!.isCraftable) {
                        minecraft.displayGuiScreen(CraftingSettingsScreen(this, playerInventory.player, stack))
                    } else if (view.canCraft() && !stack!!.isCraftable && stack.otherId != null && hasShiftDown() && hasControlDown()) {
                        minecraft.displayGuiScreen(CraftingSettingsScreen(this, playerInventory.player, view[stack.otherId]))
                    } else if (grid.gridType === GridType.FLUID && held.isEmpty) {
                        RS.NETWORK_HANDLER.sendToServer(GridFluidPullMessage(view.stacks[slotNumber].id, hasShiftDown()))
                    } else if (grid.gridType !== GridType.FLUID) {
                        var flags = 0
                        if (clickedButton == 1) {
                            flags = flags or IItemGridHandler.EXTRACT_HALF
                        }
                        if (hasShiftDown()) {
                            flags = flags or IItemGridHandler.EXTRACT_SHIFT
                        }
                        if (clickedButton == 2) {
                            flags = flags or IItemGridHandler.EXTRACT_SINGLE
                        }
                        RS.NETWORK_HANDLER.sendToServer(GridItemPullMessage(stack.id, flags))
                    }
                }
                return true
            }
        }
        return super.mouseClicked(mouseX, mouseY, clickedButton)
    }

    private fun isOverCraftingOutputArea(mouseX: Double, mouseY: Double): Boolean {
        return if (grid!!.gridType !== GridType.CRAFTING) {
            false
        } else RenderUtils.inBounds(130, topHeight + visibleRows * 18 + 18, 24, 24, mouseX, mouseY)
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

    fun charTyped(p_charTyped_1_: Char, p_charTyped_2_: Int): Boolean {
        return if (searchField!!.charTyped(p_charTyped_1_, p_charTyped_2_)) {
            true
        } else super.charTyped(p_charTyped_1_, p_charTyped_2_)
    }

    fun keyReleased(key: Int, p_223281_2_: Int, p_223281_3_: Int): Boolean {
        if (key == GLFW.GLFW_KEY_LEFT_SHIFT || key == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            view.sort()
        }
        return super.keyReleased(key, p_223281_2_, p_223281_3_)
    }

    fun keyPressed(key: Int, scanCode: Int, modifiers: Int): Boolean {
        return if (searchField!!.keyPressed(key, scanCode, modifiers) || searchField.canWrite()) {
            true
        } else super.keyPressed(key, scanCode, modifiers)
    }

    fun updateExactPattern(checked: Boolean) {
        if (exactPattern != null) {
            exactPattern!!.setChecked(checked)
        }
    }

    fun updateScrollbar() {
        scrollbar!!.isEnabled = rows > visibleRows
        scrollbar!!.setMaxOffset(rows - visibleRows)
    }

    fun canSort(): Boolean {
        return doSort || !hasShiftDown()
    }

    companion object {
        val sorters: List<IGridSorter>
            get() {
                val sorters: MutableList<IGridSorter> = LinkedList()
                sorters.add(defaultSorter)
                sorters.add(QuantityGridSorter())
                sorters.add(IdGridSorter())
                sorters.add(LastModifiedGridSorter())
                sorters.add(InventoryTweaksGridSorter())
                return sorters
            }
        val defaultSorter: IGridSorter
            get() = NameGridSorter()
    }

    init {
        view = if (grid!!.gridType === GridType.FLUID) FluidGridView(this, defaultSorter, sorters) else ItemGridView(this, defaultSorter, sorters)
        wasConnected = grid!!.isGridActive
        tabs = TabListWidget(this, ElementDrawers(this, font), grid::tabs!!, grid::totalTabPages!!, grid::tabPage!!, grid::tabSelected!!, IGrid.TABS_PER_PAGE)
        tabs.addListener(object : ITabListListener {
            override fun onSelectionChanged(tab: Int) {
                grid.onTabSelectionChanged(tab)
            }

            override fun onPageChanged(page: Int) {
                grid.onTabPageChanged(page)
            }
        })
    }
}