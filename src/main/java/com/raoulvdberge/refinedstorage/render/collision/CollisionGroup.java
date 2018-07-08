package com.raoulvdberge.refinedstorage.render.collision;

import net.minecraft.util.math.AxisAlignedBB;

import java.util.ArrayList;
import java.util.List;

public class CollisionGroup {
    private List<AxisAlignedBB> items = new ArrayList<>();
    private boolean canAccessGui;

    public CollisionGroup addItem(AxisAlignedBB item) {
        items.add(item);

        return this;
    }

    public List<AxisAlignedBB> getItems() {
        return items;
    }

    public boolean canAccessGui() {
        return canAccessGui;
    }

    public CollisionGroup setCanAccessGui(boolean canAccessGui) {
        this.canAccessGui = canAccessGui;

        return this;
    }
}
