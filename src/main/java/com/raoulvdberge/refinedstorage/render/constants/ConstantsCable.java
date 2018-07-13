package com.raoulvdberge.refinedstorage.render.constants;

import com.raoulvdberge.refinedstorage.render.collision.CollisionGroup;
import com.raoulvdberge.refinedstorage.util.CollisionUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nonnull;

public final class ConstantsCable {
    public static final CollisionGroup CORE = new CollisionGroup().addItem(CollisionUtils.getBounds(6, 6, 6, 10, 10, 10));
    public static final CollisionGroup NORTH = new CollisionGroup().addItem(CollisionUtils.getBounds(6, 6, 0, 10, 10, 6));
    public static final CollisionGroup EAST = new CollisionGroup().addItem(CollisionUtils.getBounds(10, 6, 6, 16, 10, 10));
    public static final CollisionGroup SOUTH = new CollisionGroup().addItem(CollisionUtils.getBounds(6, 6, 10, 10, 10, 16));
    public static final CollisionGroup WEST = new CollisionGroup().addItem(CollisionUtils.getBounds(0, 6, 6, 6, 10, 10));
    public static final CollisionGroup UP = new CollisionGroup().addItem(CollisionUtils.getBounds(6, 10, 6, 10, 16, 10));
    public static final CollisionGroup DOWN = new CollisionGroup().addItem(CollisionUtils.getBounds(6, 0, 6, 10, 6, 10));

    public static final CollisionGroup HOLDER_NORTH = new CollisionGroup().addItem(getHolderBounds(EnumFacing.NORTH));
    public static final CollisionGroup HOLDER_EAST = new CollisionGroup().addItem(getHolderBounds(EnumFacing.EAST));
    public static final CollisionGroup HOLDER_SOUTH = new CollisionGroup().addItem(getHolderBounds(EnumFacing.SOUTH));
    public static final CollisionGroup HOLDER_WEST = new CollisionGroup().addItem(getHolderBounds(EnumFacing.WEST));
    public static final CollisionGroup HOLDER_UP = new CollisionGroup().addItem(getHolderBounds(EnumFacing.UP));
    public static final CollisionGroup HOLDER_DOWN = new CollisionGroup().addItem(getHolderBounds(EnumFacing.DOWN));

    @Nonnull
    public static AxisAlignedBB getCoverBounds(EnumFacing side) {
        switch (side) {
            case DOWN:
                return CollisionUtils.getBounds(0, 0, 0, 16, 2, 16);
            case UP:
                return CollisionUtils.getBounds(0, 14, 0, 16, 16, 16);
            case NORTH:
                return CollisionUtils.getBounds(0, 0, 0, 16, 16, 2);
            case SOUTH:
                return CollisionUtils.getBounds(0, 0, 14, 16, 16, 16);
            case WEST:
                return CollisionUtils.getBounds(0, 0, 0, 2, 16, 16);
            case EAST:
                return CollisionUtils.getBounds(14, 0, 0, 16, 16, 16);
            default:
                return null;
        }
    }

    @Nonnull
    public static AxisAlignedBB getHolderBounds(EnumFacing side) {
        switch (side) {
            case DOWN:
                return CollisionUtils.getBounds(7, 2, 7, 9, 6, 9);
            case UP:
                return CollisionUtils.getBounds(7, 10, 7, 9, 14, 9);
            case NORTH:
                return CollisionUtils.getBounds(7, 7, 2, 9, 9, 6);
            case SOUTH:
                return CollisionUtils.getBounds(7, 7, 10, 9, 9, 14);
            case WEST:
                return CollisionUtils.getBounds(2, 7, 7, 6, 9, 9);
            case EAST:
                return CollisionUtils.getBounds(10, 7, 7, 14, 9, 9);
            default:
                return null;
        }
    }
}
