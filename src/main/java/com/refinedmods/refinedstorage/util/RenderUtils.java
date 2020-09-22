package com.refinedmods.refinedstorage.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.render.Styles;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.*;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.gui.GuiUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class RenderUtils {
    public static String shorten(String text, int length) {
        if (text.length() > length) {
            text = text.substring(0, length) + "...";
        }
        return text;
    }

    public static int getOffsetOnScale(int pos, float scale) {
        float multiplier = (pos / scale);

        return (int) multiplier;
    }

    public static void addCombinedItemsToTooltip(List<ITextComponent> tooltip, boolean displayAmount, List<ItemStack> stacks) {
        Set<Integer> combinedIndices = new HashSet<>();

        for (int i = 0; i < stacks.size(); ++i) {
            if (!stacks.get(i).isEmpty() && !combinedIndices.contains(i)) {
                ItemStack stack = stacks.get(i);

                IFormattableTextComponent data = stack.getDisplayName().copyRaw();

                int amount = stack.getCount();

                for (int j = i + 1; j < stacks.size(); ++j) {
                    if (API.instance().getComparer().isEqual(stack, stacks.get(j))) {
                        amount += stacks.get(j).getCount();

                        combinedIndices.add(j);
                    }
                }

                if (displayAmount) {
                    data = new StringTextComponent(amount + "x ").append(data);
                }

                tooltip.add(data.setStyle(Styles.GRAY));
            }
        }
    }

    public static void addCombinedFluidsToTooltip(List<ITextComponent> tooltip, boolean displayMb, List<FluidStack> stacks) {
        Set<Integer> combinedIndices = new HashSet<>();

        for (int i = 0; i < stacks.size(); ++i) {
            if (!stacks.get(i).isEmpty() && !combinedIndices.contains(i)) {
                FluidStack stack = stacks.get(i);

                IFormattableTextComponent data = stack.getDisplayName().copyRaw();

                int amount = stack.getAmount();

                for (int j = i + 1; j < stacks.size(); ++j) {
                    if (API.instance().getComparer().isEqual(stack, stacks.get(j), IComparer.COMPARE_NBT)) {
                        amount += stacks.get(j).getAmount();

                        combinedIndices.add(j);
                    }
                }

                if (displayMb) {
                    data = new StringTextComponent(API.instance().getQuantityFormatter().formatInBucketForm(amount) + " ").append(data);
                }

                tooltip.add(data.setStyle(Styles.GRAY));
            }
        }
    }

    // @Volatile: Copied with some tweaks from GuiUtils#drawHoveringText(@Nonnull final ItemStack stack, List<String> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font)
    public static void drawTooltipWithSmallText(MatrixStack matrixStack, List<? extends ITextProperties> textLines, List<String> smallTextLines, boolean showSmallText, @Nonnull ItemStack stack, int mouseX, int mouseY, int screenWidth, int screenHeight, FontRenderer fontRenderer) {
        // RS begin - definitions
        int maxTextWidth = -1;
        FontRenderer font = Minecraft.getInstance().fontRenderer;
        float textScale = Minecraft.getInstance().getForceUnicodeFont() ? 1F : 0.7F;
        // RS end

        if (!textLines.isEmpty()) {
            RenderTooltipEvent.Pre event = new RenderTooltipEvent.Pre(stack, textLines, matrixStack, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, font);
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
                int textLineWidth = font.getStringWidth(textLine.getString());
                if (textLineWidth > tooltipTextWidth)
                    tooltipTextWidth = textLineWidth;
            }

            // RS BEGIN
            if (showSmallText) {
                for (String smallText : smallTextLines) {
                    int size = (int) (font.getStringWidth(smallText) * textScale);

                    if (size > tooltipTextWidth) {
                        tooltipTextWidth = size;
                    }
                }
            }
            // RS END

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
                        int lineWidth = font.getStringWidth(line.getString());
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

            // RS BEGIN
            if (showSmallText) {
                tooltipHeight += smallTextLines.size() * 10;
            }
            // RS END

            if (tooltipY < 4)
                tooltipY = 4;
            else if (tooltipY + tooltipHeight + 4 > screenHeight)
                tooltipY = screenHeight - tooltipHeight - 4;

            final int zLevel = BaseScreen.Z_LEVEL_TOOLTIPS;
            int backgroundColor = 0xF0100010;
            int borderColorStart = 0x505000FF;
            int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
            RenderTooltipEvent.Color colorEvent = new RenderTooltipEvent.Color(stack, textLines, matrixStack, tooltipX, tooltipY, font, backgroundColor, borderColorStart, borderColorEnd);
            MinecraftForge.EVENT_BUS.post(colorEvent);
            backgroundColor = colorEvent.getBackground();
            borderColorStart = colorEvent.getBorderStart();
            borderColorEnd = colorEvent.getBorderEnd();
            Matrix4f matrix = matrixStack.getLast().getMatrix();

            GuiUtils.drawGradientRect(matrix, zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(matrix, zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(matrix, zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(matrix, zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(matrix, zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(matrix, zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            GuiUtils.drawGradientRect(matrix, zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            GuiUtils.drawGradientRect(matrix, zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);
            GuiUtils.drawGradientRect(matrix, zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);

            MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostBackground(stack, textLines, matrixStack, tooltipX, tooltipY, font, tooltipTextWidth, tooltipHeight));

            IRenderTypeBuffer.Impl renderType = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
            MatrixStack textStack = new MatrixStack();
            textStack.translate(0.0D, 0.0D, zLevel);
            Matrix4f textLocation = textStack.getLast().getMatrix();

            int tooltipTop = tooltipY;

            for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber) {
                ITextProperties line = textLines.get(lineNumber);
                if (line != null)
                    font.func_238416_a_(LanguageMap.getInstance().func_241870_a(line), (float) tooltipX, (float) tooltipY, -1, true, textLocation, renderType, false, 0, 15728880);

                if (lineNumber + 1 == titleLinesCount)
                    tooltipY += 2;

                tooltipY += 10;
            }

            renderType.finish();

            MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostText(stack, textLines, matrixStack, tooltipX, tooltipTop, font, tooltipTextWidth, tooltipHeight));

            // RS BEGIN
            if (showSmallText) {
                int y = tooltipTop + tooltipHeight - 6;

                for (int i = smallTextLines.size() - 1; i >= 0; --i) {
                    // This is FontRenderer#drawStringWithShadow but with a custom MatrixStack

                    RenderSystem.enableAlphaTest();

                    // FontRenderer#drawStringWithShadow - call to func_228078_a_ (private)
                    MatrixStack smallTextStack = new MatrixStack();
                    smallTextStack.translate(0.0D, 0.0D, zLevel);
                    smallTextStack.scale(textScale, textScale, 1);

                    IRenderTypeBuffer.Impl lvt_7_1_ = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
                    font.renderString(
                        TextFormatting.GRAY + smallTextLines.get(i),
                        RenderUtils.getOffsetOnScale(tooltipX, textScale),
                        RenderUtils.getOffsetOnScale(y - (Minecraft.getInstance().getForceUnicodeFont() ? 2 : 0), textScale),
                        -1,
                        true,
                        smallTextStack.getLast().getMatrix(),
                        lvt_7_1_,
                        false,
                        0,
                        15728880
                    );

                    lvt_7_1_.finish();

                    y -= 9;
                }
            }
            // RS END

            RenderSystem.enableDepthTest();
            RenderSystem.enableRescaleNormal();
        }
    }

    // @Volatile: From Screen#getTooltipFromItem
    public static List<ITextComponent> getTooltipFromItem(ItemStack stack) {
        Minecraft minecraft = Minecraft.getInstance();
        return stack.getTooltip(minecraft.player, minecraft.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
    }

    public static boolean inBounds(int x, int y, int w, int h, double ox, double oy) {
        return ox >= x && ox <= x + w && oy >= y && oy <= y + h;
    }
}
