package com.raoulvdberge.refinedstorage.util;

import com.google.common.collect.ImmutableMap;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.config.GuiUtils;

import javax.annotation.Nonnull;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class RenderUtils {
    public static final Matrix4f EMPTY_MATRIX_TRANSFORM = getTransform(0, 0, 0, 0, 0, 0, 1.0f).getMatrix();

    // @Volatile: From ForgeBlockStateV1
    private static final TRSRTransformation FLIP_X = new TRSRTransformation(null, null, new Vector3f(-1, 1, 1), null);

    private static ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> DEFAULT_ITEM_TRANSFORM;
    private static ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> DEFAULT_BLOCK_TRANSFORM;

    private static final VertexFormat ITEM_FORMAT_WITH_LIGHTMAP = new VertexFormat(DefaultVertexFormats.ITEM).addElement(DefaultVertexFormats.TEX_2S);

    public static String shorten(String text, int length) {
        if (text.length() > length) {
            text = text.substring(0, length) + "...";
        }
        return text;
    }

    private static void setGLColorFromInt(int color) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        GlStateManager.color(red, green, blue, 1.0F);
    }

    private static void drawFluidTexture(double xCoord, double yCoord, TextureAtlasSprite textureSprite, int maskTop, int maskRight, double zLevel) {
        double uMin = (double) textureSprite.getMinU();
        double uMax = (double) textureSprite.getMaxU();
        double vMin = (double) textureSprite.getMinV();
        double vMax = (double) textureSprite.getMaxV();
        uMax = uMax - (maskRight / 16.0 * (uMax - uMin));
        vMax = vMax - (maskTop / 16.0 * (vMax - vMin));

        Tessellator tessellator = Tessellator.getInstance();

        BufferBuilder vertexBuffer = tessellator.getBuffer();
        vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexBuffer.pos(xCoord, yCoord + 16, zLevel).tex(uMin, vMax).endVertex();
        vertexBuffer.pos(xCoord + 16 - maskRight, yCoord + 16, zLevel).tex(uMax, vMax).endVertex();
        vertexBuffer.pos(xCoord + 16 - maskRight, yCoord + maskTop, zLevel).tex(uMax, vMin).endVertex();
        vertexBuffer.pos(xCoord, yCoord + maskTop, zLevel).tex(uMin, vMin).endVertex();
        tessellator.draw();
    }

    private static TRSRTransformation leftifyTransform(TRSRTransformation transform) {
        return TRSRTransformation.blockCenterToCorner(FLIP_X.compose(TRSRTransformation.blockCornerToCenter(transform)).compose(FLIP_X));
    }

    private static TRSRTransformation getTransform(float tx, float ty, float tz, float ax, float ay, float az, float s) {
        return new TRSRTransformation(
            new Vector3f(tx / 16, ty / 16, tz / 16),
            TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)),
            new Vector3f(s, s, s),
            null
        );
    }

    public static ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> getDefaultItemTransforms() {
        if (DEFAULT_ITEM_TRANSFORM != null) {
            return DEFAULT_ITEM_TRANSFORM;
        }

        return DEFAULT_ITEM_TRANSFORM = ImmutableMap.<ItemCameraTransforms.TransformType, TRSRTransformation>builder()
            .put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, getTransform(0, 3, 1, 0, 0, 0, 0.55f))
            .put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, getTransform(0, 3, 1, 0, 0, 0, 0.55f))
            .put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, getTransform(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f))
            .put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, getTransform(1.13f, 3.2f, 1.13f, 0, 90, -25, 0.68f))
            .put(ItemCameraTransforms.TransformType.GROUND, getTransform(0, 2, 0, 0, 0, 0, 0.5f))
            .put(ItemCameraTransforms.TransformType.HEAD, getTransform(0, 13, 7, 0, 180, 0, 1))
            .build();
    }

    public static ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> getDefaultBlockTransforms() {
        if (DEFAULT_BLOCK_TRANSFORM != null) {
            return DEFAULT_BLOCK_TRANSFORM;
        }

        TRSRTransformation thirdperson = getTransform(0, 2.5f, 0, 75, 45, 0, 0.375f);

        return DEFAULT_BLOCK_TRANSFORM = ImmutableMap.<ItemCameraTransforms.TransformType, TRSRTransformation>builder()
            .put(ItemCameraTransforms.TransformType.GUI, getTransform(0, 0, 0, 30, 225, 0, 0.625f))
            .put(ItemCameraTransforms.TransformType.GROUND, getTransform(0, 3, 0, 0, 0, 0, 0.25f))
            .put(ItemCameraTransforms.TransformType.FIXED, getTransform(0, 0, 0, 0, 0, 0, 0.5f))
            .put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson)
            .put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, leftifyTransform(thirdperson))
            .put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, getTransform(0, 0, 0, 0, 45, 0, 0.4f))
            .put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, getTransform(0, 0, 0, 0, 225, 0, 0.4f))
            .build();
    }

    public static int getOffsetOnScale(int pos, float scale) {
        float multiplier = (pos / scale);

        return (int) multiplier;
    }

    public static class FluidRenderer {
        private static final int TEX_WIDTH = 16;
        private static final int TEX_HEIGHT = 16;
        private static final int MIN_FLUID_HEIGHT = 1;

        private final int capacityMb;
        private final int width;
        private final int height;

        public FluidRenderer(int capacityMb, int width, int height) {
            this.capacityMb = capacityMb;
            this.width = width;
            this.height = height;
        }

        public void draw(Minecraft minecraft, int xPosition, int yPosition, FluidStack fluidStack) {
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.disableLighting();

            drawFluid(minecraft, xPosition, yPosition, fluidStack);

            GlStateManager.color(1, 1, 1, 1);

            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
        }

        private void drawFluid(Minecraft minecraft, int xPosition, int yPosition, FluidStack fluidStack) {
            if (fluidStack == null) {
                return;
            }

            Fluid fluid = fluidStack.getFluid();

            if (fluid == null) {
                return;
            }

            TextureMap textureMapBlocks = minecraft.getTextureMapBlocks();
            ResourceLocation fluidStill = fluid.getStill();
            TextureAtlasSprite fluidStillSprite = null;

            if (fluidStill != null) {
                fluidStillSprite = textureMapBlocks.getTextureExtry(fluidStill.toString());
            }

            if (fluidStillSprite == null) {
                fluidStillSprite = textureMapBlocks.getMissingSprite();
            }

            int fluidColor = fluid.getColor(fluidStack);

            int scaledAmount = height;

            if (capacityMb != -1) {
                scaledAmount = (fluidStack.amount * height) / capacityMb;

                if (fluidStack.amount > 0 && scaledAmount < MIN_FLUID_HEIGHT) {
                    scaledAmount = MIN_FLUID_HEIGHT;
                }

                if (scaledAmount > height) {
                    scaledAmount = height;
                }
            }

            minecraft.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            setGLColorFromInt(fluidColor);

            int xTileCount = width / TEX_WIDTH;
            int xRemainder = width - (xTileCount * TEX_WIDTH);
            int yTileCount = scaledAmount / TEX_HEIGHT;
            int yRemainder = scaledAmount - (yTileCount * TEX_HEIGHT);

            int yStart = yPosition + height;

            for (int xTile = 0; xTile <= xTileCount; xTile++) {
                for (int yTile = 0; yTile <= yTileCount; yTile++) {
                    int width = (xTile == xTileCount) ? xRemainder : TEX_WIDTH;
                    int height = (yTile == yTileCount) ? yRemainder : TEX_HEIGHT;
                    int x = xPosition + (xTile * TEX_WIDTH);
                    int y = yStart - ((yTile + 1) * TEX_HEIGHT);

                    if (width > 0 && height > 0) {
                        int maskTop = TEX_HEIGHT - height;
                        int maskRight = TEX_WIDTH - width;

                        drawFluidTexture(x, y, fluidStillSprite, maskTop, maskRight, 100);
                    }
                }
            }
        }
    }

    public static void addCombinedItemsToTooltip(List<String> tooltip, boolean displayAmount, List<ItemStack> stacks) {
        Set<Integer> combinedIndices = new HashSet<>();

        for (int i = 0; i < stacks.size(); ++i) {
            if (!stacks.get(i).isEmpty() && !combinedIndices.contains(i)) {
                ItemStack stack = stacks.get(i);

                String data = stack.getDisplayName();

                int amount = stack.getCount();

                for (int j = i + 1; j < stacks.size(); ++j) {
                    if (API.instance().getComparer().isEqual(stack, stacks.get(j))) {
                        amount += stacks.get(j).getCount();

                        combinedIndices.add(j);
                    }
                }

                data = (displayAmount ? (String.valueOf(amount) + "x ") : "") + data;

                tooltip.add(data);
            }
        }
    }

    public static void addCombinedFluidsToTooltip(List<String> tooltip, boolean showMb, NonNullList<FluidStack> stacks) {
        Set<Integer> combinedIndices = new HashSet<>();

        for (int i = 0; i < stacks.size(); ++i) {
            if (!combinedIndices.contains(i)) {
                FluidStack stack = stacks.get(i);

                String data = stack.getLocalizedName();

                int amount = stack.amount;

                for (int j = i + 1; j < stacks.size(); ++j) {
                    if (API.instance().getComparer().isEqual(stack, stacks.get(j), IComparer.COMPARE_NBT)) {
                        amount += stacks.get(j).amount;

                        combinedIndices.add(j);
                    }
                }

                tooltip.add((showMb ? (API.instance().getQuantityFormatter().formatInBucketForm(amount) + " ") : "") + data);
            }
        }
    }

    // @Volatile: Copied with some tweaks from GuiUtils#drawHoveringText(@Nonnull final ItemStack stack, List<String> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font)
    public static void drawTooltipWithSmallText(List<String> textLines, List<String> smallTextLines, boolean showSmallText, @Nonnull ItemStack stack, int mouseX, int mouseY, int screenWidth, int screenHeight, FontRenderer fontRenderer) {
        if (!textLines.isEmpty()) {
            RenderTooltipEvent.Pre event = new RenderTooltipEvent.Pre(stack, textLines, mouseX, mouseY, screenWidth, screenHeight, -1, fontRenderer);
            if (MinecraftForge.EVENT_BUS.post(event)) {
                return;
            }

            mouseX = event.getX();
            mouseY = event.getY();

            screenWidth = event.getScreenWidth();
            screenHeight = event.getScreenHeight();

            FontRenderer font = event.getFontRenderer();

            // RS BEGIN
            float textScale = font.getUnicodeFlag() ? 1F : 0.7F;
            // RS END

            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int tooltipTextWidth = 0;

            for (String textLine : textLines) {
                int textLineWidth = font.getStringWidth(textLine);

                if (textLineWidth > tooltipTextWidth) {
                    tooltipTextWidth = textLineWidth;
                }
            }

            // RS BEGIN
            if (showSmallText) {
                int size;

                for (String smallText : smallTextLines) {
                    size = (int) (font.getStringWidth(smallText) * textScale);

                    if (size > tooltipTextWidth) {
                        tooltipTextWidth = size;
                    }
                }
            }
            // RS END

            int titleLinesCount = 1;
            int tooltipX = mouseX + 12;

            int tooltipY = mouseY - 12;
            int tooltipHeight = 8;

            if (textLines.size() > 1) {
                tooltipHeight += (textLines.size() - 1) * 10;
                if (textLines.size() > titleLinesCount) {
                    tooltipHeight += 2;
                }
            }

            // RS BEGIN
            if (showSmallText) {
                tooltipHeight += smallTextLines.size() * 10;
            }
            // RS END

            if (tooltipY + tooltipHeight + 6 > screenHeight) {
                tooltipY = screenHeight - tooltipHeight - 6;
            }

            final int zLevel = 300;
            final int backgroundColor = 0xF0100010;
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            final int borderColorStart = 0x505000FF;
            final int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            GuiUtils.drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);

            MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostBackground(stack, textLines, tooltipX, tooltipY, font, tooltipTextWidth, tooltipHeight));
            int tooltipTop = tooltipY;

            for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber) {
                String line = textLines.get(lineNumber);
                font.drawStringWithShadow(line, (float) tooltipX, (float) tooltipY, -1);

                if (lineNumber + 1 == titleLinesCount) {
                    tooltipY += 2;
                }

                tooltipY += 10;
            }

            MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostText(stack, textLines, tooltipX, tooltipTop, font, tooltipTextWidth, tooltipHeight));

            // RS BEGIN
            if (showSmallText) {
                GlStateManager.pushMatrix();
                GlStateManager.scale(textScale, textScale, 1);

                int y = tooltipTop + tooltipHeight - 6;

                for (int i = smallTextLines.size() - 1; i >= 0; --i) {
                    font.drawStringWithShadow(
                        TextFormatting.GRAY + smallTextLines.get(i),
                        RenderUtils.getOffsetOnScale(tooltipX, textScale),
                        RenderUtils.getOffsetOnScale(y - (font.getUnicodeFlag() ? 2 : 0), textScale),
                        -1
                    );

                    y -= 9;
                }

                GlStateManager.popMatrix();
            }
            // RS END

            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    // @Volatile: From GuiScreen#getItemToolTip
    public static List<String> getItemTooltip(ItemStack stack) {
        List<String> lines = stack.getTooltip(Minecraft.getMinecraft().player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);

        for (int i = 0; i < lines.size(); ++i) {
            if (i == 0) {
                lines.set(i, stack.getItem().getForgeRarity(stack).getColor() + lines.get(i));
            } else {
                lines.set(i, TextFormatting.GRAY + lines.get(i));
            }
        }

        return lines;
    }

    public static boolean isLightMapDisabled() {
        return FMLClientHandler.instance().hasOptifine() || !ForgeModContainer.forgeLightPipelineEnabled;
    }

    public static VertexFormat getFormatWithLightMap(VertexFormat format) {
        if (isLightMapDisabled()) {
            return format;
        }

        if (format == DefaultVertexFormats.BLOCK) {
            return DefaultVertexFormats.BLOCK;
        } else if (format == DefaultVertexFormats.ITEM) {
            return ITEM_FORMAT_WITH_LIGHTMAP;
        } else if (!format.hasUvOffset(1)) {
            VertexFormat result = new VertexFormat(format);

            result.addElement(DefaultVertexFormats.TEX_2S);

            return result;
        }

        return format;
    }

    public static TextureAtlasSprite getSprite(IBakedModel coverModel, IBlockState coverState, EnumFacing facing, long rand) {
        TextureAtlasSprite sprite = null;

        BlockRenderLayer originalLayer = MinecraftForgeClient.getRenderLayer();

        try {
            for (BlockRenderLayer layer : BlockRenderLayer.values()) {
                ForgeHooksClient.setRenderLayer(layer);

                for (BakedQuad bakedQuad : coverModel.getQuads(coverState, facing, rand)) {
                    return bakedQuad.getSprite();
                }

                for (BakedQuad bakedQuad : coverModel.getQuads(coverState, null, rand)) {
                    if (sprite == null) {
                        sprite = bakedQuad.getSprite();
                    }

                    if (bakedQuad.getFace() == facing) {
                        return bakedQuad.getSprite();
                    }
                }
            }
        } catch (Exception e) {
            // NO OP
        } finally {
            ForgeHooksClient.setRenderLayer(originalLayer);
        }

        if (sprite == null) {
            try {
                sprite = coverModel.getParticleTexture();
            } catch (Exception e) {
                // NO OP
            }
        }

        if (sprite == null) {
            sprite = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
        }

        return sprite;
    }
}
