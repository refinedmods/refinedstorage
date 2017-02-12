package com.raoulvdberge.refinedstorage.render;

import com.raoulvdberge.refinedstorage.tile.TileStorageMonitor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;

public class TileEntitySpecialRendererStorageMonitor extends TileEntitySpecialRenderer<TileStorageMonitor> {
    @Override
    public void renderTileEntityAt(TileStorageMonitor tile, double x, double y, double z, float partialTicks, int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        GlStateManager.enableRescaleNormal();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.enableLighting();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.scale(0.5F, 0.5F, 0.02F);

        IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(new ItemStack(Blocks.DIRT), null, null);

        model = ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.GUI, false);

        Minecraft.getMinecraft().getRenderItem().renderItem(new ItemStack(Blocks.DIRT), model);

        GlStateManager.popMatrix();
    }
}
