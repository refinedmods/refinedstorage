package com.refinedmods.refinedstorage.screen.grid

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement
import com.refinedmods.refinedstorage.api.autocrafting.task.CalculationResultType
import com.refinedmods.refinedstorage.api.render.IElementDrawers
import com.refinedmods.refinedstorage.apiimpl.autocrafting.preview.ErrorCraftingPreviewElement
import com.refinedmods.refinedstorage.apiimpl.autocrafting.preview.FluidCraftingPreviewElement
import com.refinedmods.refinedstorage.apiimpl.autocrafting.preview.ItemCraftingPreviewElement
import com.refinedmods.refinedstorage.apiimpl.render.CraftingPreviewElementDrawers
import com.refinedmods.refinedstorage.item.PatternItem.Companion.fromCache
import com.refinedmods.refinedstorage.network.grid.GridCraftingStartRequestMessage
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.screen.widget.ScrollbarWidget
import com.refinedmods.refinedstorage.util.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.button.Button
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.container.Container
import net.minecraft.item.ItemStack
import net.minecraft.util.text.Text
import net.minecraft.util.text.TranslationTextComponent
import net.minecraftforge.fluids.FluidInstance
import org.lwjgl.glfw.GLFW
import java.util.*

class CraftingPreviewScreen(parent: Screen, stacks: List<ICraftingPreviewElement<*>>?, id: UUID, quantity: Int, fluids: Boolean, title: Text?) : BaseScreen<Container?>(object : Container(null, 0) {
    fun canInteractWith(@Nonnull player: PlayerEntity?): Boolean {
        return false
    }
}, 254, 201, null, title) {
    private val stacks: List<ICraftingPreviewElement<*>>
    private val parent: Screen
    private val scrollbar: ScrollbarWidget?
    private val id: UUID
    private val quantity: Int
    private val fluids: Boolean
    private var hoveringStack: ItemStack? = null
    private var hoveringFluid: FluidInstance? = null
    private val drawers: IElementDrawers = CraftingPreviewElementDrawers(this, font)
    override fun onPostInit(x: Int, y: Int) {
        addButton(x + 55, y + 201 - 20 - 7, 50, 20, TranslationTextComponent("gui.cancel"), true, true, Button.IPressable({ btn -> close() }))
        val startButton: Button? = addButton(x + 129, y + 201 - 20 - 7, 50, 20, TranslationTextComponent("misc.refinedstorage.start"), true, true, Button.IPressable({ btn -> startRequest() }))
        startButton.active = stacks.stream().noneMatch { obj: ICraftingPreviewElement<*> -> obj.hasMissing() } && errorType == null
    }

    override fun tick(x: Int, y: Int) {
        scrollbar!!.isEnabled = rows > VISIBLE_ROWS
        scrollbar.setMaxOffset(rows - VISIBLE_ROWS)
    }

    @get:Nullable
    private val errorType: CalculationResultType?
        private get() = if (stacks.size == 1 && stacks[0] is ErrorCraftingPreviewElement) {
            (stacks[0] as ErrorCraftingPreviewElement).getType()
        } else null

    override fun renderBackground(matrixStack: MatrixStack?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        bindTexture(RS.ID, "gui/crafting_preview.png")
        blit(matrixStack, x, y, 0, 0, xSize, ySize)
        if (errorType != null) {
            fill(matrixStack, x + 7, y + 20, x + 228, y + 169, -0x242425)
        }
        scrollbar!!.render(matrixStack)
    }

    override fun renderForeground(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int) {
        renderString(matrixStack, 7, 7, title.getString())
        var x = 7
        var y = 15
        val scale = if (Minecraft.getInstance().getForceUnicodeFont()) 1f else 0.5f
        if (errorType != null) {
            RenderSystem.pushMatrix()
            RenderSystem.scalef(scale, scale, 1f)
            renderString(matrixStack, RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 11, scale), I18n.format("gui.refinedstorage.crafting_preview.error"))
            when (errorType) {
                CalculationResultType.RECURSIVE -> {
                    renderString(matrixStack, RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 21, scale), I18n.format("gui.refinedstorage.crafting_preview.error.recursive.0"))
                    renderString(matrixStack, RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 31, scale), I18n.format("gui.refinedstorage.crafting_preview.error.recursive.1"))
                    renderString(matrixStack, RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 41, scale), I18n.format("gui.refinedstorage.crafting_preview.error.recursive.2"))
                    renderString(matrixStack, RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 51, scale), I18n.format("gui.refinedstorage.crafting_preview.error.recursive.3"))
                    renderString(matrixStack, RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 61, scale), I18n.format("gui.refinedstorage.crafting_preview.error.recursive.4"))
                    val pattern: ICraftingPattern? = fromCache(parent.getMinecraft().world, (stacks[0].getElement() as ItemStack?)!!)
                    var yy = 83
                    for (output in pattern!!.getOutputs()) {
                        if (output != null) {
                            RenderSystem.pushMatrix()
                            RenderSystem.scalef(scale, scale, 1f)
                            renderString(matrixStack, RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy + 6, scale), output.getDisplayName().getString())
                            RenderSystem.popMatrix()
                            RenderHelper.setupGui3DDiffuseLighting()
                            RenderSystem.enableDepthTest()
                            renderItem(matrixStack, x + 5, yy, output)
                            RenderHelper.disableStandardItemLighting()
                            yy += 17
                        }
                    }
                }
                CalculationResultType.TOO_COMPLEX -> {
                    renderString(matrixStack, RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 21, scale), I18n.format("gui.refinedstorage.crafting_preview.error.too_complex.0"))
                    renderString(matrixStack, RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 31, scale), I18n.format("gui.refinedstorage.crafting_preview.error.too_complex.1"))
                }
            }
            RenderSystem.popMatrix()
        } else {
            var slot = if (scrollbar != null) scrollbar.offset * 3 else 0
            RenderHelper.setupGui3DDiffuseLighting()
            RenderSystem.enableDepthTest()
            hoveringStack = null
            hoveringFluid = null
            for (i in 0 until 3 * 5) {
                if (slot < stacks.size) {
                    val stack = stacks[slot]
                    stack.draw(matrixStack, x, y + 5, drawers)
                    if (RenderUtils.inBounds(x + 5, y + 7, 16, 16, mouseX.toDouble(), mouseY.toDouble())) {
                        hoveringStack = if (stack.getId().equals(ItemCraftingPreviewElement.ID)) stack.getElement() as ItemStack? else null
                        if (hoveringStack == null) {
                            hoveringFluid = if (stack.getId().equals(FluidCraftingPreviewElement.ID)) stack.getElement() as FluidInstance? else null
                        }
                    }
                }
                if ((i + 1) % 3 == 0) {
                    x = 7
                    y += 30
                } else {
                    x += 74
                }
                slot++
            }
        }
    }

    override fun render(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.render(matrixStack, mouseX, mouseY, partialTicks)
        if (hoveringStack != null) {
            renderTooltip(
                    matrixStack,
                    hoveringStack,
                    mouseX,
                    mouseY,
                    hoveringStack!!.getTooltip(
                            Minecraft.getInstance().player,
                            if (Minecraft.getInstance().gameSettings.advancedItemTooltips) ITooltipFlag.TooltipFlags.ADVANCED else ITooltipFlag.TooltipFlags.NORMAL
                    )
            )
        } else if (hoveringFluid != null) {
            renderTooltip(matrixStack, mouseX, mouseY, hoveringFluid.getDisplayName().getString())
        }
    }

    fun mouseMoved(mx: Double, my: Double) {
        scrollbar!!.mouseMoved(mx, my)
        super.mouseMoved(mx, my)
    }

    fun mouseClicked(mx: Double, my: Double, button: Int): Boolean {
        return scrollbar!!.mouseClicked(mx, my, button) || super.mouseClicked(mx, my, button)
    }

    fun mouseReleased(mx: Double, my: Double, button: Int): Boolean {
        return scrollbar!!.mouseReleased(mx, my, button) || super.mouseReleased(mx, my, button)
    }

    fun mouseScrolled(x: Double, y: Double, delta: Double): Boolean {
        return scrollbar!!.mouseScrolled(x, y, delta) || super.mouseScrolled(x, y, delta)
    }

    fun keyPressed(key: Int, scanCode: Int, modifiers: Int): Boolean {
        if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
            startRequest()
            return true
        }
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            close()
            return true
        }
        return super.keyPressed(key, scanCode, modifiers)
    }

    private fun startRequest() {
        RS.NETWORK_HANDLER.sendToServer(GridCraftingStartRequestMessage(id, quantity, fluids))
        close()
    }

    private val rows: Int
        private get() = Math.max(0, Math.ceil(stacks.size.toFloat() / 3f.toDouble()).toInt())

    private fun close() {
        minecraft.displayGuiScreen(parent)
    }

    companion object {
        private const val VISIBLE_ROWS = 5
    }

    init {
        this.stacks = ArrayList(stacks)
        this.parent = parent
        this.id = id
        this.quantity = quantity
        this.fluids = fluids
        scrollbar = ScrollbarWidget(this, 235, 20, 12, 149)
    }
}