package com.refinedmods.refinedstorage.screen

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.apiimpl.network.node.CrafterManagerNetworkNode
import com.refinedmods.refinedstorage.container.CrafterManagerContainer
import com.refinedmods.refinedstorage.container.slot.CrafterManagerSlot
import com.refinedmods.refinedstorage.screen.widget.ScrollbarWidget
import com.refinedmods.refinedstorage.screen.widget.SearchWidget
import com.refinedmods.refinedstorage.screen.widget.sidebutton.CrafterManagerSearchBoxModeSideButton
import com.refinedmods.refinedstorage.screen.widget.sidebutton.GridSizeSideButton
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton
import com.refinedmods.refinedstorage.tile.CrafterManagerTile
import com.refinedmods.refinedstorage.tile.data.TileDataManager
import com.refinedmods.refinedstorage.util.RenderUtils
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Slot
import net.minecraft.util.text.Text
import yalter.mousetweaks.api.MouseTweaksDisableWheelTweak

@MouseTweaksDisableWheelTweak
class CrafterManagerScreen(container: CrafterManagerContainer, inventory: PlayerInventory?, title: Text?) : BaseScreen<CrafterManagerContainer?>(container, 193, 0, inventory, title), IScreenInfoProvider {
    private val crafterManager: CrafterManagerNetworkNode
    private var scrollbar: ScrollbarWidget? = null
    private var searchField: SearchWidget? = null
    override fun onPreInit() {
        this.ySize = topHeight + bottomHeight + visibleRows * 18
    }

    override fun onPostInit(x: Int, y: Int) {
        addSideButton(RedstoneModeSideButton(this, CrafterManagerTile.REDSTONE_MODE))
        addSideButton(CrafterManagerSearchBoxModeSideButton(this))
        addSideButton(GridSizeSideButton(this, { crafterManager.getSize() }) { size: Int? -> TileDataManager.setParameter(CrafterManagerTile.SIZE, size) })
        scrollbar = ScrollbarWidget(this, 174, topHeight, 12, visibleRows * 18 - 2)
        scrollbar!!.addListener { oldOffset: Int, newOffset: Int -> container.initSlots(null) }
        container.initSlots(null)
        val sx = x + 97 + 1
        val sy = y + 6 + 1
        if (searchField == null) {
            searchField = SearchWidget(font, sx, sy, 88 - 6)
            searchField.setResponder { value ->
                searchField!!.updateJei()
                container.initSlots(null)
            }
            searchField!!.setMode(crafterManager.getSearchBoxMode())
        } else {
            searchField!!.x = sx
            searchField!!.y = sy
        }
        addButton(searchField)
    }

    override fun tick(x: Int, y: Int) {
        scrollbar!!.isEnabled = rows - 1 >= visibleRows
        scrollbar!!.setMaxOffset(rows - visibleRows)
    }

    override fun renderBackground(matrixStack: MatrixStack?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        bindTexture(RS.ID, "gui/crafter_manager.png")
        blit(matrixStack, x, y, 0, 0, xSize, topHeight)
        val rows = visibleRows
        var yy = y
        for (i in 0 until rows) {
            yy += 18
            blit(matrixStack, x, yy, 0, topHeight + if (i > 0) if (i == rows - 1) 18 * 2 else 18 else 0, xSize, 18)
        }
        yy += 18
        blit(matrixStack, x, yy, 0, topHeight + 18 * 3, xSize, bottomHeight)
        if (crafterManager.isActiveOnClient) {
            for (slot in container.inventorySlots) {
                if (slot is CrafterManagerSlot && slot.isEnabled()) {
                    blit(matrixStack, x + slot.xPos - 1, y + slot.yPos - 1, 0, 193, 18, 18)
                }
            }
        }
        searchField!!.render(matrixStack, 0, 0, 0f)
        scrollbar!!.render(matrixStack)
    }

    override fun renderForeground(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int) {
        renderString(matrixStack, 7, 7, title.getString())
        renderString(matrixStack, 7, yPlayerInventory - 12, I18n.format("container.inventory"))
        if (container != null && crafterManager.isActiveOnClient) {
            for ((key, y) in container.getHeadings().entrySet()) {
                if (y >= topHeight - 1 && y < topHeight + visibleRows * 18 - 1) {
                    RenderSystem.disableLighting()
                    RenderSystem.color3f(1f, 1f, 1f)
                    bindTexture(RS.ID, "gui/crafter_manager.png")
                    blit(matrixStack, 7, y, 0, 174, 18 * 9, 18)
                    renderString(matrixStack, 7 + 4, y + 6, RenderUtils.shorten(I18n.format(key), 25))
                }
            }
        }
    }

    fun mouseClicked(mouseX: Double, mouseY: Double, clickedButton: Int): Boolean {
        return if (scrollbar!!.mouseClicked(mouseX, mouseY, clickedButton)) {
            true
        } else super.mouseClicked(mouseX, mouseY, clickedButton)
    }

    fun charTyped(p_charTyped_1_: Char, p_charTyped_2_: Int): Boolean {
        return if (searchField!!.charTyped(p_charTyped_1_, p_charTyped_2_)) {
            true
        } else super.charTyped(p_charTyped_1_, p_charTyped_2_)
    }

    fun keyPressed(key: Int, scanCode: Int, modifiers: Int): Boolean {
        return if (searchField!!.keyPressed(key, scanCode, modifiers) || searchField.canWrite()) {
            true
        } else super.keyPressed(key, scanCode, modifiers)
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

    fun getSearchField(): SearchWidget? {
        return searchField
    }

    fun getCrafterManager(): CrafterManagerNetworkNode {
        return crafterManager
    }

    override fun getTopHeight(): Int {
        return 19
    }

    override fun getBottomHeight(): Int {
        return 99
    }

    override fun getVisibleRows(): Int {
        return when (crafterManager.getSize()) {
            IGrid.SIZE_STRETCH -> {
                val screenSpaceAvailable: Int = height - topHeight - bottomHeight
                Math.max(3, Math.min(screenSpaceAvailable / 18 - 3, RS.CLIENT_CONFIG.crafterManager.getMaxRowsStretch()))
            }
            IGrid.SIZE_SMALL -> 3
            IGrid.SIZE_MEDIUM -> 5
            IGrid.SIZE_LARGE -> 8
            else -> 3
        }
    }

    override fun getRows(): Int {
        return if (!crafterManager.isActiveOnClient) 0 else container.getRows()
    }

    override fun getCurrentOffset(): Int {
        return if (scrollbar == null) 0 else scrollbar!!.offset
    }

    override fun getSearchFieldText(): String? {
        return if (searchField == null) "" else searchField!!.text
    }

    override fun getYPlayerInventory(): Int {
        return topHeight + visibleRows * 18 + 16
    }

    init {
        crafterManager = (container.tile as CrafterManagerTile?).getNode()
    }
}