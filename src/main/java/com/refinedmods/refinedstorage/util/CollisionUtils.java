package com.refinedmods.refinedstorage.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class CollisionUtils {
    private CollisionUtils() {
    }

    public static boolean isInBounds(VoxelShape shape, BlockPos pos, Vec3 hit) {
        AABB aabb = shape.bounds().move(pos);

        return hit.x >= aabb.minX
            && hit.x <= aabb.maxX
            && hit.y >= aabb.minY
            && hit.y <= aabb.maxY
            && hit.z >= aabb.minZ
            && hit.z <= aabb.maxZ;
    }

    public static AABB getBounds(int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        return new AABB((float) fromX / 16F, (float) fromY / 16F, (float) fromZ / 16F, (float) toX / 16F, (float) toY / 16F, (float) toZ / 16F);
    }

}
