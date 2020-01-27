package com.raoulvdberge.refinedstorage.render.tesr;

import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.tile.TileStorageMonitor;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class TileEntitySpecialRendererStorageMonitor extends TileEntitySpecialRenderer<TileStorageMonitor> {

    @Override
    public void render(TileStorageMonitor tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        setLightmapDisabled(true);

        float disX = 0, disXText = 0;
        float disY = 0.5F, disYText = 0.23F;
        float disZ = 0, disZText = 0;
        float fdisX = 0;
        float fdisY = 0.5F - 1 / 4F;
        float fdisZ = 0;
        float spacing = 0.01F;

        float rotX = 0;
        float rotY = 0;
        float rotZ = 0;

        float frotX = 0;
        float frotY = 0;
        float frotZ = 0;
        int type = tile.getType();
        int amount = tile.getAmount();
        String amountString = type == IType.ITEMS ? API.instance().getQuantityFormatter().formatWithUnits(amount) :
                API.instance().getQuantityFormatter().formatInBucketFormWithOnlyTrailingDigitsIfZero(amount);

        // Very bad, but I don't know how to translate a 2D font width to a 3D font width...
        float textWidth = 0;
        for (int i = 0; i < amountString.length(); ++i) {
            char c = amountString.charAt(i);
            if (c == '.') {
                textWidth += 0.005F;
            } else {
                textWidth += 0.026F;
            }
        }

        if (tile.getDirection() == EnumFacing.NORTH) {
            disX = 0.5F;
            disXText = disX + textWidth;

            disZ = -spacing;
            disZText = disZ - spacing;

            rotZ = 1F;

            frotZ = 0F;
            frotX = -1F;
            fdisZ = -1.5F - spacing;
            fdisX = 0.5F - 1 / 4F;

        } else if (tile.getDirection() == EnumFacing.WEST) {
            disX = -spacing;
            disXText = disX - spacing;

            disZ = 0.5F;
            disZText = disZ - textWidth;

            rotZ = 1F;
            rotX = 1F;
            frotZ = -1F;
            frotX = 1F;
            fdisX = -1.5F - spacing;
            fdisZ = 1F - 1 / 4F;
        } else if (tile.getDirection() == EnumFacing.SOUTH) {
            disX = 0.5F;
            disXText = disX - textWidth;

            disZ = 1F + spacing;
            disZText = disZ + spacing;

            rotX = 1F;
            fdisZ = -0.5F + spacing;
            fdisX = 1F - 1 / 4F;
        } else if (tile.getDirection() == EnumFacing.EAST) {
            disX = 1F + spacing;
            disXText = disX + spacing;

            disZ = 0.5F;
            disZText = disZ + textWidth;

            rotZ = 1F;
            rotX = -1F;

            frotZ = -1F;
            frotX = -1F;
            fdisX = 2.5F + spacing;
            fdisZ = 0.5F - 1 / 4F;
        }

        GlStateManager.pushMatrix();

        if (type == IType.ITEMS) {
            GlStateManager.translate(x + disX, y + disY, z + disZ);
            GlStateManager.rotate(180F, rotX, rotY, rotZ);
        } else {
            GlStateManager.translate(x + fdisX, y + fdisY, z + fdisZ);
            GlStateManager.rotate(180F, frotX, frotY, frotZ);
        }
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.depthMask(true);
        if (type == IType.ITEMS) {
            GlStateManager.scale(0.4F, -0.4F, -0.015F);
        } else {
            GlStateManager.scale(1F / 32F, 1F / -32F, -0.015F);
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1F, 1F, 1F, 1F);

        final ItemStack itemStack = tile.getItemStack();
        final FluidStack fluidStack = tile.getFluidStack();
        if (type == IType.ITEMS) {
            if (itemStack != null) {
                IBakedModel bakedModel = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(itemStack, null, Minecraft.getMinecraft().player);
                bakedModel = ForgeHooksClient.handleCameraTransforms(bakedModel, ItemCameraTransforms.TransformType.GUI, false);
                Minecraft.getMinecraft().getRenderItem().renderItem(itemStack, bakedModel);
            }
        } else if (type == IType.FLUIDS) {
            if (fluidStack != null) {
                RenderUtils.FluidRenderer fr = new RenderUtils.FluidRenderer(1000, 16, 16);
                fr.draw(Minecraft.getMinecraft(), 0, 0, fluidStack);
            }
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

        GlStateManager.translate(x + disXText, y + disYText, z + disZText);
        GlStateManager.rotate(180F, rotX, rotY, rotZ);
        float size = 0.00450F;
        float factor = 2.0f;
        GlStateManager.scale(size * factor, size * factor, size);

        if ((itemStack != null && type == IType.ITEMS) || (fluidStack != null && type == IType.FLUIDS)) {
            Minecraft.getMinecraft().fontRenderer.drawString(amountString, 0, 0, 0xFFFFFF);
        }

        GlStateManager.popMatrix();

        setLightmapDisabled(false);
    }
}
