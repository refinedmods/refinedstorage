package com.refinedmods.refinedstorage.screen

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot
import com.refinedmods.refinedstorage.integration.craftingtweaks.CraftingTweaksIntegration
import com.refinedmods.refinedstorage.integration.craftingtweaks.CraftingTweaksIntegration.isCraftingTweaksClass
import com.refinedmods.refinedstorage.render.FluidRenderer
import com.refinedmods.refinedstorage.render.RenderSettings
import com.refinedmods.refinedstorage.screen.grid.AlternativesScreen
import com.refinedmods.refinedstorage.screen.widget.CheckboxWidget
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton
import com.refinedmods.refinedstorage.util.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.client.gui.widget.Widget
import net.minecraft.client.gui.widget.button.Button
import net.minecraft.client.gui.widget.button.CheckboxButton
import net.minecraft.client.settings.KeyBinding
import net.minecraft.client.util.InputMappings
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.ClickType
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.Slot
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.text.Text
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TranslationTextComponent
import net.minecraftforge.fluids.FluidInstance
import net.minecraftforge.fml.client.gui.GuiUtils
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.GL11
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Collectors

abstract class BaseScreen<T : Container?>(container: T, xSize: Int, ySize: Int, inventory: PlayerInventory?, title: Text?) : ContainerScreen<T>(container, inventory, title) {
    private val logger = LogManager.getLogger(javaClass)
    private var sideButtonY = 0
    private fun runActions() {
        runActions(javaClass)
        runActions(ContainerScreen::class.java)
    }

    private fun runActions(clazz: Class<*>) {
        val queue = ACTIONS[clazz]
        if (queue != null && !queue.isEmpty()) {
            var callback: Consumer<*>
            while (queue.poll().also { callback = it } != null) {
                callback.accept(this)
            }
        }
    }

    fun init() {
        minecraft.keyboardListener.enableRepeatEvents(true)
        onPreInit()
        super.init()
        if (CraftingTweaksIntegration.isLoaded) {
            buttons.removeIf({ b -> !isCraftingTweaksClass(b.getClass()) })
            children.removeIf({ c -> !isCraftingTweaksClass(c.getClass()) })
        } else {
            buttons.clear()
            children.clear()
        }
        sideButtonY = 6
        onPostInit(guiLeft, guiTop)
        runActions()
    }

    fun onClose() {
        super.onClose()
        minecraft.keyboardListener.enableRepeatEvents(false)
    }

    fun tick() {
        super.tick()
        runActions()
        tick(guiLeft, guiTop)
    }

    open fun render(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int, partialTicks: Float) {
        renderBackground(matrixStack)
        super.render(matrixStack, mouseX, mouseY, partialTicks)
        func_230459_a_(matrixStack, mouseX, mouseY)
    }

    protected fun drawGuiContainerBackgroundLayer(matrixStack: MatrixStack?, renderPartialTicks: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        renderBackground(matrixStack, guiLeft, guiTop, mouseX, mouseY)
        for (i in 0 until this.container.inventorySlots.size()) {
            val slot: Slot = container.inventorySlots.get(i)
            if (slot.isEnabled() && slot is FluidFilterSlot) {
                val stack: FluidInstance? = (slot as FluidFilterSlot).fluidInventory.getFluid(slot.getSlotIndex())
                if (!stack.isEmpty()) {
                    FluidRenderer.INSTANCE.render(matrixStack, guiLeft + slot.xPos, guiTop + slot.yPos, stack)
                    if ((slot as FluidFilterSlot).isSizeAllowed) {
                        renderQuantity(matrixStack, guiLeft + slot.xPos, guiTop + slot.yPos, instance().getQuantityFormatter()!!.formatInBucketForm(stack.getAmount()), RenderSettings.INSTANCE.secondaryColor)
                        GL11.glDisable(GL11.GL_LIGHTING)
                    }
                }
            }
        }
    }

    protected fun drawGuiContainerForegroundLayer(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int) {
        var mouseX = mouseX
        var mouseY = mouseY
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        mouseX -= guiLeft
        mouseY -= guiTop
        renderForeground(matrixStack, mouseX, mouseY)
        for (button in this.buttons) {
            if (button is SideButton && button.isHovered()) {
                renderTooltip(matrixStack, mouseX, mouseY, (button as SideButton).tooltip)
            }
        }
        for (i in 0 until this.container.inventorySlots.size()) {
            val slot: Slot = container.inventorySlots.get(i)
            if (slot.isEnabled() && slot is FluidFilterSlot) {
                val stack: FluidInstance? = (slot as FluidFilterSlot).fluidInventory.getFluid(slot.getSlotIndex())
                if (!stack.isEmpty() && RenderUtils.inBounds(slot.xPos, slot.yPos, 17, 17, mouseX.toDouble(), mouseY.toDouble())) {
                    renderTooltip(matrixStack, mouseX, mouseY, stack.getDisplayName().getString())
                }
            }
        }
    }

