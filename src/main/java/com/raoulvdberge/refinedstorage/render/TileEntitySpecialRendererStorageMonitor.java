package com.raoulvdberge.refinedstorage.render;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.tile.TileStorageMonitor;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;

public class TileEntitySpecialRendererStorageMonitor extends TileEntitySpecialRenderer<TileStorageMonitor> {
    @Override
    public void renderTileEntityAt(TileStorageMonitor tile, double x, double y, double z, float partialTicks, int destroyStage) {
        setLightmapDisabled(true);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5F, y + 0.5F, z - 0.01F);
        GlStateManager.rotate(180F, 0F, 0F, 1F);
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.depthMask(true);
        GlStateManager.scale(0.4F, -0.4F, -0.015F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1F, 1F, 1F, 1F);

        if (tile.getItemStack() != null) {
            IBakedModel bakedModel = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(tile.getItemStack(), null, Minecraft.getMinecraft().player);
            bakedModel = ForgeHooksClient.handleCameraTransforms(bakedModel, ItemCameraTransforms.TransformType.GUI, false);
            Minecraft.getMinecraft().getRenderItem().renderItem(tile.getItemStack(), bakedModel);
        }

        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        GlStateManager.disableBlend();
        GlStateManager.color(1F, 1F, 1F, 1F);

        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        String amount = tile.getType() == IType.ITEMS ? RSUtils.formatQuantity(tile.getAmount()) : RSUtils.QUANTITY_FORMATTER.format((float) tile.getAmount() / 1000F);

        // Very bad, but I don't know how to translate a 2D font width to a 3D font width...
        float textWidth = 0;
        for (int i = 0; i < amount.length(); ++i) {
            char c = amount.charAt(i);
            if (c == '.') {
                textWidth += 0.005F;
            } else {
                textWidth += 0.024F;
            }
        }

        GlStateManager.translate(x + 0.5F + textWidth, y + 0.2F, z - 0.02F);
        GlStateManager.rotate(180F, 0F, 0F, 1F);
        float size = 0.00450F;
        float factor = 2.0f;
        GlStateManager.scale(size * factor, size * factor, size);

        Minecraft.getMinecraft().fontRendererObj.drawString(amount, 0, 0, 0xFFFFFF);

        GlStateManager.popMatrix();

        setLightmapDisabled(false);
    }
}
