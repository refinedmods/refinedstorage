package com.refinedmods.refinedstorage.screen.widget

import com.refinedmods.refinedstorage.RSKeyBindings
import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.api.network.grid.IGrid.Companion.isSearchBoxModeWithAutoselection
import com.refinedmods.refinedstorage.integration.jei.JeiIntegration
import com.refinedmods.refinedstorage.integration.jei.RSJeiPlugin
import com.refinedmods.refinedstorage.render.RenderSettings
import com.refinedmods.refinedstorage.screen.BaseScreen
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.util.text.StringTextComponent
import org.lwjgl.glfw.GLFW
import java.util.*

class SearchWidget(fontRenderer: FontRenderer, x: Int, y: Int, width: Int) : TextFieldWidget(fontRenderer, x, y, width, fontRenderer.FONT_HEIGHT, StringTextComponent("")) {
    private var mode = 0
    private var historyIndex = -1
    fun updateJei() {
        if (JeiIntegration.isLoaded && (mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED || mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED)) {
            RSJeiPlugin.RUNTIME.getIngredientFilter().setFilterText(text)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        val wasFocused = isFocused
        val result = super.mouseClicked(mouseX, mouseY, mouseButton)
        val clickedWidget = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height
        if (clickedWidget && mouseButton == 1) {
            text = ""
            isFocused = true
        } else if (wasFocused != isFocused) {
            saveHistory()
        }
        return result
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifier: Int): Boolean {
        var result = super.keyPressed(keyCode, scanCode, modifier)
        if (isFocused) {
            if (keyCode == GLFW.GLFW_KEY_UP) {
                updateHistory(-1)
                result = true
            } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
                updateHistory(1)
                result = true
            } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                saveHistory()
                if (canLoseFocus) {
                    isFocused = false
                }
                result = true
            } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                saveHistory()
                if (!canLoseFocus) {
                    // If we can't lose focus,
                    // and we press escape,
                    // we unfocus ourselves,
                    // and close the screen immediately.
                    isFocused = false
                    result = false // Bubble the event up to the screen.
                } else {
                    // If we can lose focus,
                    // and we press escape,
                    // we unfocus ourselves.
                    // On the next escape press, the screen will close.
                    isFocused = false
                    result = true
                }
            }
        }
        if (BaseScreen.Companion.isKeyDown(RSKeyBindings.FOCUS_SEARCH_BAR) && canLoseFocus) {
            isFocused = !isFocused
            saveHistory()
            result = true
        }
        return result
    }

    private fun updateHistory(delta: Int) {
        if (HISTORY.isEmpty()) {
            return
        }
        if (historyIndex == -1) {
            historyIndex = HISTORY.size
        }
        historyIndex += delta
        if (historyIndex < 0) {
            historyIndex = 0
        } else if (historyIndex > HISTORY.size - 1) {
            historyIndex = HISTORY.size - 1
            if (delta == 1) {
                text = ""
                return
            }
        }
        text = HISTORY[historyIndex]
    }

    private fun saveHistory() {
        if (!HISTORY.isEmpty() && HISTORY[HISTORY.size - 1] == text) {
            return
        }
        if (!text.trim { it <= ' ' }.isEmpty()) {
            HISTORY.add(text)
        }
    }

    fun setMode(mode: Int) {
        this.mode = mode
        this.setCanLoseFocus(!isSearchBoxModeWithAutoselection(mode))
        this.isFocused = isSearchBoxModeWithAutoselection(mode)
    }

    companion object {
        private val HISTORY: MutableList<String> = ArrayList()
    }

    init {
        this.setEnableBackgroundDrawing(false)
        this.isVisible = true
        this.setTextColor(RenderSettings.INSTANCE.secondaryColor)
    }
}