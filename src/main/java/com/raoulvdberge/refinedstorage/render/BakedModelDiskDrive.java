package com.raoulvdberge.refinedstorage.render;

import com.raoulvdberge.refinedstorage.block.BlockBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.annotation.Nullable;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BakedModelDiskDrive implements IBakedModel {
    private IBakedModel base;
    private Map<EnumFacing, IBakedModel> models = new HashMap<>();
    private Map<EnumFacing, List<IBakedModel>> disks = new HashMap<>();

    public BakedModelDiskDrive(IBakedModel base, IBakedModel disk) {
        this.base = base;

        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            models.put(facing, new BakedModelTRSR(base, facing));

            disks.put(facing, new ArrayList<>());

            for (int y = 0; y < 4; ++y) {
                for (int x = 0; x < 2; ++x) {
                    BakedModelTRSR model = new BakedModelTRSR(disk, facing);

                    Vector3f trans = model.transformation.getTranslation();

                    if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
                        trans.x += (((float) x * 7F) / 16F) * (facing == EnumFacing.NORTH ? -1 : 1);
                    } else if (facing == EnumFacing.EAST || facing == EnumFacing.WEST) {
                        trans.z += (((float) x * 7F) / 16F) * (facing == EnumFacing.EAST ? -1 : 1);
                    }

                    trans.y -= ((float) y * 3F) / 16F;

                    model.transformation = new TRSRTransformation(trans, model.transformation.getLeftRot(), model.transformation.getScale(), model.transformation.getRightRot());

                    disks.get(facing).add(model);
                }
            }
        }
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (state == null) {
            return base.getQuads(state, side, rand);
        }

        EnumFacing facing = state.getValue(BlockBase.DIRECTION);

        List<BakedQuad> quads = models.get(facing).getQuads(state, side, rand);

        for (int i = 0; i < 8; ++i) {
            quads.addAll(disks.get(facing).get(i).getQuads(state, side, rand));
        }

        return quads;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return base.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return base.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return base.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return base.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return base.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return base.getOverrides();
    }
}
