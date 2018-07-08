package com.raoulvdberge.refinedstorage.render.collision.constants;

import com.raoulvdberge.refinedstorage.render.collision.CollisionGroup;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

public final class ConstantsCable {
    public static final CollisionGroup CORE = new CollisionGroup().addItem(RenderUtils.getBounds(6, 6, 6, 10, 10, 10));
    public static final CollisionGroup NORTH = new CollisionGroup().addItem(RenderUtils.getBounds(6, 6, 0, 10, 10, 6));
    public static final CollisionGroup EAST = new CollisionGroup().addItem(RenderUtils.getBounds(10, 6, 6, 16, 10, 10));
    public static final CollisionGroup SOUTH = new CollisionGroup().addItem(RenderUtils.getBounds(6, 6, 10, 10, 10, 16));
    public static final CollisionGroup WEST = new CollisionGroup().addItem(RenderUtils.getBounds(0, 6, 6, 6, 10, 10));
    public static final CollisionGroup UP = new CollisionGroup().addItem(RenderUtils.getBounds(6, 10, 6, 10, 16, 10));
    public static final CollisionGroup DOWN = new CollisionGroup().addItem(RenderUtils.getBounds(6, 0, 6, 10, 6, 10));

    public static final CollisionGroup HOLDER_NORTH = new CollisionGroup().addItem(getHolderBoundsAabb(EnumFacing.NORTH));
    public static final CollisionGroup HOLDER_EAST = new CollisionGroup().addItem(getHolderBoundsAabb(EnumFacing.EAST));
    public static final CollisionGroup HOLDER_SOUTH = new CollisionGroup().addItem(getHolderBoundsAabb(EnumFacing.SOUTH));
    public static final CollisionGroup HOLDER_WEST = new CollisionGroup().addItem(getHolderBoundsAabb(EnumFacing.WEST));
    public static final CollisionGroup HOLDER_UP = new CollisionGroup().addItem(getHolderBoundsAabb(EnumFacing.UP));
    public static final CollisionGroup HOLDER_DOWN = new CollisionGroup().addItem(getHolderBoundsAabb(EnumFacing.DOWN));

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
                    new Vector3f(0, 0, 14),
                    new Vector3f(16, 16, 16)
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

    private static AxisAlignedBB getHolderBoundsAabb(EnumFacing side) {
        Pair<Vector3f, Vector3f> bounds = getHolderBounds(side);

        return RenderUtils.getBounds(
            (int) bounds.getLeft().getX(), (int) bounds.getLeft().getY(), (int) bounds.getLeft().getZ(),
            (int) bounds.getRight().getX(), (int) bounds.getRight().getY(), (int) bounds.getRight().getZ()
        );
    }
}
