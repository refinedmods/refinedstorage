package com.raoulvdberge.refinedstorage.render.collision.constants;

import com.raoulvdberge.refinedstorage.render.collision.CollisionGroup;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nonnull;

public final class ConstantsCable {
    public static final CollisionGroup CORE = new CollisionGroup().addItem(getBounds(6, 6, 6, 10, 10, 10));
    public static final CollisionGroup NORTH = new CollisionGroup().addItem(getBounds(6, 6, 0, 10, 10, 6));
    public static final CollisionGroup EAST = new CollisionGroup().addItem(getBounds(10, 6, 6, 16, 10, 10));
    public static final CollisionGroup SOUTH = new CollisionGroup().addItem(getBounds(6, 6, 10, 10, 10, 16));
    public static final CollisionGroup WEST = new CollisionGroup().addItem(getBounds(0, 6, 6, 6, 10, 10));
    public static final CollisionGroup UP = new CollisionGroup().addItem(getBounds(6, 10, 6, 10, 16, 10));
    public static final CollisionGroup DOWN = new CollisionGroup().addItem(getBounds(6, 0, 6, 10, 6, 10));

    public static final CollisionGroup HOLDER_NORTH = new CollisionGroup().addItem(getHolderBounds(EnumFacing.NORTH));
    public static final CollisionGroup HOLDER_EAST = new CollisionGroup().addItem(getHolderBounds(EnumFacing.EAST));
    public static final CollisionGroup HOLDER_SOUTH = new CollisionGroup().addItem(getHolderBounds(EnumFacing.SOUTH));
    public static final CollisionGroup HOLDER_WEST = new CollisionGroup().addItem(getHolderBounds(EnumFacing.WEST));
    public static final CollisionGroup HOLDER_UP = new CollisionGroup().addItem(getHolderBounds(EnumFacing.UP));
    public static final CollisionGroup HOLDER_DOWN = new CollisionGroup().addItem(getHolderBounds(EnumFacing.DOWN));


    public static AxisAlignedBB getBounds(int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        return new AxisAlignedBB((float) fromX / 16F, (float) fromY / 16F, (float) fromZ / 16F, (float) toX / 16F, (float) toY / 16F, (float) toZ / 16F);
    }

    @Nonnull
    public static AxisAlignedBB getCoverBounds(EnumFacing side) {
        switch (side) {
            case DOWN:
                return getBounds(0, 0, 0, 16, 2, 16);
            case UP:
                return getBounds(0, 14, 0, 16, 16, 16);
            case NORTH:
                return getBounds(0, 0, 0, 16, 16, 2);
            case SOUTH:
                return getBounds(0, 0, 14, 16, 16, 16);
            case WEST:
                return getBounds(0, 0, 0, 2, 16, 16);
            case EAST:
                return getBounds(14, 0, 0, 16, 16, 16);
            default:
                return null;
        }
    }

    @Nonnull
    public static AxisAlignedBB getHolderBounds(EnumFacing side) {
        switch (side) {
            case DOWN:
                return getBounds(7, 2, 7, 9, 6, 9);
            case UP:
                return getBounds(7, 10, 7, 9, 14, 9);
            case NORTH:
                return getBounds(7, 7, 2, 9, 9, 6);
            case SOUTH:
                return getBounds(7, 7, 10, 9, 9, 14);
            case WEST:
                return getBounds(2, 7, 7, 6, 9, 9);
            case EAST:
                return getBounds(10, 7, 7, 14, 9, 9);
            default:
                return null;
        }
    }
}
