package com.refinedmods.refinedstorage.util

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.render.Styles
import com.refinedmods.refinedstorage.screen.BaseScreen
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.util.IReorderingProcessor
import net.minecraft.util.math.vector.Matrix4f
import net.minecraft.util.text.*
import net.minecraftforge.client.event.RenderTooltipEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fluids.FluidInstance
import net.minecraftforge.fml.client.gui.GuiUtils
import java.util.*

object RenderUtils {
    fun shorten(text: String, length: Int): String {
        var text = text
        if (text.length > length) {
            text = text.substring(0, length) + "..."
        }
        return text
    }

    fun getOffsetOnScale(pos: Int, scale: Float): Int {
        val multiplier = pos / scale
        return multiplier.toInt()
    }

    fun addCombinedItemsToTooltip(tooltip: MutableList<Text?>, displayAmount: Boolean, stacks: List<ItemStack>) {
        val combinedIndices: MutableSet<Int> = HashSet()
        for (i in stacks.indices) {
            if (!stacks[i].isEmpty && !combinedIndices.contains(i)) {
                val stack = stacks[i]
                var data: IFormattableTextComponent = stack.getDisplayName().copyRaw()
                var amount = stack.count
                for (j in i + 1 until stacks.size) {
                    if (instance().getComparer()!!.isEqual(stack, stacks[j])) {
                        amount += stacks[j].count
                        combinedIndices.add(j)
                    }
                }
                if (displayAmount) {
                    data = StringTextComponent(amount.toString() + "x ").append(data)
                }
                tooltip.add(data.setStyle(Styles.GRAY))
            }
        }
    }

    fun addCombinedFluidsToTooltip(tooltip: MutableList<Text?>, displayMb: Boolean, stacks: List<FluidInstance>) {
        val combinedIndices: MutableSet<Int> = HashSet()
        for (i in stacks.indices) {
            if (!stacks[i].isEmpty() && !combinedIndices.contains(i)) {
                val stack: FluidInstance = stacks[i]
                var data: IFormattableTextComponent = stack.getDisplayName().copyRaw()
                var amount: Int = stack.getAmount()
                for (j in i + 1 until stacks.size) {
                    if (instance().getComparer().isEqual(stack, stacks[j], IComparer.COMPARE_NBT)) {
                        amount += stacks[j].getAmount()
                        combinedIndices.add(j)
                    }
                }
                if (displayMb) {
                    data = StringTextComponent(instance().getQuantityFormatter()!!.formatInBucketForm(amount) + " ").append(data)
                }
                tooltip.add(data.setStyle(Styles.GRAY))
            }
        }
    }

    // @Volatile: Copied with some tweaks from GuiUtils#drawHoveringText(@Nonnull final ItemStack stack, List<String> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font)
    fun drawTooltipWithSmallText(matrixStack: MatrixStack, textLines: List<ITextProperties?>, smallTextLines: List<String>, showSmallText: Boolean, @Nonnull stack: ItemStack?, mouseX: Int, mouseY: Int, screenWidth: Int, screenHeight: Int, fontRenderer: FontRenderer?) {
        // RS begin - definitions
        var textLines: List<ITextProperties?> = textLines
        var mouseX = mouseX
        var mouseY = mouseY
        var screenWidth = screenWidth
        var screenHeight = screenHeight
        var maxTextWidth = -1
        var font: FontRenderer = Minecraft.getInstance().fontRenderer
        val textScale = if (Minecraft.getInstance().getForceUnicodeFont()) 1f else 0.7f
        // RS end
        if (!textLines.isEmpty()) {
            val event: RenderTooltipEvent.Pre = Pre(stack, textLines, matrixStack, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, font)
            if (MinecraftForge.EVENT_BUS.post(event)) return
            mouseX = event.getX()
            mouseY = event.getY()
            screenWidth = event.getScreenWidth()
            screenHeight = event.getScreenHeight()
            maxTextWidth = event.getMaxWidth()
            font = event.getFontRenderer()
            RenderSystem.disableRescaleNormal()
            RenderSystem.disableDepthTest()
            var tooltipTextWidth = 0
            for (textLine in textLines) {
                val textLineWidth: Int = font.getStringWidth(textLine.getString())
                if (textLineWidth > tooltipTextWidth) tooltipTextWidth = textLineWidth
            }

            // RS BEGIN
            if (showSmallText) {
                for (smallText in smallTextLines) {
                    val size = (font.getStringWidth(smallText) * textScale) as Int
                    if (size > tooltipTextWidth) {
                        tooltipTextWidth = size
                    }
                }
            }
            // RS END
            var needsWrap = false
            var titleLinesCount = 1
            var tooltipX = mouseX + 12
            if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
                tooltipX = mouseX - 16 - tooltipTextWidth
                if (tooltipX < 4) // if the tooltip doesn't fit on the screen
                {
                    tooltipTextWidth = if (mouseX > screenWidth / 2) mouseX - 12 - 8 else screenWidth - 16 - mouseX
                    needsWrap = true
                }
            }
            if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
                tooltipTextWidth = maxTextWidth
                needsWrap = true
            }
            if (needsWrap) {
                var wrappedTooltipWidth = 0
                val wrappedTextLines: MutableList<ITextProperties?> = ArrayList<ITextProperties?>()
                for (i in textLines.indices) {
                    val textLine: ITextProperties? = textLines[i]
                    val wrappedLine: List<ITextProperties> = font.func_238420_b_().func_238362_b_(textLine, tooltipTextWidth, Style.EMPTY)
                    if (i == 0) titleLinesCount = wrappedLine.size
                    for (line in wrappedLine) {
                        val lineWidth: Int = font.getStringWidth(line.getString())
                        if (lineWidth > wrappedTooltipWidth) wrappedTooltipWidth = lineWidth
                        wrappedTextLines.add(line)
                    }
                }
                tooltipTextWidth = wrappedTooltipWidth
                textLines = wrappedTextLines
                tooltipX = if (mouseX > screenWidth / 2) mouseX - 16 - tooltipTextWidth else mouseX + 12
            }
            var tooltipY = mouseY - 12
            var tooltipHeight = 8
            if (textLines.size > 1) {
                tooltipHeight += (textLines.size - 1) * 10
                if (textLines.size > titleLinesCount) tooltipHeight += 2 // gap between title lines and next lines
            }

