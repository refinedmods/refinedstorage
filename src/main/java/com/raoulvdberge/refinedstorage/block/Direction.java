package com.raoulvdberge.refinedstorage.block;

import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import scala.actors.threadpool.Arrays;

public enum Direction {
    ANY(EnumFacing.VALUES),
    ANY_FACE_PLAYER(EnumFacing.VALUES),
    HORIZONTAL(EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST);

    private final PropertyDirection property;

    Direction(EnumFacing... allowed) {
        this.property = PropertyDirection.create("direction", Arrays.asList(allowed));
    }

    public PropertyDirection getProperty() {
        return property;
    }

    public EnumFacing getFrom(EnumFacing facing, BlockPos pos, EntityLivingBase entity) {
        switch (this) {
            case ANY:
                return facing.getOpposite();
            case ANY_FACE_PLAYER:
                return EnumFacing.getDirectionFromEntityLiving(pos, entity);
            case HORIZONTAL:
                return entity.getHorizontalFacing().getOpposite();
            default:
                return null;
        }
    }

    public EnumFacing cycle(EnumFacing previous) {
        switch (this) {
            case ANY:
            case ANY_FACE_PLAYER:
                return previous.ordinal() + 1 >= EnumFacing.VALUES.length ? EnumFacing.VALUES[0] : EnumFacing.VALUES[previous.ordinal() + 1];
            case HORIZONTAL:
                return previous.rotateYCCW();
            default:
                return previous;
        }
    }
}
