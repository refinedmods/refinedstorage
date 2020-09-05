package com.refinedmods.refinedstorage.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.Style;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.gui.GuiUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.fml.client.gui.GuiUtils.drawGradientRect;

public class TemporaryPortingUtils {
    @Nonnull
    private static ItemStack cachedTooltipStack = ItemStack.EMPTY;

    public static void drawHoveringText(MatrixStack mStack, List<? extends ITextProperties> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font) {
        drawHoveringText(mStack, textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, GuiUtils.DEFAULT_BACKGROUND_COLOR, GuiUtils.DEFAULT_BORDER_COLOR_START, GuiUtils.DEFAULT_BORDER_COLOR_END, font);
    }

    /**
     * Draws a tooltip box on the screen with text in it.
     * Automatically positions the box relative to the mouse to match Mojang's implementation.
     * Automatically wraps text when there is not enough space on the screen to display the text without wrapping.
     * Can have a maximum width set to avoid creating very wide tooltips.
     *
     * @param textLines        the lines of text to be drawn in a hovering tooltip box.
     * @param mouseX           the mouse X position
     * @param mouseY           the mouse Y position
     * @param screenWidth      the available screen width for the tooltip to drawn in
     * @param screenHeight     the available  screen height for the tooltip to drawn in
     * @param maxTextWidth     the maximum width of the text in the tooltip box.
     *                         Set to a negative number to have no max width.
     * @param backgroundColor  The background color of the box
     * @param borderColorStart The starting color of the box border
     * @param borderColorEnd   The ending color of the box border. The border color will be smoothly interpolated
     *                         between the start and end values.
     * @param font             the font for drawing the text in the tooltip box
     */
    public static void drawHoveringText(MatrixStack mStack, List<? extends ITextProperties> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight,
                                        int maxTextWidth, int backgroundColor, int borderColorStart, int borderColorEnd, FontRenderer font) {
        drawHoveringText(cachedTooltipStack, mStack, textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, backgroundColor, borderColorStart, borderColorEnd, font);
    }

    public static void drawHoveringText(@Nonnull final ItemStack stack, MatrixStack mStack, List<? extends ITextProperties> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font) {
        drawHoveringText(stack, mStack, textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, GuiUtils.DEFAULT_BACKGROUND_COLOR, GuiUtils.DEFAULT_BORDER_COLOR_START, GuiUtils.DEFAULT_BORDER_COLOR_END, font);
    }

    /**
     * Use this version if calling from somewhere where ItemStack context is available.
     *
     * @see #drawHoveringText(MatrixStack, List, int, int, int, int, int, int, int, int, FontRenderer)
     */
    public static void drawHoveringText(@Nonnull final ItemStack stack, MatrixStack mStack, List<? extends ITextProperties> textLines, int mouseX, int mouseY,
                                        int screenWidth, int screenHeight, int maxTextWidth,
                                        int backgroundColor, int borderColorStart, int borderColorEnd, FontRenderer font) {
        if (!textLines.isEmpty()) {
            RenderTooltipEvent.Pre event = new RenderTooltipEvent.Pre(stack, textLines, mStack, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, font);
            if (MinecraftForge.EVENT_BUS.post(event))
                return;
            mouseX = event.getX();
            mouseY = event.getY();
            screenWidth = event.getScreenWidth();
            screenHeight = event.getScreenHeight();
            maxTextWidth = event.getMaxWidth();
            font = event.getFontRenderer();

            RenderSystem.disableRescaleNormal();
            RenderSystem.disableDepthTest();
            int tooltipTextWidth = 0;

            for (ITextProperties textLine : textLines) {
                int textLineWidth = font.func_238414_a_(textLine);
                if (textLineWidth > tooltipTextWidth)
                    tooltipTextWidth = textLineWidth;
            }

            boolean needsWrap = false;

            int titleLinesCount = 1;
            int tooltipX = mouseX + 12;
            if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
                tooltipX = mouseX - 16 - tooltipTextWidth;
                if (tooltipX < 4) // if the tooltip doesn't fit on the screen
                {
                    if (mouseX > screenWidth / 2)
                        tooltipTextWidth = mouseX - 12 - 8;
                    else
                        tooltipTextWidth = screenWidth - 16 - mouseX;
                    needsWrap = true;
                }
            }

            if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
                tooltipTextWidth = maxTextWidth;
                needsWrap = true;
            }

            if (needsWrap) {
                int wrappedTooltipWidth = 0;
                List<ITextProperties> wrappedTextLines = new ArrayList<>();
                for (int i = 0; i < textLines.size(); i++) {
                    ITextProperties textLine = textLines.get(i);
                    List<ITextProperties> wrappedLine = font.func_238420_b_().func_238362_b_(textLine, tooltipTextWidth, Style.EMPTY);
                    if (i == 0)
                        titleLinesCount = wrappedLine.size();

                    for (ITextProperties line : wrappedLine) {
                        int lineWidth = font.func_238414_a_(line);
                        if (lineWidth > wrappedTooltipWidth)
                            wrappedTooltipWidth = lineWidth;
                        wrappedTextLines.add(line);
                    }
                }
                tooltipTextWidth = wrappedTooltipWidth;
                textLines = wrappedTextLines;

                if (mouseX > screenWidth / 2)
                    tooltipX = mouseX - 16 - tooltipTextWidth;
                else
                    tooltipX = mouseX + 12;
            }

            int tooltipY = mouseY - 12;
            int tooltipHeight = 8;

            if (textLines.size() > 1) {
                tooltipHeight += (textLines.size() - 1) * 10;
                if (textLines.size() > titleLinesCount)
                    tooltipHeight += 2; // gap between title lines and next lines
            }

            if (tooltipY < 4)
                tooltipY = 4;
            else if (tooltipY + tooltipHeight + 4 > screenHeight)
                tooltipY = screenHeight - tooltipHeight - 4;

            final int zLevel = 400;
            RenderTooltipEvent.Color colorEvent = new RenderTooltipEvent.Color(stack, textLines, mStack, tooltipX, tooltipY, font, backgroundColor, borderColorStart, borderColorEnd);
            MinecraftForge.EVENT_BUS.post(colorEvent);
            backgroundColor = colorEvent.getBackground();
            borderColorStart = colorEvent.getBorderStart();
            borderColorEnd = colorEvent.getBorderEnd();

            mStack.push();
            Matrix4f mat = mStack.getLast().getMatrix();

            drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
            drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
            drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            drawGradientRect(mat, zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            drawGradientRect(mat, zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            drawGradientRect(mat, zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);
            drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);

            MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostBackground(stack, textLines, mStack, tooltipX, tooltipY, font, tooltipTextWidth, tooltipHeight));

            IRenderTypeBuffer.Impl renderType = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
            mStack.translate(0.0D, 0.0D, zLevel);

            int tooltipTop = tooltipY;

            for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber) {
                ITextProperties line = textLines.get(lineNumber);
                if (line != null)
                    font.func_238416_a_(LanguageMap.getInstance().func_241870_a(line), (float) tooltipX, (float) tooltipY, -1, true, mat, renderType, false, 0, 15728880);

                if (lineNumber + 1 == titleLinesCount)
                    tooltipY += 2;

                tooltipY += 10;
            }

            renderType.finish();
            mStack.pop();

            MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostText(stack, textLines, mStack, tooltipX, tooltipTop, font, tooltipTextWidth, tooltipHeight));

            RenderSystem.enableDepthTest();
            RenderSystem.enableRescaleNormal();
        }
    }
}

