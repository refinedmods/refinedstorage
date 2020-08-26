package com.refinedmods.refinedstorage.screen

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.api.util.IFilter
import com.refinedmods.refinedstorage.container.FilterContainer
import com.refinedmods.refinedstorage.item.FilterItem.Companion.getCompare
import com.refinedmods.refinedstorage.item.FilterItem.Companion.getMode
import com.refinedmods.refinedstorage.item.FilterItem.Companion.getName
import com.refinedmods.refinedstorage.item.FilterItem.Companion.getType
import com.refinedmods.refinedstorage.item.FilterItem.Companion.isModFilter
import com.refinedmods.refinedstorage.item.FilterItem.Companion.setType
import com.refinedmods.refinedstorage.network.FilterUpdateMessage
import com.refinedmods.refinedstorage.render.RenderSettings
import com.refinedmods.refinedstorage.screen.widget.CheckboxWidget
import com.refinedmods.refinedstorage.screen.widget.sidebutton.FilterTypeSideButton
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.gui.widget.button.Button
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.text.Text
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TranslationTextComponent
import org.lwjgl.glfw.GLFW

class FilterScreen(container: FilterContainer, inventory: PlayerInventory?, title: Text?) : BaseScreen<FilterContainer?>(container, 176, 231, inventory, title) {
    private val stack: ItemStack
    private var compare: Int
    private var mode: Int
    private var modFilter: Boolean
    private val name: String
    private var type: Int
    private var modFilterCheckBox: CheckboxWidget? = null
    private var modeButton: Button? = null
    private var nameField: TextFieldWidget? = null
    override fun onPostInit(x: Int, y: Int) {
        addCheckBox(x + 7, y + 77, TranslationTextComponent("gui.refinedstorage.filter.compare_nbt"), compare and IComparer.COMPARE_NBT == IComparer.COMPARE_NBT) { btn: CheckboxButton? ->
            compare = compare xor IComparer.COMPARE_NBT
            sendUpdate()
        }
        modFilterCheckBox = addCheckBox(0, y + 71 + 25, TranslationTextComponent("gui.refinedstorage.filter.mod_filter"), modFilter) { btn: CheckboxButton? ->
            modFilter = !modFilter
            sendUpdate()
        }
        modeButton = addButton(x + 7, y + 71 + 21, 0, 20, StringTextComponent(""), true, true, Button.IPressable({ btn ->
            mode = if (mode == IFilter.MODE_WHITELIST) IFilter.MODE_BLACKLIST else IFilter.MODE_WHITELIST
            updateModeButton(mode)
            sendUpdate()
        }))
        updateModeButton(mode)
        nameField = TextFieldWidget(font, x + 34, y + 121, 137 - 6, font.FONT_HEIGHT, StringTextComponent(""))
        nameField!!.text = name
        nameField.setEnableBackgroundDrawing(false)
        nameField!!.isVisible = true
        nameField.setCanLoseFocus(true)
        nameField.setFocused2(false)
        nameField.setTextColor(RenderSettings.INSTANCE.secondaryColor)
        nameField.setResponder { name -> sendUpdate() }
        addButton(nameField)
        addSideButton(FilterTypeSideButton(this))
    }

    private fun updateModeButton(mode: Int) {
        val text: Text = if (mode == IFilter.MODE_WHITELIST) TranslationTextComponent("sidebutton.refinedstorage.mode.whitelist") else TranslationTextComponent("sidebutton.refinedstorage.mode.blacklist")
        modeButton.setWidth(font.getStringWidth(text.getString()) + 12)
        modeButton.setMessage(text)
        modFilterCheckBox.x = modeButton.x + modeButton.getWidth() + 4
    }

    fun keyPressed(key: Int, scanCode: Int, modifiers: Int): Boolean {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            minecraft.player.closeScreen()
            return true
        }
        return if (nameField!!.keyPressed(key, scanCode, modifiers) || nameField.canWrite()) {
            true
        } else super.keyPressed(key, scanCode, modifiers)
    }

    override fun tick(x: Int, y: Int) {}
    override fun renderBackground(matrixStack: MatrixStack?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        bindTexture(RS.ID, "gui/filter.png")
        blit(matrixStack, x, y, 0, 0, xSize, ySize)
    }

    override fun renderForeground(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int) {
        renderString(matrixStack, 7, 7, title.getString())
        renderString(matrixStack, 7, 137, I18n.format("container.inventory"))
    }

    fun getType(): Int {
        return type
    }

    fun setType(type: Int) {
        this.type = type
        setType(stack, type)
    }

    fun sendUpdate() {
        RS.NETWORK_HANDLER.sendToServer(FilterUpdateMessage(compare, mode, modFilter, nameField!!.text, type))
    }

    init {
        stack = container.stack
        compare = getCompare(container.stack)
        mode = getMode(container.stack)
        modFilter = isModFilter(container.stack)
        name = getName(container.stack)
        type = getType(container.stack)
    }
}