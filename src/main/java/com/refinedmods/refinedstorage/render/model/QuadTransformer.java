package com.refinedmods.refinedstorage.render.model;

import com.google.common.collect.ImmutableList;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.TRSRTransformer;
import net.minecraftforge.common.model.TransformationHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public final class QuadTransformer {
    private QuadTransformer() {
    }

    public static List<BakedQuad> getTransformedQuads(BakedModel model, Direction facing, @Nullable Vector3f translation, BlockState state, Random rand, Direction side) {
        double r = Math.PI * (360 - facing.getOpposite().get2DDataValue() * 90) / 180d;

        Transformation transformation = new Transformation(translation, TransformationHelper.quatFromXYZ(new Vector3f(0, (float) r, 0), false), null, null);

        ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();

        if (side != null && side.get2DDataValue() > -1) {
            int faceOffset = 4 + Direction.NORTH.get2DDataValue() - facing.get2DDataValue();

            side = Direction.from2DDataValue((side.get2DDataValue() + faceOffset) % 4);
        }

        for (BakedQuad quad : model.getQuads(state, side, rand, EmptyModelData.INSTANCE)) {
            BakedQuadBuilder builder = new BakedQuadBuilder(quad.getSprite());
            TRSRTransformer transformer = new TRSRTransformer(builder, transformation.blockCenterToCorner());

            quad.pipe(transformer);

            quads.add(builder.build());
        }

        return quads.build();
    }
}
