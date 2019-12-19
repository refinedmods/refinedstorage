package com.raoulvdberge.refinedstorage.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public final class DirectionUtils {
    public static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity) {
        return Direction.getFacingFromVector(
            (float) (entity.getPosition().getX() - clickedBlock.getX()),
            (float) (entity.getPosition().getY() - clickedBlock.getY()),
            (float) (entity.getPosition().getZ() - clickedBlock.getZ())
        );
    }
}
