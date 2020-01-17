package com.raoulvdberge.refinedstorage.render.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.block.StorageMonitorBlock;
import com.raoulvdberge.refinedstorage.tile.StorageMonitorTile;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.model.TransformationHelper;

public class StorageMonitorTileRenderer extends TileEntityRenderer<StorageMonitorTile> {
    public StorageMonitorTileRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void func_225616_a_(StorageMonitorTile tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int i, int i1) {
        Direction direction = Direction.NORTH;

        BlockState state = tile.getWorld().getBlockState(tile.getPos());
        if (state.getBlock() instanceof StorageMonitorBlock) {
            direction = state.get(RSBlocks.STORAGE_MONITOR.getDirection().getProperty());
        }

        String amount = API.instance().getQuantityFormatter().formatWithUnits(tile.getAmount());

        if (tile.getItemStack() != null) {
            // Push
            matrixStack.func_227860_a_();

            double r = Math.PI * (360 - direction.getOpposite().getHorizontalIndex() * 90) / 180d;

            matrixStack.func_227861_a_(0.5D, 0.5D, 0.5D);
            matrixStack.func_227861_a_((float) direction.getXOffset() * 0.4F, 0, (float) direction.getZOffset() * 0.4F);
            matrixStack.func_227863_a_(TransformationHelper.quatFromXYZ(new Vector3f(0, (float) r, 0), false));

            matrixStack.func_227860_a_();
            matrixStack.func_227862_a_(0.5F, 0.5F, 0.5F);

            Minecraft.getInstance().getItemRenderer().func_229110_a_(
                tile.getItemStack(),
                ItemCameraTransforms.TransformType.FIXED,
                0x00F000F0,
                OverlayTexture.field_229196_a_,
                matrixStack,
                renderTypeBuffer
            );

            matrixStack.func_227865_b_();

            // Pop
            matrixStack.func_227865_b_();

            // Push
            matrixStack.func_227860_a_();

            float stringOffset = -(Minecraft.getInstance().fontRenderer.getStringWidth(amount) * 0.01F) / 2F;

            matrixStack.func_227861_a_(0.5D, 0.5D, 0.5D);
            matrixStack.func_227861_a_(
                ((float) direction.getXOffset() * 0.5F) + (direction.getZOffset() * stringOffset),
                -0.225,
                ((float) direction.getZOffset() * 0.5F) - (direction.getXOffset() * stringOffset)
            );

            matrixStack.func_227863_a_(TransformationHelper.quatFromXYZ(new Vector3f(direction.getXOffset() * 180, 0, direction.getZOffset() * 180), true));
            matrixStack.func_227863_a_(TransformationHelper.quatFromXYZ(new Vector3f(0, (float) r, 0), false));

            matrixStack.func_227862_a_(0.01F, 0.01F, 0.01F);

            Minecraft.getInstance().fontRenderer.func_228079_a_(
                amount,
                0,
                0,
                -1,
                false,
                matrixStack.func_227866_c_().func_227870_a_(),
                renderTypeBuffer,
                false,
                0,
                15728880
            );

            // Pop
            matrixStack.func_227865_b_();
        }
    }
}
