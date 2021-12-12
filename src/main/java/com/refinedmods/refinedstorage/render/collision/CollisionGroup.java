package com.refinedmods.refinedstorage.render.collision;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CollisionGroup {

    private List<AABB> items = new ArrayList<>();
    private boolean canAccessGui;
    @Nullable
    private Direction direction;

    public CollisionGroup addItem(AABB item) {
        items.add(item);

        return this;
    }

    public List<AABB> getItems() {
        return items;
    }

    public boolean canAccessGui() {
        return canAccessGui;
    }

    public CollisionGroup setCanAccessGui(boolean canAccessGui) {
        this.canAccessGui = canAccessGui;

        return this;
    }

    @Nullable
    public Direction getDirection() {
        return direction;
    }

    public CollisionGroup setDirection(Direction direction) {
        this.direction = direction;

        return this;
    }

}
