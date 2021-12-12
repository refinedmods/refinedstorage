package com.refinedmods.refinedstorage.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;

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
