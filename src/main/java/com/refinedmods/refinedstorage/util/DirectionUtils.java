package com.refinedmods.refinedstorage.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public final class DirectionUtils {
    public static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity) {
        return Direction.getFacingFromVector(
            (float) (entity.getPosX() - clickedBlock.getX()),
            (float) (entity.getPosY() - clickedBlock.getY()),
            (float) (entity.getPosZ() - clickedBlock.getZ())
        );
    }
}
