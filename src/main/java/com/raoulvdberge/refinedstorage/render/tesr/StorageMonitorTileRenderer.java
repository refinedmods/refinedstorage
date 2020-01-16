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
            Vec3d offset = getOffset(direction);

            matrixStack.func_227860_a_();

            double r = Math.PI * (360 - direction.getOpposite().getHorizontalIndex() * 90) / 180d;

            matrixStack.func_227861_a_(0.5D, 0.5D, 0.5D);
            matrixStack.func_227861_a_(offset.getX(), offset.getY(), offset.getZ());
            matrixStack.func_227863_a_(TransformationHelper.quatFromXYZ(new Vector3f(0, (float) r, 0), false));

            matrixStack.func_227860_a_();
            matrixStack.func_227862_a_(0.5F, 0.5F, 0.5F);
            Minecraft.getInstance().getItemRenderer().func_229110_a_(tile.getItemStack(), ItemCameraTransforms.TransformType.FIXED, 0x00F000F0, OverlayTexture.field_229196_a_, matrixStack, renderTypeBuffer);
            matrixStack.func_227865_b_();

            Minecraft.getInstance().fontRenderer.func_228079_a_(
                "Hello",
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

            matrixStack.func_227865_b_();
        }
    }

    public Vec3d getOffset(Direction direction) {
        return new Vec3d(((float) direction.getXOffset() * 0.4F), 0, ((float) direction.getZOffset() * 0.4F));
    }
}
