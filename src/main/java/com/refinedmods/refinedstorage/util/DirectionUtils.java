package com.refinedmods.refinedstorage.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public final class DirectionUtils {
    private DirectionUtils() {
    }

    public static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity) {
        return Direction.getNearest(
            (float) (entity.getX() - clickedBlock.getX()),
            (float) (entity.getY() - clickedBlock.getY()),
            (float) (entity.getZ() - clickedBlock.getZ())
        );
    }
}