    protected fun handleMouseClick(slot: Slot, slotId: Int, mouseButton: Int, type: ClickType) {
        val valid = type !== ClickType.QUICK_MOVE && minecraft.player.inventory.getItemStack().isEmpty()
        if (valid && slot is FilterSlot && slot.isEnabled() && (slot as FilterSlot).isSizeAllowed) {
            if (!slot.getStack().isEmpty()) {
                if ((slot as FilterSlot).isAlternativesAllowed && hasControlDown()) {
                    minecraft.displayGuiScreen(AlternativesScreen(
                            this,
                            minecraft.player,
                            TranslationTextComponent("gui.refinedstorage.alternatives"),
                            slot.getStack(),
                            slot.getSlotIndex()
                    ))
                } else {
                    minecraft.displayGuiScreen(ItemAmountScreen(
                            this,
                            minecraft.player,
                            slot.slotNumber,
                            slot.getStack(),
                            slot.getSlotStackLimit(),
                            if ((slot as FilterSlot).isAlternativesAllowed) Function { parent: Screen? ->
                                AlternativesScreen(
                                        parent,
                                        minecraft.player,
                                        TranslationTextComponent("gui.refinedstorage.alternatives"),
                                        slot.getStack(),
                                        slot.getSlotIndex()
                                )
                            } else null
                    ))
                }
            }
        } else if (valid && slot is FluidFilterSlot && slot.isEnabled() && (slot as FluidFilterSlot).isSizeAllowed) {
            val stack: FluidInstance? = (slot as FluidFilterSlot).fluidInventory.getFluid(slot.getSlotIndex())
            if (!stack.isEmpty()) {
                if ((slot as FluidFilterSlot).isAlternativesAllowed && hasControlDown()) {
                    minecraft.displayGuiScreen(AlternativesScreen(
                            this,
                            minecraft.player,
                            TranslationTextComponent("gui.refinedstorage.alternatives"),
                            stack,
                            slot.getSlotIndex()
                    ))
                } else {
                    minecraft.displayGuiScreen(FluidAmountScreen(
                            this,
                            minecraft.player,
                            slot.slotNumber,
                            stack,
                            (slot as FluidFilterSlot).fluidInventory.maxAmount,
                            if ((slot as FluidFilterSlot).isAlternativesAllowed) Function { parent: Screen? ->
                                AlternativesScreen(
                                        this,
                                        minecraft.player,
                                        TranslationTextComponent("gui.refinedstorage.alternatives"),
                                        stack,
                                        slot.getSlotIndex()
                                )
                            } else null
                    ))
                }
            } else {
                super.handleMouseClick(slot, slotId, mouseButton, type)
            }
        } else {
            super.handleMouseClick(slot, slotId, mouseButton, type)
        }
    }

    fun addCheckBox(x: Int, y: Int, text: Text, checked: Boolean, onPress: Consumer<CheckboxButton>): CheckboxWidget {
        val checkBox = CheckboxWidget(x, y, text, checked, onPress)
        addButton(checkBox)
        return checkBox
    }

    fun addButton(x: Int, y: Int, w: Int, h: Int, text: Text?, enabled: Boolean, visible: Boolean, onPress: Button.IPressable?): Button {
        val button = Button(x, y, w, h, text, onPress)
        button.active = enabled
        button.visible = visible
        addButton(button)
        return button
    }

    fun addSideButton(button: SideButton) {
        button.x = guiLeft + -SideButton.Companion.WIDTH - 2
        button.y = guiTop + sideButtonY
        sideButtonY += SideButton.Companion.HEIGHT + 2
        addButton(button)
    }

    fun bindTexture(namespace: String, filenameInTexturesFolder: String) {
        minecraft.getTextureManager().bindTexture(TEXTURE_CACHE.computeIfAbsent("$namespace:$filenameInTexturesFolder") { newId: String? -> Identifier(namespace, "textures/$filenameInTexturesFolder") })
    }

