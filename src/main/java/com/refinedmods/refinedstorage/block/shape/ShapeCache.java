package com.refinedmods.refinedstorage.block.shape;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

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
