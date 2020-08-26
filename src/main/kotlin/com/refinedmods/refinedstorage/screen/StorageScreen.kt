package com.refinedmods.refinedstorage.screen

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.storage.AccessType
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.screen.widget.sidebutton.*
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import com.refinedmods.refinedstorage.util.RenderUtils
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.util.text.Text
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.TranslationTextComponent
import java.util.function.Supplier

open class StorageScreen<T : Container?>(container: T,
                                         inventory: PlayerInventory?,
                                         title: Text?,
                                         private val texture: String,
                                         @param:Nullable private val typeParameter: TileDataParameter<Int?, *>?,
                                         @param:Nullable private val redstoneModeParameter: TileDataParameter<Int?, *>?,
                                         @param:Nullable private val exactModeParameter: TileDataParameter<Int?, *>?,
                                         @param:Nullable private val whitelistBlacklistParameter: TileDataParameter<Int?, *>?,
                                         private val priorityParameter: TileDataParameter<Int, *>,
                                         @param:Nullable private val accessTypeParameter: TileDataParameter<AccessType?, *>?,
                                         private val storedSupplier: Supplier<Long>, private val capacitySupplier: Supplier<Long>) : BaseScreen<T>(container, 176, 223, inventory, title) {
    override fun onPostInit(x: Int, y: Int) {
        if (redstoneModeParameter != null) {
            addSideButton(RedstoneModeSideButton(this, redstoneModeParameter))
        }
        if (typeParameter != null) {
            addSideButton(TypeSideButton(this, typeParameter))
        }
        if (whitelistBlacklistParameter != null) {
            addSideButton(WhitelistBlacklistSideButton(this, whitelistBlacklistParameter))
        }
        if (exactModeParameter != null) {
            addSideButton(ExactModeSideButton(this, exactModeParameter))
        }
        if (accessTypeParameter != null) {
            addSideButton(AccessTypeSideButton(this, accessTypeParameter))
        }
        val buttonWidth: Int = 10 + font.getStringWidth(I18n.format("misc.refinedstorage.priority"))
        addButton(x + 169 - buttonWidth, y + 41, buttonWidth, 20, TranslationTextComponent("misc.refinedstorage.priority"), true, true, Button.IPressable({ btn -> minecraft.displayGuiScreen(PriorityScreen(this, priorityParameter, playerInventory)) }))
    }

    override fun tick(x: Int, y: Int) {}
    override fun renderBackground(matrixStack: MatrixStack?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        bindTexture(RS.ID, texture)
        blit(matrixStack, x, y, 0, 0, xSize, ySize)
        val barHeightNew = (storedSupplier.get().toFloat() / capacitySupplier.get().toFloat() * BAR_HEIGHT.toFloat()).toInt()
        blit(matrixStack, x + BAR_X, y + BAR_Y + BAR_HEIGHT - barHeightNew, 179, BAR_HEIGHT - barHeightNew, BAR_WIDTH, barHeightNew)
    }

    override fun renderForeground(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int) {
        renderString(matrixStack, 7, 7, title.getString())
        renderString(matrixStack, 7, 42, if (capacitySupplier.get() == -1) I18n.format("misc.refinedstorage.storage.stored_minimal", instance().getQuantityFormatter()!!.formatWithUnits(storedSupplier.get())) else I18n.format("misc.refinedstorage.storage.stored_capacity_minimal", instance().getQuantityFormatter()!!.formatWithUnits(storedSupplier.get()), instance().getQuantityFormatter()!!.formatWithUnits(capacitySupplier.get()))
        )
        renderString(matrixStack, 7, 129, I18n.format("container.inventory"))
        if (RenderUtils.inBounds(BAR_X, BAR_Y, BAR_WIDTH, BAR_HEIGHT, mouseX.toDouble(), mouseY.toDouble())) {
            var full = 0
            if (capacitySupplier.get() >= 0) {
                full = (storedSupplier.get().toFloat() / capacitySupplier.get().toFloat() * 100f).toInt()
            }
            renderTooltip(matrixStack, mouseX, mouseY, (if (capacitySupplier.get() == -1) I18n.format("misc.refinedstorage.storage.stored_minimal", instance().getQuantityFormatter()!!.format(storedSupplier.get())) else I18n.format("misc.refinedstorage.storage.stored_capacity_minimal", instance().getQuantityFormatter()!!.format(storedSupplier.get()), instance().getQuantityFormatter()!!.format(capacitySupplier.get()))).toString() + "\n" + TextFormatting.GRAY + I18n.format("misc.refinedstorage.storage.full", full))
        }
    }

    companion object {
        private const val BAR_X = 8
        private const val BAR_Y = 54
        private const val BAR_WIDTH = 16
        private const val BAR_HEIGHT = 70
    }
}