package com.raoulvdberge.refinedstorage.render.tesr;

import com.mojang.blaze3d.platform.GlStateManager;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.tile.TileStorageMonitor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.Direction;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;

public class TileEntitySpecialRendererStorageMonitor extends TileEntityRenderer<TileStorageMonitor> {
    @Override
    public void render(TileStorageMonitor tile, double x, double y, double z, float partialTicks, int destroyStage) {
        setLightmapDisabled(true);

        float disX = 0, disXText = 0;
        float disY = 0.5F, disYText = 0.23F;
        float disZ = 0, disZText = 0;
        float spacing = 0.01F;

        float rotX = 0;
        float rotY = 0;
        float rotZ = 0;

        String amount = API.instance().getQuantityFormatter().formatWithUnits(tile.getAmount());

        // Very bad, but I don't know how to translate a 2D font width to a 3D font width...
        float textWidth = 0;
        for (int i = 0; i < amount.length(); ++i) {
            char c = amount.charAt(i);
            if (c == '.') {
                textWidth += 0.005F;
            } else {
                textWidth += 0.026F;
            }
        }

        if (tile.getDirection() == Direction.NORTH) {
            disX = 0.5F;
            disXText = disX + textWidth;

            disZ = -spacing;
            disZText = disZ - spacing;

            rotZ = 1F;
        } else if (tile.getDirection() == Direction.WEST) {
            disX = -spacing;
            disXText = disX - spacing;

            disZ = 0.5F;
            disZText = disZ - textWidth;

            rotZ = 1F;
            rotX = 1F;
        } else if (tile.getDirection() == Direction.SOUTH) {
            disX = 0.5F;
            disXText = disX - textWidth;

            disZ = 1F + spacing;
            disZText = disZ + spacing;

            rotX = 1F;
        } else if (tile.getDirection() == Direction.EAST) {
            disX = 1F + spacing;
            disXText = disX + spacing;

            disZ = 0.5F;
            disZText = disZ + textWidth;

            rotZ = 1F;
            rotX = -1F;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translated(x + disX, y + disY, z + disZ);
        GlStateManager.rotated(180F, rotX, rotY, rotZ);
        GlStateManager.color4f(1F, 1F, 1F, 1F);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.depthMask(true);
        GlStateManager.scalef(0.4F, -0.4F, -0.015F);
        // TODO Minecraft.getInstance().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        // TODO Minecraft.getInstance().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlphaTest();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color4f(1F, 1F, 1F, 1F);

        if (tile.getItemStack() != null) {
            IBakedModel bakedModel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(tile.getItemStack(), null, Minecraft.getInstance().player);
            bakedModel = ForgeHooksClient.handleCameraTransforms(bakedModel, ItemCameraTransforms.TransformType.GUI, false);
            Minecraft.getInstance().getItemRenderer().renderItem(tile.getItemStack(), bakedModel);
        }

        GlStateManager.disableAlphaTest();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        // TODO Minecraft.getInstance().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        // TODO Minecraft.getInstance().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        GlStateManager.disableBlend();
        GlStateManager.color4f(1F, 1F, 1F, 1F);

        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();

        GlStateManager.translated(x + disXText, y + disYText, z + disZText);
        GlStateManager.rotated(180F, rotX, rotY, rotZ);
        float size = 0.00450F;
        float factor = 2.0f;
        GlStateManager.scaled(size * factor, size * factor, size);

        if (tile.getItemStack() != null) {
            Minecraft.getInstance().fontRenderer.drawString(amount, 0, 0, 0xFFFFFF);
        }

        GlStateManager.popMatrix();

        setLightmapDisabled(false);
    }
}
