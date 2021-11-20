package com.refinedmods.refinedstorage.block.shape;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.shapes.VoxelShape;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class ShapeCache {
    private static final Map<BlockState, VoxelShape> CACHE = new HashMap<>();

    private ShapeCache() {
    }

    public static VoxelShape getOrCreate(BlockState state, Function<BlockState, VoxelShape> shapeFactory) {
        return CACHE.computeIfAbsent(state, shapeFactory);
    }
}