    fun renderItem(matrixStack: MatrixStack?, x: Int, y: Int, stack: ItemStack) {
        renderItem(matrixStack, x, y, stack, false, null, 0)
    }

    fun renderItem(matrixStack: MatrixStack?, x: Int, y: Int, stack: ItemStack, overlay: Boolean, @Nullable text: String?, textColor: Int) {
        try {
            setBlitOffset(Z_LEVEL_ITEMS)
            itemRenderer.zLevel = Z_LEVEL_ITEMS
            itemRenderer.renderItemIntoGUI(stack, x, y)
            if (overlay) {
                itemRenderer.renderItemOverlayIntoGUI(font, stack, x, y, "")
            }
            setBlitOffset(0)
            itemRenderer.zLevel = 0
            text?.let { renderQuantity(matrixStack, x, y, it, textColor) }
        } catch (t: Throwable) {
            logger.warn("Couldn't render stack: " + stack.item.toString(), t)
        }
    }

    fun renderQuantity(matrixStack: MatrixStack?, x: Int, y: Int, qty: String?, color: Int) {
        val large = minecraft.getForceUnicodeFont() || RS.CLIENT_CONFIG.grid.getLargeFont()
        RenderSystem.pushMatrix()
        RenderSystem.translatef(x.toFloat(), y.toFloat(), Z_LEVEL_QTY.toFloat())
        if (!large) {
            RenderSystem.scalef(0.5f, 0.5f, 1f)
        }
        font.drawStringWithShadow(matrixStack, qty, (if (large) 16 else 30) - font.getStringWidth(qty), if (large) 8 else 22, color)
        RenderSystem.popMatrix()
    }

    fun renderString(matrixStack: MatrixStack?, x: Int, y: Int, message: String?) {
        renderString(matrixStack, x, y, message, RenderSettings.INSTANCE.primaryColor)
    }

    fun renderString(matrixStack: MatrixStack?, x: Int, y: Int, message: String?, color: Int) {
        font.drawString(matrixStack, message, x, y, color)
    }

    fun renderTooltip(matrixStack: MatrixStack?, x: Int, y: Int, lines: String?) {
        renderTooltip(matrixStack, ItemStack.EMPTY, x, y, lines)
    }

    fun renderTooltip(matrixStack: MatrixStack?, @Nonnull stack: ItemStack?, x: Int, y: Int, lines: String?) {
        renderTooltip(matrixStack, stack, x, y, Arrays.stream(lines!!.split("\n").toTypedArray()).map<Any> { StringTextComponent() }.collect(Collectors.toList()))
    }

    fun renderTooltip(matrixStack: MatrixStack?, @Nonnull stack: ItemStack?, x: Int, y: Int, lines: List<Text?>?) {
        // TODO GuiUtils.drawHoveringText(stack, matrixStack, lines, x, y, width, height, -1, font);
    }

    protected open fun onPreInit() {
        // NO OP
    }

    abstract fun onPostInit(x: Int, y: Int)
    abstract fun tick(x: Int, y: Int)
    abstract fun renderBackground(matrixStack: MatrixStack?, x: Int, y: Int, mouseX: Int, mouseY: Int)
    abstract fun renderForeground(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int)

    companion object {
        const val Z_LEVEL_ITEMS = 100
        const val Z_LEVEL_TOOLTIPS = 500
        const val Z_LEVEL_QTY = 300
        private val TEXTURE_CACHE: MutableMap<String, Identifier> = HashMap<String, Identifier>()
        private val ACTIONS: MutableMap<Class<*>, Queue<Consumer<*>>> = HashMap()
        fun isKeyDown(keybinding: KeyBinding): Boolean {
            return InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), keybinding.getKey().getKeyCode()) &&
                    keybinding.getKeyConflictContext().isActive() &&
                    keybinding.getKeyModifier().isActive(keybinding.getKeyConflictContext())
        }

        fun <T> executeLater(clazz: Class<T>, callback: Consumer<T>?) {
            var queue: Queue<Consumer<*>?>? = ACTIONS[clazz]
            if (queue == null) {
                ACTIONS[clazz] = ArrayDeque<Consumer<*>>().also { queue = it }
            }
            queue!!.add(callback)
        }

        fun executeLater(callback: Consumer<ContainerScreen>?) {
            executeLater<T>(ContainerScreen::class.java, callback)
        }
    }

    init {
        xSize = xSize
        ySize = ySize
    }
}