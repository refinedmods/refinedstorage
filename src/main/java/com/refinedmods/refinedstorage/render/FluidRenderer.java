package com.refinedmods.refinedstorage.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

/**
 * @link https://github.com/mezz/JustEnoughItems/blob/1.15/src/main/java/mezz/jei/plugins/vanilla/ingredients/fluid/FluidStackRenderer.java
 */
public class FluidRenderer {
    public static final FluidRenderer INSTANCE = new FluidRenderer(FluidAttributes.BUCKET_VOLUME, 16, 16, 16);

    private static final int TEX_WIDTH = 16;
    private static final int TEX_HEIGHT = 16;

    private final int capacityMb;
    private final int width;
    private final int height;
    private final int minHeight;

    public FluidRenderer(int capacityMb, int width, int height, int minHeight) {
        this.capacityMb = capacityMb;
        this.width = width;
        this.height = height;
        this.minHeight = minHeight;
    }

    public void render(MatrixStack matrixStack, final int xPosition, final int yPosition, @Nullable FluidStack fluidStack) {
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();

        drawFluid(matrixStack, xPosition, yPosition, fluidStack);

        RenderSystem.color4f(1, 1, 1, 1);

        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
    }

    private void drawFluid(MatrixStack matrixStack, final int xPosition, final int yPosition, @Nullable FluidStack fluidStack) {
        if (fluidStack == null) {
            return;
        }
        Fluid fluid = fluidStack.getFluid();
        if (fluid == null) {
            return;
        }

        TextureAtlasSprite fluidStillSprite = getStillFluidSprite(fluidStack);

        FluidAttributes attributes = fluid.getAttributes();
        int fluidColor = attributes.getColor(fluidStack);

        int amount = fluidStack.getAmount();
        int scaledAmount = (amount * height) / capacityMb;
        if (amount > 0 && scaledAmount < minHeight) {
            scaledAmount = minHeight;
        }
        if (scaledAmount > height) {
            scaledAmount = height;
        }

        drawTiledSprite(matrixStack, xPosition, yPosition, width, height, fluidColor, scaledAmount, fluidStillSprite);
    }

    private void drawTiledSprite(MatrixStack matrixStack, final int xPosition, final int yPosition, final int tiledWidth, final int tiledHeight, int color, int scaledAmount, TextureAtlasSprite sprite) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
        Matrix4f matrix = matrixStack.getLast().getMatrix();
        setGLColorFromInt(color);

        final int xTileCount = tiledWidth / TEX_WIDTH;
        final int xRemainder = tiledWidth - (xTileCount * TEX_WIDTH);
        final int yTileCount = scaledAmount / TEX_HEIGHT;
        final int yRemainder = scaledAmount - (yTileCount * TEX_HEIGHT);

        final int yStart = yPosition + tiledHeight;

        for (int xTile = 0; xTile <= xTileCount; xTile++) {
            for (int yTile = 0; yTile <= yTileCount; yTile++) {
                int width = (xTile == xTileCount) ? xRemainder : TEX_WIDTH;
                int height = (yTile == yTileCount) ? yRemainder : TEX_HEIGHT;
                int x = xPosition + (xTile * TEX_WIDTH);
                int y = yStart - ((yTile + 1) * TEX_HEIGHT);
                if (width > 0 && height > 0) {
                    int maskTop = TEX_HEIGHT - height;
                    int maskRight = TEX_WIDTH - width;

                    drawTextureWithMasking(matrix, x, y, sprite, maskTop, maskRight, 100);
                }
            }
        }
    }

    private static TextureAtlasSprite getStillFluidSprite(FluidStack fluidStack) {
        Minecraft minecraft = Minecraft.getInstance();
        Fluid fluid = fluidStack.getFluid();
        FluidAttributes attributes = fluid.getAttributes();
        ResourceLocation fluidStill = attributes.getStillTexture(fluidStack);
        return minecraft.getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(fluidStill);
    }

    private static void setGLColorFromInt(int color) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float alpha = ((color >> 24) & 0xFF) / 255F;

        RenderSystem.color4f(red, green, blue, alpha);
    }

    private static void drawTextureWithMasking(Matrix4f matrix, float xCoord, float yCoord, TextureAtlasSprite textureSprite, int maskTop, int maskRight, float zLevel) {
        float uMin = textureSprite.getMinU();
        float uMax = textureSprite.getMaxU();
        float vMin = textureSprite.getMinV();
        float vMax = textureSprite.getMaxV();
        uMax = uMax - (maskRight / 16F * (uMax - uMin));
        vMax = vMax - (maskTop / 16F * (vMax - vMin));

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferBuilder.pos(matrix, xCoord, yCoord + 16, zLevel).tex(uMin, vMax).endVertex();
        bufferBuilder.pos(matrix, xCoord + 16 - maskRight, yCoord + 16, zLevel).tex(uMax, vMax).endVertex();
        bufferBuilder.pos(matrix, xCoord + 16 - maskRight, yCoord + maskTop, zLevel).tex(uMax, vMin).endVertex();
        bufferBuilder.pos(matrix, xCoord, yCoord + maskTop, zLevel).tex(uMin, vMin).endVertex();
        tessellator.draw();
    }
}
