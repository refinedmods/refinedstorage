package com.refinedmods.refinedstorage.render.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.TRSRTransformer;
import net.minecraftforge.common.model.TransformationHelper;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class QuadTransformer {
    private static final Map<Direction, TransformationMatrix> SIDE_TRANSFORMS = new EnumMap<>(Direction.class);

    public static List<BakedQuad> getTransformedQuads(IBakedModel model, Direction facing, @Nullable Vector3f translation, BlockState state, Random rand, Direction side) {
        double r = Math.PI * (360 - facing.getOpposite().getHorizontalIndex() * 90) / 180d;

        TransformationMatrix transformation = new TransformationMatrix(translation, TransformationHelper.quatFromXYZ(new Vector3f(0, (float) r, 0), false), null, null);

        ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();

        if (side != null && side.getHorizontalIndex() > -1) {
            int faceOffset = 4 + Direction.NORTH.getHorizontalIndex() - facing.getHorizontalIndex();

            side = Direction.byHorizontalIndex((side.getHorizontalIndex() + faceOffset) % 4);
        }

        for (BakedQuad quad : model.getQuads(state, side, rand, EmptyModelData.INSTANCE)) {
            BakedQuadBuilder builder = new BakedQuadBuilder(quad.func_187508_a());
            TRSRTransformer transformer = new TRSRTransformer(builder, transformation.blockCenterToCorner());

            quad.pipe(transformer);

            quads.add(builder.build());
        }

        return quads.build();
    }
}
