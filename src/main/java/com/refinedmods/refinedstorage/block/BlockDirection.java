package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.util.DirectionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

import java.util.Arrays;

public enum BlockDirection {
    NONE(),
    ANY(Direction.values()),
    ANY_FACE_PLAYER(Direction.values()),
    HORIZONTAL(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);

    private final DirectionProperty property;

    BlockDirection(Direction... allowed) {
        this.property = DirectionProperty.create("direction", Arrays.asList(allowed));
    }

    public DirectionProperty getProperty() {
        return property;
    }

    public Direction getFrom(Direction facing, BlockPos pos, LivingEntity entity) {
        switch (this) {
            case ANY:
                return facing.getOpposite();
            case ANY_FACE_PLAYER:
                return DirectionUtils.getFacingFromEntity(pos, entity);
            case HORIZONTAL:
                return entity.getDirection().getOpposite();
            default:
                throw new IllegalStateException("Unknown direction type");
        }
    }

    public Direction cycle(Direction previous) {
        switch (this) {
            case ANY:
            case ANY_FACE_PLAYER:
                return previous.ordinal() + 1 >= Direction.values().length ? Direction.values()[0] : Direction.values()[previous.ordinal() + 1];
            case HORIZONTAL:
                return previous.getCounterClockWise();
            default:
                throw new IllegalStateException("Unknown direction type");
        }
    }
}
