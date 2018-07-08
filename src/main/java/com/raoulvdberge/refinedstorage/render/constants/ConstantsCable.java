package com.raoulvdberge.refinedstorage.render.constants;

import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

public final class ConstantsCable {
    public static final AxisAlignedBB CORE_AABB = RenderUtils.getBounds(6, 6, 6, 10, 10, 10);
    public static final AxisAlignedBB NORTH_AABB = RenderUtils.getBounds(6, 6, 0, 10, 10, 6);
    public static final AxisAlignedBB EAST_AABB = RenderUtils.getBounds(10, 6, 6, 16, 10, 10);
    public static final AxisAlignedBB SOUTH_AABB = RenderUtils.getBounds(6, 6, 10, 10, 10, 16);
    public static final AxisAlignedBB WEST_AABB = RenderUtils.getBounds(0, 6, 6, 6, 10, 10);
    public static final AxisAlignedBB UP_AABB = RenderUtils.getBounds(6, 10, 6, 10, 16, 10);
    public static final AxisAlignedBB DOWN_AABB = RenderUtils.getBounds(6, 0, 6, 10, 6, 10);

    public static final AxisAlignedBB HOLDER_NORTH_AABB = RenderUtils.getBounds(7, 7, 2, 9, 9, 6);
    public static final AxisAlignedBB HOLDER_EAST_AABB = RenderUtils.getBounds(10, 7, 7, 14, 9, 9);
    public static final AxisAlignedBB HOLDER_SOUTH_AABB = RenderUtils.getBounds(7, 7, 10, 9, 9, 14);
    public static final AxisAlignedBB HOLDER_WEST_AABB = RenderUtils.getBounds(2, 7, 7, 6, 9, 9);
    public static final AxisAlignedBB HOLDER_UP_AABB = RenderUtils.getBounds(7, 10, 7, 9, 14, 9);
    public static final AxisAlignedBB HOLDER_DOWN_AABB = RenderUtils.getBounds(7, 2, 7, 9, 6, 9);

    public static Pair<Vector3f, Vector3f> getCoverBounds(EnumFacing side) {
        switch (side) {
            case DOWN:
                return Pair.of(
                    new Vector3f(0, 0, 0),
                    new Vector3f(16, 2, 16)
                );
            case UP:
                return Pair.of(
                    new Vector3f(0, 14, 0),
                    new Vector3f(16, 16, 16)
                );
            case NORTH:
                return Pair.of(
                    new Vector3f(0, 0, 0),
                    new Vector3f(16, 16, 2)
                );
            case SOUTH:
                return Pair.of(
                    new Vector3f(16, 0, 16),
                    new Vector3f(0, 16, 14)
                );
            case WEST:
                return Pair.of(
                    new Vector3f(0, 0, 0),
                    new Vector3f(2, 16, 16)
                );
            case EAST:
                return Pair.of(
                    new Vector3f(14, 0, 0),
                    new Vector3f(16, 16, 16)
                );
            default:
                return null;
        }
    }

    public static Pair<Vector3f, Vector3f> getHolderBounds(EnumFacing side) {
        switch (side) {
            case DOWN:
                return Pair.of(
                    new Vector3f(7, 2, 7),
                    new Vector3f(9, 6, 9)
                );
            case UP:
                return Pair.of(
                    new Vector3f(7, 10, 7),
                    new Vector3f(9, 14, 9)
                );
            case NORTH:
                return Pair.of(
                    new Vector3f(7, 7, 2),
                    new Vector3f(9, 9, 6)
                );
            case SOUTH:
                return Pair.of(
                    new Vector3f(7, 7, 10),
                    new Vector3f(9, 9, 14)
                );
            case WEST:
                return Pair.of(
                    new Vector3f(2, 7, 7),
                    new Vector3f(6, 9, 9)
                );
            case EAST:
                return Pair.of(
                    new Vector3f(10, 7, 7),
                    new Vector3f(14, 9, 9)
                );
            default:
                return null;
        }
    }
}
