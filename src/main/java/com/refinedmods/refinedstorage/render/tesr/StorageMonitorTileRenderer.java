package com.refinedmods.refinedstorage.render.tesr;

import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.block.StorageMonitorBlock;
import com.refinedmods.refinedstorage.tile.StorageMonitorTile;
import com.refinedmods.refinedstorage.tile.config.IType;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.common.model.TransformationHelper;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

public class StorageMonitorTileRenderer extends TileEntityRenderer<StorageMonitorTile> {
    public StorageMonitorTileRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(StorageMonitorTile tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int i, int i1) {
        Direction direction = Direction.NORTH;

        BlockState state = tile.getWorld().getBlockState(tile.getPos());
        if (state.getBlock() instanceof StorageMonitorBlock) {
            direction = state.get(RSBlocks.STORAGE_MONITOR.get().getDirection().getProperty());
        }

        final int light = WorldRenderer.getCombinedLight(tile.getWorld(), tile.getPos().add(direction.getDirectionVec()));
        final float rotation = (float) (Math.PI * (360 - direction.getOpposite().getHorizontalIndex() * 90) / 180d);

        final int type = tile.getStackType();

        final ItemStack itemStack = tile.getItemStack();
        final FluidStack fluidStack = tile.getFluidStack();

        if (type == IType.ITEMS && itemStack != null && !itemStack.isEmpty()) {
            renderItem(matrixStack, renderTypeBuffer, direction, rotation, light, itemStack);

            String amount = API.instance().getQuantityFormatter().formatWithUnits(tile.getAmount());

            renderText(matrixStack, renderTypeBuffer, direction, rotation, light, amount);
        } else if (type == IType.FLUIDS && fluidStack != null && !fluidStack.isEmpty()) {
            renderFluid(matrixStack, renderTypeBuffer, direction, rotation, light, fluidStack);

            String amount = API.instance().getQuantityFormatter().formatInBucketFormWithOnlyTrailingDigitsIfZero(tile.getAmount());

            renderText(matrixStack, renderTypeBuffer, direction, rotation, light, amount);
        }
    }

    private void renderText(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, Direction direction, float rotation, int light, String amount) {
        matrixStack.push();

        float stringOffset = -(Minecraft.getInstance().fontRenderer.getStringWidth(amount) * 0.01F) / 2F;

        matrixStack.translate(0.5D, 0.5D, 0.5D);
        matrixStack.translate(
                ((float) direction.getXOffset() * 0.501F) + (direction.getZOffset() * stringOffset),
                -0.275,
                ((float) direction.getZOffset() * 0.501F) - (direction.getXOffset() * stringOffset)
        );

        matrixStack.rotate(TransformationHelper.quatFromXYZ(new Vector3f(direction.getXOffset() * 180, 0, direction.getZOffset() * 180), true));
        matrixStack.rotate(TransformationHelper.quatFromXYZ(new Vector3f(0, rotation, 0), false));

        matrixStack.scale(0.01F, 0.01F, 0.01F);

        Minecraft.getInstance().fontRenderer.renderString(
                amount,
                0,
                0,
                -1,
                false,
                matrixStack.getLast().getMatrix(),
                renderTypeBuffer,
                false,
                0,
                light
        );

        matrixStack.pop();
    }

    @SuppressWarnings("deprecation")
    private void renderItem(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, Direction direction, float rotation, int light, ItemStack itemStack) {
        matrixStack.push();

        // Put it in the middle, outwards, and facing the correct direction
        matrixStack.translate(0.5D, 0.5D, 0.5D);
        matrixStack.translate((float) direction.getXOffset() * 0.501F, 0, (float) direction.getZOffset() * 0.501F);
        matrixStack.rotate(TransformationHelper.quatFromXYZ(new Vector3f(0, rotation, 0), false));

        // Make it look "flat"
        matrixStack.scale(0.5F, -0.5F, -0.00005f);

        // Fix rotation after making it look flat
        matrixStack.rotate(TransformationHelper.quatFromXYZ(new Vector3f(0, 0, 180), true));

        IBakedModel itemModel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(itemStack, null, null);
        boolean render3D = itemModel.isGui3d();

        if (render3D) {
            RenderHelper.setupGui3DDiffuseLighting();
        } else {
            RenderHelper.setupGuiFlatDiffuseLighting();
        }

        matrixStack.getLast().getNormal().set(Matrix3f.makeScaleMatrix(1, -1, 1));
        Minecraft.getInstance().getItemRenderer().renderItem(
                itemStack,
                ItemCameraTransforms.TransformType.GUI,
                false,
                matrixStack,
                renderTypeBuffer,
                light,
                OverlayTexture.NO_OVERLAY,
                itemModel
        );

        matrixStack.pop();
    }

    private void renderFluid(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, Direction direction, float rotation, int light, FluidStack fluidStack) {
        matrixStack.push();

        matrixStack.translate(0.5D, 0.5D, 0.5D);
        matrixStack.translate((float) direction.getXOffset() * 0.51F, 0.5F, (float) direction.getZOffset() * 0.51F);
        matrixStack.rotate(TransformationHelper.quatFromXYZ(new Vector3f(0, rotation, 0), false));

        matrixStack.scale(0.5F, 0.5F, 0.5F);

        final Fluid fluid = fluidStack.getFluid();
        final FluidAttributes attributes = fluid.getAttributes();
        final ResourceLocation fluidStill = attributes.getStillTexture(fluidStack);
        final TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(fluidStill);
        final int fluidColor = attributes.getColor(fluidStack);

        final IVertexBuilder buffer = renderTypeBuffer.getBuffer(RenderType.getText(sprite.getAtlasTexture().getTextureLocation()));

        final int colorRed = fluidColor >> 16 & 0xFF;
        final int colorGreen = fluidColor >> 8 & 0xFF;
        final int colorBlue = fluidColor & 0xFF;
        final int colorAlpha = fluidColor >> 24 & 0xFF;

        buffer.pos(matrixStack.getLast().getMatrix(), -0.5F, -0.5F, 0F)
                .color(colorRed, colorGreen, colorBlue, colorAlpha)
                .tex(sprite.getMinU(), sprite.getMinV())
                .lightmap(light)
                .endVertex();
        buffer.pos(matrixStack.getLast().getMatrix(), 0.5F, -0.5F, 0F)
                .color(colorRed, colorGreen, colorBlue, colorAlpha)
                .tex(sprite.getMaxU(), sprite.getMinV())
                .lightmap(light)
                .endVertex();
        buffer.pos(matrixStack.getLast().getMatrix(), 0.5F, -1.5F, 0F)
                .color(colorRed, colorGreen, colorBlue, colorAlpha)
                .tex(sprite.getMaxU(), sprite.getMaxV())
                .lightmap(light)
                .endVertex();
        buffer.pos(matrixStack.getLast().getMatrix(), -0.5F, -1.5F, 0F)
                .color(colorRed, colorGreen, colorBlue, colorAlpha)
                .tex(sprite.getMinU(), sprite.getMaxV())
                .lightmap(light)
                .endVertex();

        matrixStack.pop();
    }
}
