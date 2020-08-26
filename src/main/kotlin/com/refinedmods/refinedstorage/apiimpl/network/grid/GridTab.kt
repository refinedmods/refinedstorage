package com.refinedmods.refinedstorage.apiimpl.network.grid

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem
import com.refinedmods.refinedstorage.api.network.grid.IGridTab
import com.refinedmods.refinedstorage.api.render.IElementDrawer
import com.refinedmods.refinedstorage.api.util.IFilter
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.fluids.FluidInstance
import net.minecraftforge.fml.client.gui.GuiUtils


class GridTab(override val filters: List<IFilter<*>>, private val name: String, @field:Nonnull @param:Nonnull private val icon: ItemStack, @Nullable fluidIcon: FluidInstance) : IGridTab {
    @Nullable
    private val fluidIcon: FluidInstance
    override fun drawTooltip(matrixStack: MatrixStack?, x: Int, y: Int, screenWidth: Int, screenHeight: Int, fontRenderer: FontRenderer?) {
        if (name.trim { it <= ' ' } != "") {
            // TODO GuiUtils.drawHoveringText(matrixStack, Collections.singletonList(new StringTextComponent(name)), x, y, screenWidth, screenHeight, -1, fontRenderer);
        }
    }

    fun drawIcon(matrixStack: MatrixStack?, x: Int, y: Int, itemDrawer: IElementDrawer<ItemStack?>?, fluidDrawer: IElementDrawer<FluidInstance?>?) {
        if (!icon.isEmpty) {
            RenderHelper.setupGui3DDiffuseLighting()
            itemDrawer!!.draw(matrixStack, x, y, icon)
        } else {
            fluidDrawer!!.draw(matrixStack, x, y, fluidIcon)
            RenderSystem.enableAlphaTest()
        }
    }

    init {
        this.fluidIcon = fluidIcon
    }
}