            // RS BEGIN
            if (showSmallText) {
                tooltipHeight += smallTextLines.size * 10
            }
            // RS END
            if (tooltipY < 4) tooltipY = 4 else if (tooltipY + tooltipHeight + 4 > screenHeight) tooltipY = screenHeight - tooltipHeight - 4
            val zLevel = BaseScreen.Z_LEVEL_TOOLTIPS
            var backgroundColor = -0xfeffff0
            var borderColorStart = 0x505000FF
            var borderColorEnd = borderColorStart and 0xFEFEFE shr 1 or borderColorStart and -0x1000000
            val colorEvent: RenderTooltipEvent.Color = Color(stack, textLines, matrixStack, tooltipX, tooltipY, font, backgroundColor, borderColorStart, borderColorEnd)
            MinecraftForge.EVENT_BUS.post(colorEvent)
            backgroundColor = colorEvent.getBackground()
            borderColorStart = colorEvent.getBorderStart()
            borderColorEnd = colorEvent.getBorderEnd()
            val matrix: Matrix4f = matrixStack.getLast().getMatrix()
            GuiUtils.drawGradientRect(matrix, zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor)
            GuiUtils.drawGradientRect(matrix, zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor)
            GuiUtils.drawGradientRect(matrix, zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor)
            GuiUtils.drawGradientRect(matrix, zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor)
            GuiUtils.drawGradientRect(matrix, zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor)
            GuiUtils.drawGradientRect(matrix, zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd)
            GuiUtils.drawGradientRect(matrix, zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd)
            GuiUtils.drawGradientRect(matrix, zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart)
            GuiUtils.drawGradientRect(matrix, zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd)
            MinecraftForge.EVENT_BUS.post(PostBackground(stack, textLines, matrixStack, tooltipX, tooltipY, font, tooltipTextWidth, tooltipHeight))
            val renderType: IRenderTypeBuffer.Impl = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer())
            val textStack = MatrixStack()
            textStack.translate(0.0, 0.0, zLevel)
            val textLocation: Matrix4f = textStack.getLast().getMatrix()
            val tooltipTop = tooltipY
            for (lineNumber in textLines.indices) {
                val line: ITextProperties? = textLines[lineNumber]
                if (line != null) font.func_238416_a_(LanguageMap.getInstance().func_241870_a(line), tooltipX.toFloat(), tooltipY.toFloat(), -1, true, textLocation, renderType, false, 0, 15728880)
                if (lineNumber + 1 == titleLinesCount) tooltipY += 2
                tooltipY += 10
            }
            renderType.finish()
            MinecraftForge.EVENT_BUS.post(PostText(stack, textLines, matrixStack, tooltipX, tooltipTop, font, tooltipTextWidth, tooltipHeight))

            // RS BEGIN
            if (showSmallText) {
                var y = tooltipTop + tooltipHeight - 6
                for (i in smallTextLines.indices.reversed()) {
                    // This is FontRenderer#drawStringWithShadow but with a custom MatrixStack
                    RenderSystem.enableAlphaTest()

                    // FontRenderer#drawStringWithShadow - call to func_228078_a_ (private)
                    val smallTextStack = MatrixStack()
                    smallTextStack.translate(0.0, 0.0, zLevel)
                    smallTextStack.scale(textScale, textScale, 1)
                    val lvt_7_1_: IRenderTypeBuffer.Impl = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer())
                    font.renderString(
                            TextFormatting.GRAY + smallTextLines[i],
                            getOffsetOnScale(tooltipX, textScale),
                            getOffsetOnScale(y - if (Minecraft.getInstance().getForceUnicodeFont()) 2 else 0, textScale),
                            -1,
                            true,
                            smallTextStack.getLast().getMatrix(),
                            lvt_7_1_,
                            false,
                            0,
                            15728880
                    )
                    lvt_7_1_.finish()
                    y -= 9
                }
            }
            // RS END
            RenderSystem.enableDepthTest()
            RenderSystem.enableRescaleNormal()
        }
    }

    // @Volatile: From Screen#getTooltipFromItem
    fun getTooltipFromItem(stack: ItemStack): List<Text> {
        val minecraft: Minecraft = Minecraft.getInstance()
        return stack.getTooltip(minecraft.player, if (minecraft.gameSettings.advancedItemTooltips) ITooltipFlag.TooltipFlags.ADVANCED else ITooltipFlag.TooltipFlags.NORMAL)
    }

    fun inBounds(x: Int, y: Int, w: Int, h: Int, ox: Double, oy: Double): Boolean {
        return ox >= x && ox <= x + w && oy >= y && oy <= y + h
    }
}