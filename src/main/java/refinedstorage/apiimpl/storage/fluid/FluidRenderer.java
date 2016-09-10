package refinedstorage.apiimpl.storage.fluid;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * This fluid renderer is copied over from JEI because Forge lacks a utility method for rendering fluids.
 *
 * @link https://github.com/mezz/JustEnoughItems/blob/1.10/src/main/java/mezz/jei/gui/ingredients/FluidStackRenderer.java
 */
public class FluidRenderer {
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

        VertexBuffer vertexBuffer = tessellator.getBuffer();
        vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexBuffer.pos(xCoord, yCoord + 16, zLevel).tex(uMin, vMax).endVertex();
        vertexBuffer.pos(xCoord + 16 - maskRight, yCoord + 16, zLevel).tex(uMax, vMax).endVertex();
        vertexBuffer.pos(xCoord + 16 - maskRight, yCoord + maskTop, zLevel).tex(uMax, vMin).endVertex();
        vertexBuffer.pos(xCoord, yCoord + maskTop, zLevel).tex(uMin, vMin).endVertex();
        tessellator.draw();
    }
}
